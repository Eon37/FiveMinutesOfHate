package com.eon37_dev.fmh.config.filters;

import com.eon37_dev.fmh.utils.CookieUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SessionTrackingFilter implements Filter {
  private static final ConcurrentHashMap<String, Long> clientLastAccess = new ConcurrentHashMap<>();

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    String clientId = CookieUtils.getClientIdFromCookie(request);

    if (clientId != null) {
      clientLastAccess.put(clientId, System.nanoTime());
    }

    chain.doFilter(req, res);
  }

  public static int getAllSessionCount() {
    return clientLastAccess.size();
  }

  @Scheduled(fixedRate = 5 * 60 * 1000) //every 5 minutes
  public void cleanClients() {
    long currTimestamp = System.nanoTime();
    clientLastAccess.entrySet().removeIf(e -> TimeUnit.NANOSECONDS.toMinutes(currTimestamp - e.getValue()) >= 5);
  }
}

