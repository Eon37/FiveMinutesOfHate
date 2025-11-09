package com.eon37_dev.fmh.controllers;

import com.eon37_dev.fmh.config.exceptions.LocalException;
import com.eon37_dev.fmh.dto.*;
import com.eon37_dev.fmh.model.ModelAndViewUtils;
import com.eon37_dev.fmh.model.Post;
import com.eon37_dev.fmh.services.NotificationService;
import com.eon37_dev.fmh.services.PostService;
import com.eon37_dev.fmh.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
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
  private static final String CLIENT_ID_MESSAGE_CODE = "errors.no-client-id";
  private final PostService postService;
  private final NotificationService notificationService;

  public PostController(PostService postService, NotificationService notificationService) {
    this.postService = postService;
    this.notificationService = notificationService;
  }

  @GetMapping({"", "/"})
  public ModelAndView getAll(HttpServletRequest request) {
    List<PostDto> postDtos = DtoMapper.mapPostList(postService.getPosts());

    return ModelAndViewUtils.buildView("index", Map.of("posts", postDtos), request);
  }

  @PostMapping(path = {"", "/"})
  public ModelAndView newPost(RedirectAttributes redirectAttributes, HttpServletRequest request,
                              @RequestParam String text) {
    String clientId = CookieUtils.getClientIdFromCookie(request);
    if (clientId == null) throw new LocalException(HttpStatus.BAD_REQUEST, "ClientId missing", CLIENT_ID_MESSAGE_CODE);

    Post newPost = postService.newPost(clientId, text);
    List<PostDto> postDtos = DtoMapper.mapPostList(postService.getPosts());

    notificationService.send(true, newPost, newPost.getId());
    return ModelAndViewUtils.buildRedirect("/", Map.of("posts", postDtos), redirectAttributes, request);
  }

  @ResponseBody
  @PostMapping(path = "/{id}/like-async")
  public ResponseEntity<Integer> likePostAsync(HttpServletRequest request, @PathVariable(name = "id") Long id) {
    String clientId = CookieUtils.getClientIdFromCookie(request);
    if (clientId == null) throw new LocalException(HttpStatus.BAD_REQUEST, "ClientId missing", CLIENT_ID_MESSAGE_CODE);

    return ResponseEntity.ok(postService.likePost(clientId, id));
  }

  @ResponseBody
  @PostMapping(path = "/{id}/comments/{commentId}/like-async")
  public ResponseEntity<Integer> likeCommentAsync(HttpServletRequest request,
                                                  @PathVariable(name = "id") Long id,
                                                  @PathVariable(name = "commentId") String commentId) {
    String clientId = CookieUtils.getClientIdFromCookie(request);
    if (clientId == null) throw new LocalException(HttpStatus.BAD_REQUEST, "ClientId missing", CLIENT_ID_MESSAGE_CODE);

    return ResponseEntity.ok(postService.likeComment(clientId, id, Long.parseLong(commentId)));
  }

  @ResponseBody
  @PostMapping(path = "/{id}/comments")
  public ResponseEntity<CommentDto> commentPostAsync(HttpServletRequest request,
                                                     @PathVariable(name = "id") Long id,
                                                     @RequestBody NewCommentDto commentDto) {
    String clientId = CookieUtils.getClientIdFromCookie(request);
    if (clientId == null) throw new LocalException(HttpStatus.BAD_REQUEST, "ClientId missing", CLIENT_ID_MESSAGE_CODE);

    Map<Long, Post> comment = postService.newComment(clientId, id, commentDto.getComment());

    notificationService.send(false, comment.values().iterator().next(), id);
    return ResponseEntity.ok(DtoMapper.mapComments(comment).iterator().next());
  }
}
