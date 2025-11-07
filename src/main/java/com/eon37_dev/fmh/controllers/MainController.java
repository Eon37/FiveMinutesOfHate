package com.eon37_dev.fmh.controllers;

import com.eon37_dev.fmh.dto.DtoMapper;
import com.eon37_dev.fmh.model.ModelAndViewUtils;
import com.eon37_dev.fmh.dto.PostDto;
import com.eon37_dev.fmh.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private final PostService postService;

  public MainController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/")
  public ModelAndView getAll(HttpServletRequest request) {
    List<PostDto> postDtos = DtoMapper.mapPostList(postService.getPosts());

    return ModelAndViewUtils.buildView("index", Map.of("posts", postDtos), request);
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

    return ModelAndViewUtils.buildRedirect("/", Map.of("posts", postDtos), redirectAttributes, theme);
  }
}

