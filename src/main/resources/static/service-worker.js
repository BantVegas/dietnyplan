self.addEventListener('install', event => {
    console.log('[ServiceWorker] Inštalovaný');
});

self.addEventListener('fetch', event => {
    event.respondWith(fetch(event.request));
});
