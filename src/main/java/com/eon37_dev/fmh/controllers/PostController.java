package com.eon37_dev.fmh.controllers;

import com.eon37_dev.fmh.dto.*;
import com.eon37_dev.fmh.model.ModelAndViewUtils;
import com.eon37_dev.fmh.model.Post;
import com.eon37_dev.fmh.services.NotificationService;
import com.eon37_dev.fmh.services.PostService;
import com.eon37_dev.fmh.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/posts")
public class PostController {
  @Value("${vapid.public.key}")
  private String appServKey;
  private final PostService postService;
  private final NotificationService notificationService;

  public PostController(PostService postService, NotificationService notificationService) {
    this.postService = postService;
    this.notificationService = notificationService;
  }

  @GetMapping({"", "/"})
  public ModelAndView getAll(HttpServletRequest request) {
    List<PostDto> postDtos = DtoMapper.mapPostList(postService.getPosts());

    return ModelAndViewUtils.buildView("index", Map.of("posts", postDtos), request, appServKey);
  }

  @PostMapping(path = {"", "/"})
  public ModelAndView newPost(RedirectAttributes redirectAttributes, HttpServletRequest request,
                              @RequestParam(name = "text") String text) {
    Post newPost = postService.newPost(CookieUtils.getClientIdFromCookie(request), text);
    List<PostDto> postDtos = DtoMapper.mapPostList(postService.getPosts());

    notificationService.send(true, newPost, newPost.getId());
    return ModelAndViewUtils.buildRedirect("/", Map.of("posts", postDtos), redirectAttributes, request, appServKey);
  }

  @ResponseBody
  @PostMapping(path = "/{id}/like-async")
  public ResponseEntity<Integer> likePostAsync(HttpServletRequest request, @PathVariable(name = "id") Long id) {
    return ResponseEntity.ok(postService.likePost(CookieUtils.getClientIdFromCookie(request), id));
  }

  @ResponseBody
  @PostMapping(path = "/{id}/comments/{commentId}/like-async")
  public ResponseEntity<Integer> likeCommentAsync(HttpServletRequest request,
                                                  @PathVariable(name = "id") Long id,
                                                  @PathVariable(name = "commentId") String commentId) {
    return ResponseEntity.ok(postService.likeComment(CookieUtils.getClientIdFromCookie(request), id, Long.parseLong(commentId)));
  }

  @ResponseBody
  @PostMapping(path = "/{id}/comments")
  public ResponseEntity<CommentDto> commentPostAsync(HttpServletRequest request,
                                                     @PathVariable(name = "id") Long id,
                                                     @RequestBody NewCommentDto commentDto) {
    Map<Long, Post> comment = postService.newComment(CookieUtils.getClientIdFromCookie(request), id, commentDto.getComment());

    notificationService.send(false, comment.values().iterator().next(), id);
    return ResponseEntity.ok(DtoMapper.mapComments(comment).iterator().next());
  }
}
