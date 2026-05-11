package com.eon37_dev.fmh.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {
  private static final ConcurrentHashMap<String, Long> clientLastAccess = new ConcurrentHashMap<>();

  public void updateAccess(String clientId) {
    if (clientId != null) {
      clientLastAccess.put(clientId, System.nanoTime());
    }
  }

  public static int getAllSessionCount() {
    return clientLastAccess.size();
  }

  @Scheduled(fixedRate = 300_000) // 5 minutes
  public void cleanClients() {
    long currTimestamp = System.nanoTime();
    clientLastAccess.entrySet().removeIf(e -> TimeUnit.NANOSECONDS.toMinutes(currTimestamp - e.getValue()) >= 5);
  }
}

