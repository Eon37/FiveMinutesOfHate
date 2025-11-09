package com.eon37_dev.fmh.config.exceptions;

import org.springframework.http.HttpStatus;

public class LocalException extends RuntimeException {
  private final HttpStatus statusCode;
  private final String localizedMessageCode;

  public LocalException(HttpStatus statusCode, String message, String localizedMessageCode) {
    super(message);
    this.statusCode = statusCode;
    this.localizedMessageCode = localizedMessageCode;
  }

  public HttpStatus getStatus() {
    return statusCode;
  }

  public String getLocalizedMessageCode() {
    return localizedMessageCode;
  }
}
