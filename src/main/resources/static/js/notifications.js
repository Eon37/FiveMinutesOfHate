//PUSH notifications
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('/service-worker.js')
    .then(function (registration) {
      console.log('ServiceWorker registration successful');
    })
    .then(initializeState)
    .catch(function (error) {
      console.error('ServiceWorker registration failed:', error);
    });
} else {
  console.warn('Service workers are not supported in this browser.');
}

function initializeState() {
  if (!('showNotification' in ServiceWorkerRegistration.prototype)) {
    console.warn('Notifications aren\'t supported.');
    return;
  }

  if (Notification.permission === 'denied') {
    console.warn('The user has notifications blocked.');
    return;
  }

  if (!('PushManager' in window)) {
    console.warn('Push messaging isn\'t supported.');
    return;
  }

  navigator.serviceWorker.ready.then(function (serviceWorkerRegistration) {
    serviceWorkerRegistration.pushManager.getSubscription().then(function (subscription) {
      console.log("subscription?", subscription)
      if (!subscription) {
        subscribe();
        return;
      }

      sendSubscriptionToServer(subscription);
    })
      .catch(function (err) {
        console.warn('Error during getSubscription()', err);
      });
  });
}

function subscribe() {
  navigator.serviceWorker.ready.then(function (serviceWorkerRegistration) {
    // When .subscribe() is invoked, a notification will be shown in the
    // user's browser, asking the user to accept push notifications from
    // <yoursite.com>. This is why it is async and requires a catch.
    serviceWorkerRegistration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: consts.notificationAppServerKey
    })
      .then(function (subscription) {
        return sendSubscriptionToServer(subscription);
      })
      .catch(function (e) {
        if (Notification.permission === 'denied') {
          console.warn('Permission for Notifications was denied');
        } else {
          console.error('Unable to subscribe to push.', e);
        }
      });
  });
}

function sendSubscriptionToServer(subscription) {
  var key = subscription.getKey ? subscription.getKey('p256dh') : '';
  var auth = subscription.getKey ? subscription.getKey('auth') : '';
  if (key === '' || auth === '') {
    return;
  }

  // This example uses the new fetch API. This is not supported in all
  // browsers yet.
  return fetch('/api/notifications/subscribe', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      endpoint: subscription.endpoint,
      // Take byte[] and turn it into a base64 encoded string suitable for
      // POSTing to a server over HTTP
      key: key ? btoa(String.fromCharCode.apply(null, new Uint8Array(key))) : '',
      auth: auth ? btoa(String.fromCharCode.apply(null, new Uint8Array(auth))) : ''
    })
  });
}

//TOAST notifications
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);
const clientId = getClientId();

stompClient.connect({}, function () {
  stompClient.subscribe('/topic/posts', function (message) {
    const body = JSON.parse(message.body);

    if (clientId !== body.clientId) {
      showNotification(body.title, body.text, body.id);
    }
  });

  stompClient.subscribe('/topic/comments', function (message) {
    const body = JSON.parse(message.body);

    if (clientId !== body.clientId) {
      showNotification(body.title, body.text, body.id);
    }
  });
});

function showNotification(title, body, postId) {
  showToast(title, body, postId);

  const audio = new Audio('/sounds/notification.mp3');
  audio.play().catch(e => {
    console.warn('Audio play prevented:', e);
  });
}

let toastCount = 0;

function showToast(title, body, postId) {
  const toastContainer = document.getElementById('toast-container');
  const toastCountBadge = document.getElementById("toast-count");

  const toast = document.createElement('div');
  toast.className = 'toast';

  toast.innerHTML = `<strong>${title}</strong><br>
                            ${body}<br>
                            <button onclick="location.assign('/#post-' + ${postId}); location.reload();" class="toast-button">${consts.notificationButton}</button>`;
  toastContainer.appendChild(toast);
  toastCount++;
  toastCountBadge.textContent = toastCount;

  setTimeout(() => {
    toast.remove();
    toastCount--;
    document.getElementById("toast-count").textContent = toastCount;
  }, 60000);
}

function toggleToasts() {
  const container = document.getElementById("toast-container");
  const isHidden = container.classList.contains("hidden");

  if (isHidden) {
    container.classList.remove("hidden");
  } else {
    container.classList.add("hidden");
  }
}

function getClientId() {
  const match = document.cookie.match(/clientId=([^;]+)/);
  return match ? match[1] : null;
}