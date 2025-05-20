package com.eon37_dev.tmh.services;

import com.eon37_dev.tmh.model.PushSubscription;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
  static {
    if (Security.getProvider("BC") == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }
  @Value("${vapid.public.key}")
  private String vapidPublicKey;
  @Value("${vapid.private.key}")
  private String vapidPrivateKey;
  @Value("${vapid.subject}")
  private String vapidSubject;
  private final Map<String, PushSubscription> subscriptions = new ConcurrentHashMap<>();

  public void addSubscription(String clientId, PushSubscription pushSubscription) {
    if (clientId == null) return;
    Optional<String> clientIdChanged = subscriptions.entrySet().stream()
            .filter(sub -> sub.getValue().getEndpoint().equals(pushSubscription.getEndpoint()))
            .findFirst()
            .map(Map.Entry::getKey);

    clientIdChanged.filter(id -> !clientId.equals(id)).ifPresent(subscriptions::remove);

    subscriptions.put(clientId, pushSubscription);
  }

  public void sendNotificationToAllExcept(String title, String body, String exceptClientId) {
    subscriptions.entrySet().stream()
            .filter(entry -> !exceptClientId.equals(entry.getKey()))
            .forEach(subscriptionEntry -> {
              try {
                String payload = String.format("{\"title\": \"%s\", \"body\": \"%s\"}", title, body);

                Notification notification = new Notification(
                        subscriptionEntry.getValue().getEndpoint(),
                        subscriptionEntry.getValue().getKeys().get("p256dh"),
                        subscriptionEntry.getValue().getKeys().get("auth"),
                        payload
                );

                PushService pushService = new PushService()
                        .setPublicKey(vapidPublicKey)
                        .setPrivateKey(vapidPrivateKey)
                        .setSubject(vapidSubject);

                pushService.send(notification);
              } catch (Exception e) {
                if (e.getMessage().contains("410") || e.getMessage().contains("404") || e.getMessage().contains("403")) {
                  subscriptions.remove(subscriptionEntry.getKey());
                }

                e.printStackTrace();
              }
            });
  }
}
