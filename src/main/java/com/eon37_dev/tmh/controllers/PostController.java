package com.eon37_dev.tmh.controllers;

import com.eon37_dev.tmh.config.ClientIdFilter;
import com.eon37_dev.tmh.dto.*;
import com.eon37_dev.tmh.model.ModelAndViewUtils;
import com.eon37_dev.tmh.model.Post;
import com.eon37_dev.tmh.services.NotificationService;
import com.eon37_dev.tmh.services.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/posts")
public class PostController {
  @Value("${vapid.public.key}")
  private String appServKey;
  private final PostService postService;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final NotificationService notificationService;

  public PostController(PostService postService, SimpMessagingTemplate simpMessagingTemplate, NotificationService notificationService) {
    this.postService = postService;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.notificationService = notificationService;
  }

  @GetMapping("/")
  public ModelAndView getAll(HttpServletRequest request) {
    List<PostDto> postDtos = DtoMapper.mapPostListFilterBySession(request.getSession().getId(), postService.getPosts());

    return ModelAndViewUtils.buildView("index", Map.of("posts", postDtos), request, appServKey);
  }

  @PostMapping(path = "/create")
  public ModelAndView newPost(RedirectAttributes redirectAttributes, HttpServletRequest request,
                              @RequestParam(name = "text") String text,
                              @RequestParam(name = "anonymous", defaultValue = "false") boolean anonymous) {
    Post newPost = postService.newPost(request.getSession().getId(), HtmlUtils.htmlEscape(text), anonymous);
    List<PostDto> postDtos = DtoMapper.mapPostListFilterBySession(request.getSession().getId(), postService.getPosts());

    simpMessagingTemplate.convertAndSend("/topic/posts", new PostMSG( //TODO refactor
            String.valueOf(request.getSession().getId().hashCode()),
            newPost.getId(),
            newPost.getAuthor()));
    notificationService.sendNotificationToAllExcept("New Post", "New post from " + newPost.getAuthor(), ClientIdFilter.getClientIdFromCookie(request));
    return ModelAndViewUtils.buildRedirect("/", Map.of("posts", postDtos), redirectAttributes, request, appServKey);
  }

  @ResponseBody
  @PostMapping(path = "/{id}/like-async")
  public ResponseEntity<Integer> likePostAsync(HttpSession httpSession, @PathVariable(name = "id") String id) {
    return ResponseEntity.ok(postService.likePost(httpSession.getId(), Long.parseLong(id)));
  }

  @ResponseBody
  @PostMapping(path = "/{id}/comments/{commentId}/like-async")
  public ResponseEntity<Integer> likeCommentAsync(HttpSession httpSession,
                                                  @PathVariable(name = "id") String id,
                                                  @PathVariable(name = "commentId") String commentId) {
    return ResponseEntity.ok(postService.likeComment(httpSession.getId(), Long.parseLong(id), Long.parseLong(commentId)));
  }

  @ResponseBody
  @PostMapping(path = "/{id}/comments")
  public ResponseEntity<CommentDto> commentPostAsync(HttpServletRequest request,
                                                     @PathVariable(name = "id") String id,
                                                     @RequestBody NewCommentDto commentDto) {
    String escaped = HtmlUtils.htmlEscape(commentDto.getComment());
    Map<Long, Post> comment = postService.newComment(request.getSession().getId(), Long.parseLong(id), escaped);

    simpMessagingTemplate.convertAndSend("/topic/comments", new CommentMSG(
            String.valueOf(request.getSession().getId().hashCode()),
            Long.parseLong(id),
            comment.entrySet().iterator().next().getKey(),
            comment.entrySet().iterator().next().getValue().getAuthor()));
    notificationService.sendNotificationToAllExcept("New Comment", "New comment from " + comment.entrySet().iterator().next().getValue().getAuthor(), ClientIdFilter.getClientIdFromCookie(request));
    return ResponseEntity.ok(DtoMapper.mapComments(comment).iterator().next());
  }
}
