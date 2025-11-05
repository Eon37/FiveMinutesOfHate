self.addEventListener('install', (event) => {
  console.log('[Service Worker] Installed');
  // Activate new service worker immediately
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  console.log('[Service Worker] Activated');
  // Take control of open clients
  event.waitUntil(self.clients.claim());
});

self.addEventListener('push', function (event) {
  const data = event.data.json();
  console.log('[Service Worker] Push data received:', data);
  event.waitUntil(
      self.registration.showNotification(data.title, {
        body: data.body,
        icon: 'icons/icon.png',
        requireInteraction: false
      })
  );
});

self.addEventListener('notificationclick', function (event) {
  event.notification.close(); // Close the notification

  event.waitUntil(
      (async () => {
        const allClients = await clients.matchAll({
          includeUncontrolled: true,
          type: 'window'
        });

        for (const client of allClients) {
          if (client.url === 'https://five-minutes-of-hate.fly.dev/' && 'focus' in client) {
            client.focus();
            // Send a message to the client to trigger reload
            client.postMessage({ action: 'reload' });
            return;
          }
        }

        // If no existing tab found — open a new one
        await clients.openWindow('https://five-minutes-of-hate.fly.dev/');
      })()
    );
});