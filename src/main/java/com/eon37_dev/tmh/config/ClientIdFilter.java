package com.eon37_dev.tmh.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.boot.web.server.Cookie.SameSite;

import java.io.IOException;

@Component
public class ClientIdFilter implements Filter {
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {

    if (req instanceof HttpServletRequest request && res instanceof HttpServletResponse response) {
      boolean hasCookie = getClientIdFromCookie(request) != null;

      if (!hasCookie) {
        Cookie cookie = new Cookie("clientId", getClientIdFromSession(request.getSession()));
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(false);
        cookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
        cookie.setAttribute("SameSite", SameSite.STRICT.attributeValue());
        response.addCookie(cookie);
      }
    }

    chain.doFilter(req, res);
  }

  public static String getClientIdFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("clientId".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }

    return null;
  }

  private static String getClientIdFromSession(HttpSession session) {
    return String.valueOf(session.getId().hashCode());
  }
}
