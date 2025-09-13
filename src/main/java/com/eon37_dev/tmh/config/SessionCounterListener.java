package com.eon37_dev.tmh.config;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SessionCounterListener implements HttpSessionListener {

  @Override
  public void sessionCreated(HttpSessionEvent se) {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    HttpServletRequest request = attr.getRequest();
    String ip = getClientIP(request);

    IpSessionControlFilter.incrementSessionCount(se.getSession().getId(), ip);
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    IpSessionControlFilter.decrementSessionCount(se.getSession().getId());
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}

