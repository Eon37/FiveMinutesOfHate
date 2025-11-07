package com.eon37_dev.fmh.config.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

public class ValidateCaptchaFilter implements Filter {
  private static final String SITEVERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
  private final RestTemplate restTemplate = new RestTemplate();
  private final String turnstileSecretKey;

  public ValidateCaptchaFilter(String turnstileSecretKey) {
    this.turnstileSecretKey = turnstileSecretKey;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    String token = req.getParameter("token");

    //todo create map of endpoints to check to require not null token
    if (token != null && !verify(token)) {
      res.sendError(HttpServletResponse.SC_FORBIDDEN, "Captcha validation failed");
      return;
    }

    chain.doFilter(request, response);
  }

  private boolean verify(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("secret", turnstileSecretKey);
    params.add("response", token);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    try {
      ResponseEntity<TurnstileResponse> response =
              restTemplate.postForEntity(SITEVERIFY_URL, request, TurnstileResponse.class);
      return response.getBody().success;
    } catch (Exception e) {
      //todo add logging
      return false;
    }
  }

  private static class TurnstileResponse {
    private boolean success;
    private List<String> errorCodes;

    public TurnstileResponse(boolean success, List<String> errorCodes) {
      this.success = success;
      this.errorCodes = errorCodes;
    }
  }
}
