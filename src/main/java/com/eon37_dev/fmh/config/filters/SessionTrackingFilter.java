package com.eon37_dev.fmh.config.filters;

import com.eon37_dev.fmh.utils.CookieUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class SessionTrackingFilter implements Filter {
  private static final ConcurrentHashMap<String, Integer> sessionsPerIp = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<String, String> sessionIp = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<String, HttpSession> clientSession = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<String, String> sessionClient = new ConcurrentHashMap<>();

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;

    String clientId = CookieUtils.getClientIdFromCookie(request);
    if (clientId != null) {
      synchronized ("SESSION_TRACKING_FILTER_INCREMENT_SESSION") {
        HttpSession session = request.getSession();
        HttpSession oldSession = clientSession.get(clientId);
        if (oldSession != null && !session.getId().equals(oldSession.getId())) {
          oldSession.invalidate();
        }

        if (!sessionIp.containsKey(session.getId())) {
          String ip = request.getRemoteAddr();
          incrementSessionCount(session, clientId, ip);
        }
      }
    }
    chain.doFilter(req, res);
  }

  public static void incrementSessionCount(HttpSession session, String clientId, String ip) {
    String sessionId = session.getId();
    sessionClient.put(sessionId, clientId);
    clientSession.put(clientId, session);

    sessionIp.put(sessionId, ip);
    sessionsPerIp.compute(ip, (key, val) -> {
      if (val == null) return 1;
      return ++val;
    });
  }

  public static void decrementSessionCount(String sessionId) {
    String clientId = sessionClient.remove(sessionId);
    clientSession.remove(clientId);

    String ip = sessionIp.remove(sessionId);
    sessionsPerIp.computeIfPresent(ip, (key, val) -> {
      if (val <= 0) return null;
      return --val;
    });
  }

  public static int getAllSessionCount() {
    return sessionIp.size();
  }
}

