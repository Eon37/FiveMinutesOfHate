package com.eon37_dev.fmh.config.filters;

import com.eon37_dev.fmh.utils.CookieUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class SessionTrackingFilter implements Filter {
  private static final ConcurrentHashMap<String, HttpSession> clientSession = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<String, String> sessionClient = new ConcurrentHashMap<>();

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    String clientId = CookieUtils.getClientIdFromCookie(request);

    if (clientId != null) {
      synchronized ("SESSION_TRACKING_FILTER_INCREMENT_SESSION_" + clientId) {
        HttpSession session = request.getSession();
        HttpSession oldSession = clientSession.get(clientId);

        if (oldSession != null) {
          if (!session.getId().equals(oldSession.getId())) {
            oldSession.invalidate();
            incrementSessionCount(session, clientId);
          }
        } else {
          incrementSessionCount(session, clientId);
        }
      }
    }
    chain.doFilter(req, res);
  }

  private static void incrementSessionCount(HttpSession session, String clientId) {
    String sessionId = session.getId();
    sessionClient.compute(sessionId, (k, v) -> {
      clientSession.put(clientId, session);
      return clientId;
    });
  }

  public static void decrementSessionCount(String sessionId) {
    sessionClient.compute(sessionId, (k, v) -> {
      if (v != null) clientSession.remove(v);
      return null;
    });
  }

  public static int getAllSessionCount() {
    return clientSession.size();
  }
}

