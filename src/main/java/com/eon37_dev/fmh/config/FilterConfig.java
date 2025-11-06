package com.eon37_dev.fmh.config;

import com.eon37_dev.fmh.config.filters.ClientIdFilter;
import com.eon37_dev.fmh.config.filters.IpSessionControlFilter;
import com.eon37_dev.fmh.config.filters.SessionTrackingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
  @Bean
  public FilterRegistrationBean<ClientIdFilter> clientIdFilter() {
    FilterRegistrationBean<ClientIdFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(new ClientIdFilter());
    reg.addUrlPatterns("/", "/theme", "/api/*");
    reg.setOrder(1);
    return reg;
  }
  @Bean
  public FilterRegistrationBean<SessionTrackingFilter> sessionTrackingFilter() {
    FilterRegistrationBean<SessionTrackingFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(new SessionTrackingFilter());
    reg.addUrlPatterns("/", "/theme", "/api/*");
    reg.setOrder(2);
    return reg;
  }
  @Bean
  public FilterRegistrationBean<IpSessionControlFilter> ipSessionControlFilter() {
    FilterRegistrationBean<IpSessionControlFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(new IpSessionControlFilter());
    reg.addUrlPatterns("/", "/theme", "/api/*");
    reg.setOrder(3);
    return reg;
  }
}

