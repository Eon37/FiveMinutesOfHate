package com.eon37_dev.fmh.config.filters;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

public class IpSessionControlFilter implements Filter {
  private static final int MAX_SESSIONS_PER_IP = 5;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    String ip = req.getRemoteAddr();

    if (SessionTrackingFilter.getSessionCountByIp(ip) >= MAX_SESSIONS_PER_IP) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Too many sessions created from your IP address");
      return;
    }

    chain.doFilter(request, response);
  }
}

