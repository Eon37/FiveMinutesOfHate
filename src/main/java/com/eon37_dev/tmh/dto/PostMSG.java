package com.eon37_dev.tmh.dto;

public class PostMSG {
  private String clientId;
  private Long id;
  private String author;

  public PostMSG(String clientId, Long id, String author) {
    this.clientId = clientId;
    this.id = id;
    this.author = author;
  }

  public String getClientId() {
    return clientId;
  }

  public Long getId() {
    return id;
  }

  public String getAuthor() {
    return author;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PostMSG postMSG = (PostMSG) o;

    if (!clientId.equals(postMSG.clientId)) return false;
    if (!id.equals(postMSG.id)) return false;
    return author.equals(postMSG.author);
  }

  @Override
  public int hashCode() {
    int result = clientId.hashCode();
    result = 31 * result + id.hashCode();
    result = 31 * result + author.hashCode();
    return result;
  }
}
