package com.eon37_dev.fmh.config.filters;

import com.eon37_dev.fmh.services.SessionService;
import com.eon37_dev.fmh.utils.CookieUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class SessionTrackingFilter implements Filter {
  private final SessionService sessionService;

  public SessionTrackingFilter(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    String clientId = CookieUtils.getClientIdFromCookie(request);

    sessionService.updateAccess(clientId);

    chain.doFilter(req, res);
  }
}

