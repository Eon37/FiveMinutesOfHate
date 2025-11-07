package com.eon37_dev.fmh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfig {
  @Bean
  public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(10_000); // 5 seconds
    clientHttpRequestFactory.setReadTimeout(30_000); // 30 seconds
    return new RestTemplate(clientHttpRequestFactory);
  }
}
