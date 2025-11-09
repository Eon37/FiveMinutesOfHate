package com.eon37_dev.fmh.config.exceptions;

import com.eon37_dev.fmh.model.ModelAndViewUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class GlobalExceptionHandler implements ErrorController {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  @RequestMapping("/error")
  public ModelAndView handleError(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    if (Integer.parseInt(status.toString()) == HttpStatus.FORBIDDEN.value()) {
      return ModelAndViewUtils.buildView(
              "error",
              Map.of("statusCode", status,
                      "userMessage", HttpStatus.FORBIDDEN.getReasonPhrase()),
              request);
    } else {
      log.error("Received global exception");
      return ModelAndViewUtils.buildView(
              "error",
              Map.of("statusCode", status),
              request);
    }
  }
}

