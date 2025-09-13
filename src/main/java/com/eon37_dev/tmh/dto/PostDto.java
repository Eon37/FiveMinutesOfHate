package com.eon37_dev.tmh.dto;

import com.eon37_dev.tmh.model.Post;

import java.util.*;

public class PostDto {
  private final Long id;
  private final String author;
  private final String text;
  private final int likes;
  private List<CommentDto> comments;

  public PostDto(Long id, String author, String text, int likes, List<CommentDto> comments) {
    this.id = id;
    this.author = author;
    this.text = text;
    this.likes = likes;
    this.comments = comments;
  }

  public Long getId() {
    return id;
  }

  public String getAuthor() {
    return author;
  }

  public String getText() {
    return text;
  }

  public int getLikes() {
    return likes;
  }

  public List<CommentDto> getComments() {
    return comments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PostDto postDto = (PostDto) o;

    if (likes != postDto.likes) return false;
    if (!id.equals(postDto.id)) return false;
    if (!author.equals(postDto.author)) return false;
    if (!text.equals(postDto.text)) return false;
    return comments.equals(postDto.comments);
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + author.hashCode();
    result = 31 * result + text.hashCode();
    result = 31 * result + likes;
    result = 31 * result + comments.hashCode();
    return result;
  }
}
