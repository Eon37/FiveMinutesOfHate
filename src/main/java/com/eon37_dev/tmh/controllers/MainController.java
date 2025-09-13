package com.eon37_dev.tmh.controllers;

import com.eon37_dev.tmh.dto.DtoMapper;
import com.eon37_dev.tmh.model.ModelAndViewUtils;
import com.eon37_dev.tmh.dto.PostDto;
import com.eon37_dev.tmh.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
  private static final Logger log = LoggerFactory.getLogger(MainController.class);
  @Value("${vapid.public.key}")
  private String appServKey;
  private final PostService postService;

  public MainController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/")
  public ModelAndView getAll(HttpServletRequest request) {
    List<PostDto> postDtos = DtoMapper.mapPostList(postService.getPosts());

    return ModelAndViewUtils.buildView("index", Map.of("posts", postDtos), request, appServKey);
  }

  @PostMapping("/theme")
  public ModelAndView theme(RedirectAttributes redirectAttributes, HttpServletResponse response,
                            @RequestParam("theme") String theme) {
    List<PostDto> postDtos = DtoMapper.mapPostList(postService.getPosts());

    ResponseCookie cookie = ResponseCookie.from("theme", theme)
            .path("/")
            .secure(true)
            .httpOnly(false)
            .maxAge(Duration.ofDays(365))
            .sameSite("Strict")
            .build();
    response.setHeader("Set-Cookie", cookie.toString());

    return ModelAndViewUtils.buildRedirect("/", Map.of("posts", postDtos), redirectAttributes, theme, appServKey);
  }
}

