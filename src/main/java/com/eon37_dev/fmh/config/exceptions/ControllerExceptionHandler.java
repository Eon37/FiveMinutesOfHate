package com.eon37_dev.fmh.config.exceptions;

import com.eon37_dev.fmh.model.ModelAndViewUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);
  private static final Object[] EMPTY_ARGS = new Object[]{};
  private final MessageSource messageSource;
  private final LocaleResolver localeResolver;

  public ControllerExceptionHandler(MessageSource messageSource, LocaleResolver localeResolver) {
    this.messageSource = messageSource;
    this.localeResolver = localeResolver;
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ModelAndView handleNotFound(HttpServletRequest request, NoResourceFoundException ex) {
    return ModelAndViewUtils.buildView(
            "error",
            Map.of("statusCode", ex.getStatusCode().value(),
                    "userMessage", ex.getLocalizedMessage()),
            request);
  }
  @ExceptionHandler(LocalException.class)
  public ModelAndView handleLocalException(HttpServletRequest request, LocalException ex) {
    log.warn("Received local exception for request {}", request.getRequestURI(), ex);
    Locale locale = localeResolver.resolveLocale(request);
    String localizedMessage = messageSource.getMessage(ex.getLocalizedMessageCode(), EMPTY_ARGS, locale);

    return ModelAndViewUtils.buildView(
            "error",
            Map.of("statusCode", ex.getStatus().value(),
                    "userMessage", localizedMessage),
            request);
  }

  @ExceptionHandler(Exception.class)
  public ModelAndView handleGenericException(HttpServletRequest request, Exception ex) {
    log.error("Received exception for request {}", request.getRequestURI(), ex);
    return ModelAndViewUtils.buildView(
            "error",
            Map.of("statusCode", 500),
            request);
  }
}
