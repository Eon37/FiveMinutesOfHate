package com.eon37_dev.tmh.dto;

import com.eon37_dev.tmh.model.Post;
import com.eon37_dev.tmh.model.PushSubscription;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DtoMapper {
  public static PostDto mapPost(Post post) {
    return new PostDto(post.getId(), post.isAnonymous(), post.getAuthor(), post.getText(), post.getLikes().size(), mapComments(post.getComments()));
  }

  public static List<CommentDto> mapComments(Map<Long, Post> comments) {
    return comments.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
            .map(postEntry -> new CommentDto(
                    postEntry.getKey(),
                    new PostDto(
                            postEntry.getValue().getId(),
                            postEntry.getValue().isAnonymous(),
                            postEntry.getValue().getAuthor(),
                            postEntry.getValue().getText(),
                            postEntry.getValue().getLikes().size(),
                            Collections.emptyList())))
            .toList();
  }

  public static List<PostDto> mapPostListFilterBySession(String sessionId, Map<Long, Post> posts) {
    return posts.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
            .filter(post -> !post.getValue().isAnonymous() || post.getValue().getClientId().equals(sessionId))
            .map(Map.Entry::getValue)
            .map(DtoMapper::mapPost)
            .toList();
  }

  public static PushSubscription mapPushSubscriptionFromDto(PushSubscriptionDto pushSubscriptionDto) {
    return new PushSubscription(
            pushSubscriptionDto.getEndpoint(),
            Map.of("p256dh", pushSubscriptionDto.getKey(),
                    "auth", pushSubscriptionDto.getAuth()));
  }
}
