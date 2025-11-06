package com.eon37_dev.fmh.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtils {
  public static String getClientIdFromCookie(HttpServletRequest request) {
    Cookie clientIdCookie = getCookie(request, "clientId");
    return clientIdCookie == null ? null : clientIdCookie.getValue();
  }
  public static Cookie getCookie(HttpServletRequest request, String cookieName) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookieName.equals(cookie.getName())) {
          return cookie;
        }
      }
    }

    return null;
  }
}
