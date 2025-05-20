package com.eon37_dev.tmh.dto;

public class NewCommentDto {
  private String comment;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NewCommentDto that = (NewCommentDto) o;

    return comment.equals(that.comment);
  }

  @Override
  public int hashCode() {
    return comment.hashCode();
  }
}
