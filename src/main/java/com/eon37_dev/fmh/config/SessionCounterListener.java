package com.eon37_dev.fmh.config;
import com.eon37_dev.fmh.config.filters.IpSessionControlFilter;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

@Component
public class SessionCounterListener implements HttpSessionListener {
  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    IpSessionControlFilter.decrementSessionCount(se.getSession().getId());
  }
}

