package com.eon37_dev.fmh.config.filters;

import com.eon37_dev.fmh.utils.CookieUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.boot.web.server.Cookie.SameSite;

import java.io.IOException;
import java.util.UUID;

public class ClientIdFilter implements Filter {
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {

    if (req instanceof HttpServletRequest request && res instanceof HttpServletResponse response) {
      boolean hasCookie = CookieUtils.getClientIdFromCookie(request) != null;

      if (!hasCookie) {
        Cookie cookie = new Cookie("clientId", UUID.randomUUID().toString());
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
}
