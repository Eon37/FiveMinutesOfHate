package com.eon37_dev.fmh.dto;

public class PostMSG {
  private String clientId;
  private Long id;
  private String title;
  private String text;

  public PostMSG(String clientId, Long id, String title, String text) {
    this.clientId = clientId;
    this.id = id;
    this.title = title;
    this.text = text;
  }

  public String getClientId() {
    return clientId;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getText() {
    return text;
  }
}
