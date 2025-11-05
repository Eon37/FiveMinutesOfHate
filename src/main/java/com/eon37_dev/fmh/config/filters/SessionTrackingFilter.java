package com.eon37_dev.fmh.config.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class SessionTrackingFilter implements Filter {
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpSession session = request.getSession(true); //Creating session manually here in the first filter

    synchronized ("SESSION_TRACKING_FILTER_INCREMENT_SESSION") {
      if (!IpSessionControlFilter.hasSession(session.getId())) {
        String ip = request.getRemoteAddr();
        IpSessionControlFilter.incrementSessionCount(session.getId(), ip);
      }
    }

    chain.doFilter(req, res);
  }
}

