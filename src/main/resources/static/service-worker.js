self.addEventListener('push', function (event) {
  const data = event.data.json();
  console.log('[Service Worker] Push data received:', data);
  event.waitUntil(
      self.registration.showNotification(data.title, {
        body: data.body,
        icon: '/icon.png',
        requireInteraction: false
      })
  );
});

self.addEventListener('notificationclick', function (event) {
  event.notification.close(); // Close the notification

  // Focus/open the URL
  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then(clientList => {
      // Check if a tab is already open with the URL
      for (const client of clientList) {
        if (client.url === 'https://five-minutes-of-hate.fly.dev/' && 'focus' in client) {
          return client.focus();
        }
      }
      // Otherwise, open a new tab
      if (clients.openWindow) {
        return clients.openWindow('https://five-minutes-of-hate.fly.dev/');
      }
    })
  );
});