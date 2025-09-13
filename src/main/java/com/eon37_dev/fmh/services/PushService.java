package com.eon37_dev.fmh.services;

import com.eon37_dev.fmh.model.PushSubscription;
import nl.martijndwars.webpush.Notification;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PushService {
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
  private final Map<String, String> endpointClient = new ConcurrentHashMap<>();

  public void addSubscription(String clientId, PushSubscription pushSubscription) {
    if (clientId == null) return;

    PushSubscription existing = subscriptions.get(clientId);
    if (existing != null && !pushSubscription.getEndpoint().equals(existing.getEndpoint())) {
      endpointClient.remove(existing.getEndpoint());
    }

    endpointClient.compute(pushSubscription.getEndpoint(), (k, v) -> {
      if (v != null) {
        if (!clientId.equals(v)) {
          subscriptions.remove(v);
        }
      }

      subscriptions.put(clientId, pushSubscription);
      return clientId;
    });
  }

  public void sendNotificationToAllExcept(String title, String body, String exceptClientId) {
    try {
      nl.martijndwars.webpush.PushService pushService = new nl.martijndwars.webpush.PushService()
              .setPublicKey(vapidPublicKey)
              .setPrivateKey(vapidPrivateKey)
              .setSubject(vapidSubject);

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

                  pushService.send(notification);
                } catch (Exception e) {
                  if (e.getMessage().contains("410") || e.getMessage().contains("404") || e.getMessage().contains("403")) {
                    subscriptions.remove(subscriptionEntry.getKey());
                  }

                  e.printStackTrace();
                }
              });
    } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }
}
