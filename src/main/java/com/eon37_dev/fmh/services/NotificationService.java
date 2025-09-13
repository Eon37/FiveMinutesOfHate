package com.eon37_dev.fmh.services;

import com.eon37_dev.fmh.dto.CommentMSG;
import com.eon37_dev.fmh.dto.PostMSG;
import com.eon37_dev.fmh.model.Post;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class NotificationService {
  private static final String wsTopicPost = "/topic/posts";
  private static final String wsTopicComment = "/topic/comments";
  private static final String notificationTitlePost = "page.main.notification.title.post";
  private static final String notificationTitleComment = "page.main.notification.title.comment";
  private static final String notificationTextPost = "page.main.notification.text.post";
  private static final String notificationTextComment = "page.main.notification.text.comment";
  private final MessageSource messageSource;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final PushService pushService;

  public NotificationService(MessageSource messageSource, SimpMessagingTemplate simpMessagingTemplate, PushService pushService) {
    this.messageSource = messageSource;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.pushService = pushService;
  }

  public void send(boolean isPost, Post post, Long postId) {
    if (isPost) {
      sendPost(post);
    } else {
      sendComment(post, postId);
    }
  }


  private void sendPost(Post post) {
    String title = messageSource.getMessage(notificationTitlePost, new Object[]{}, Locale.getDefault());
    String text = messageSource.getMessage(notificationTextPost, new Object[]{}, Locale.getDefault());

    simpMessagingTemplate.convertAndSend(
            wsTopicPost,
            new PostMSG(
                    post.getClientId(),
                    post.getId(),
                    title,
                    text + "<b>" + post.getAuthor() + "</b>"));

    pushService.sendNotificationToAllExcept(title, text + post.getAuthor(), post.getClientId());
  }

  private void sendComment(Post comment, Long postId) {
    String title = messageSource.getMessage(notificationTitleComment, new Object[]{}, Locale.getDefault());
    String text = messageSource.getMessage(notificationTextComment, new Object[]{}, Locale.getDefault());

    simpMessagingTemplate.convertAndSend(
            wsTopicComment,
            new CommentMSG(
                    comment.getClientId(),
                    postId,
                    comment.getId(),
                    title,
                    text + "<b>" + comment.getAuthor() + "</b>"));

    pushService.sendNotificationToAllExcept(title, text + comment.getAuthor(), comment.getClientId());
  }
}
