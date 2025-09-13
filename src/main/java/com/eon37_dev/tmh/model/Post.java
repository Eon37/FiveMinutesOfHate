package com.eon37_dev.tmh.model;

import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Post {
  private final long id;
  private final String clientId;
  private final String author;
  private final String text;
  private Set<String> likes;
  private final Map<Long, Post> comments;
  private long expire;

  private Post(long id, String clientId, String author, String text, Set<String> likes, Map<Long, Post> comments, long expire) {
    this.id = id;
    this.clientId = clientId;
    this.author = author;
    this.text = text;
    this.likes = likes;
    this.comments = comments;
    this.expire = expire;
  }

  public long getId() {
    return id;
  }

  public String getClientId() {
    return clientId;
  }

  public String getAuthor() {
    return author;
  }

  public String getText() {
    return text;
  }

  public Set<String> getLikes() {
    return likes;
  }

  public long getExpire() {
    return expire;
  }

  public Map<Long, Post> getComments() {
    return comments;
  }

  public int incrementLikes(String sessionId) {
    if (likes.contains(sessionId)) {
      likes.remove(sessionId);
    } else {
      likes.add(sessionId);
    }

    return likes.size();
  }

  public void incrementExpire() {
    this.expire = System.nanoTime() + TimeUnit.MINUTES.toNanos(5);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private long id;
    private String clientId;
    private String author;
    private String text = "";

    public Builder id(long id) {
      this.id = id;
      return this;
    }

    public Builder clientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public Builder author(String author) {
      this.author = author;
      return this;
    }

    public Builder text(String text) {
      this.text = text;
      return this;
    }

    public Post build(boolean isPost) {
      Assert.isTrue(this.id > 0, "Post should have an id");
      Assert.hasText(this.clientId, "Post should have a session id");
      Assert.hasText(this.author, "Author must not be empty");
      if (isPost) Assert.isTrue(this.text.length() >= 20, "Text should be at least 20 chars");

      return new Post(
              this.id,
              this.clientId,
              this.author,
              this.text,
              new HashSet<>(),
              new ConcurrentHashMap<>(),
              System.nanoTime() + TimeUnit.MINUTES.toNanos(5));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Post post = (Post) o;

    if (id != post.id) return false;
    if (expire != post.expire) return false;
    if (!clientId.equals(post.clientId)) return false;
    if (!author.equals(post.author)) return false;
    if (!text.equals(post.text)) return false;
    if (!likes.equals(post.likes)) return false;
    return comments.equals(post.comments);
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + clientId.hashCode();
    result = 31 * result + author.hashCode();
    result = 31 * result + text.hashCode();
    result = 31 * result + likes.hashCode();
    result = 31 * result + comments.hashCode();
    result = 31 * result + (int) (expire ^ (expire >>> 32));
    return result;
  }
}


