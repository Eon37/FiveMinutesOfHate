package com.eon37_dev.fmh.config.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

public class ValidateCaptchaFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(ValidateCaptchaFilter.class);
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

    String token = req.getParameter("cf-turnstile-response");

    if (token == null || !verify(token)) {
      res.sendError(HttpStatus.FORBIDDEN.value(), "Captcha validation failed");
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
      log.debug("REQUEST verifying token");

      ResponseEntity<TurnstileResponse> response =
              restTemplate.postForEntity(SITEVERIFY_URL, request, TurnstileResponse.class);

      TurnstileResponse resp = response.getBody();
      if (response.getStatusCode().is2xxSuccessful()) {
        log.debug("RESPONSE [{}] verifying token", response.getStatusCode());
      } else {
        log.error("RESPONSE [{}] verifying token: {}", response.getStatusCode(), resp.errorCodes);
      }
      return resp.success;
    } catch (Exception e) {
      log.error("Error verifying token", e);
      return false;
    }
  }

  private static class TurnstileResponse {
    private final boolean success;
    private final List<String> errorCodes;

    public TurnstileResponse(boolean success, List<String> errorCodes) {
      this.success = success;
      this.errorCodes = errorCodes;
    }
  }
}
