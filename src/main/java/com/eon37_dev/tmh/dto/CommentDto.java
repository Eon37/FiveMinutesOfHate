package com.eon37_dev.tmh.dto;

public class CommentDto {
  private Long id;
  private PostDto comment;

  public CommentDto(Long id, PostDto comment) {
    this.id = id;
    this.comment = comment;
  }

  public Long getId() {
    return id;
  }

  public PostDto getComment() {
    return comment;
  }

  public void setComment(PostDto comment) {
    this.comment = comment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CommentDto that = (CommentDto) o;

    if (!id.equals(that.id)) return false;
    return comment.equals(that.comment);
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + comment.hashCode();
    return result;
  }
}
