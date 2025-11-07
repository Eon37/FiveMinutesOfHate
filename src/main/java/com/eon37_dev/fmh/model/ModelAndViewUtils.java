package com.eon37_dev.fmh.model;

import com.eon37_dev.fmh.config.filters.SessionTrackingFilter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
public class ModelAndViewUtils {
  private static String APP_SERV_KEY;
  private static String TURNSTILE_SITE_KEY;
  @Value("${vapid.key.public}")
  private String appServKey;
  @Value("${turnstile.key.site}")
  private String turnstileSiteKey;
  @PostConstruct
  public void init() {
    APP_SERV_KEY = appServKey;
    TURNSTILE_SITE_KEY = turnstileSiteKey;
  }

  public static ModelAndView buildView(String viewName,
                                       Map<String, ?> model,
                                       HttpServletRequest request) {
    ModelAndView mav = new ModelAndView(viewName, model);

    mav.addObject("theme", retrieveTheme(request));
    mav.addObject("activeSessions", SessionTrackingFilter.getAllSessionCount());
    mav.addObject("appServKey", APP_SERV_KEY);
    mav.addObject("turnstileSiteKey", TURNSTILE_SITE_KEY);

    return mav;
  }

  public static ModelAndView buildRedirect(String redirectName,
                                           Map<String, ?> model,
                                           RedirectAttributes redirectAttributes,
                                           HttpServletRequest request) {
    model.forEach(redirectAttributes::addFlashAttribute);

    redirectAttributes.addFlashAttribute("theme", retrieveTheme(request));
    redirectAttributes.addFlashAttribute("activeSessions", SessionTrackingFilter.getAllSessionCount());
    redirectAttributes.addFlashAttribute("appServKey", APP_SERV_KEY);
    redirectAttributes.addFlashAttribute("turnstileSiteKey", TURNSTILE_SITE_KEY);

    return new ModelAndView("redirect:" + redirectName);
  }

  public static ModelAndView buildRedirect(String redirectName,
                                           Map<String, ?> model,
                                           RedirectAttributes redirectAttributes,
                                           String theme) {
    model.forEach(redirectAttributes::addFlashAttribute);

    redirectAttributes.addFlashAttribute("theme", theme);
    redirectAttributes.addFlashAttribute("activeSessions", SessionTrackingFilter.getAllSessionCount());
    redirectAttributes.addFlashAttribute("appServKey", APP_SERV_KEY);
    redirectAttributes.addFlashAttribute("turnstileSiteKey", TURNSTILE_SITE_KEY);


    return new ModelAndView("redirect:" + redirectName);
  }

  private static String retrieveTheme(HttpServletRequest request) {
    return Optional.ofNullable(request.getCookies())
            .flatMap(cookies -> Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("theme")).findFirst())
            .map(Cookie::getValue)
            .orElse("dark");
  }
}
