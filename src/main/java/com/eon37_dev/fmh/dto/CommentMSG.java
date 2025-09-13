package com.eon37_dev.fmh.dto;

public class CommentMSG {
  private String clientId;
  private Long id;
  private Long commentId;
  private String title;
  private String text;

  public CommentMSG(String clientId, Long id, Long commentId, String title, String text) {
    this.clientId = clientId;
    this.id = id;
    this.commentId = commentId;
    this.title = title;
    this.text = text;
  }

  public String getClientId() {
    return clientId;
  }

  public Long getId() {
    return id;
  }

  public Long getCommentId() {
    return commentId;
  }

  public String getTitle() {
    return title;
  }

  public String getText() {
    return text;
  }
}
