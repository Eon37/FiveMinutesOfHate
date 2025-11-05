package com.eon37_dev.fmh.config.filters;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IpSessionControlFilter implements Filter {
  private static final int MAX_SESSIONS_PER_IP = 5;
  private static final ConcurrentHashMap<String, AtomicInteger> sessionsPerIp = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<String, String> sessionIp = new ConcurrentHashMap<>();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    String ip = req.getRemoteAddr();

    AtomicInteger count = sessionsPerIp.get(ip);
    if (count != null && count.get() >= MAX_SESSIONS_PER_IP) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Too many sessions created from your IP address");
      return;
    }

    chain.doFilter(request, response);
  }

  public static boolean hasSession(String sessionId) {
    return sessionIp.containsKey(sessionId);
  }

  public static void incrementSessionCount(String sessionId, String ip) {
    sessionIp.put(sessionId, ip);
    sessionsPerIp.compute(ip, (key, val) -> {
      if (val == null) return new AtomicInteger(1);
      val.incrementAndGet();
      return val;
    });
  }

  public static void decrementSessionCount(String sessionId) {
    String ip = sessionIp.remove(sessionId);
    sessionsPerIp.computeIfPresent(ip, (key, val) -> {
      if (val.decrementAndGet() <= 0) return null;
      return val;
    });
  }

  public static int getAllSessionCount() {
    return sessionIp.size();
  }
}

