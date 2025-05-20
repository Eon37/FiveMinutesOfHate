package com.eon37_dev.tmh.dto;

public class CommentMSG {
  private String clientId;
  private Long id;
  private Long commentId;
  private String author;

  public CommentMSG(String clientId, Long id, Long commentId, String author) {
    this.clientId = clientId;
    this.id = id;
    this.commentId = commentId;
    this.author = author;
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

  public String getAuthor() {
    return author;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CommentMSG that = (CommentMSG) o;

    if (!clientId.equals(that.clientId)) return false;
    if (!id.equals(that.id)) return false;
    if (!commentId.equals(that.commentId)) return false;
    return author.equals(that.author);
  }

  @Override
  public int hashCode() {
    int result = clientId.hashCode();
    result = 31 * result + id.hashCode();
    result = 31 * result + commentId.hashCode();
    result = 31 * result + author.hashCode();
    return result;
  }
}
