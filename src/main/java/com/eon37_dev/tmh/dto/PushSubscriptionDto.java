package com.eon37_dev.tmh.dto;

public class PushSubscriptionDto {
  private String endpoint;
  private String key;
  private String auth;

  public PushSubscriptionDto(String endpoint, String key, String auth) {
    this.endpoint = endpoint;
    this.key = key;
    this.auth = auth;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getKey() {
    return key;
  }

  public String getAuth() {
    return auth;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PushSubscriptionDto that = (PushSubscriptionDto) o;

    if (!endpoint.equals(that.endpoint)) return false;
    if (!key.equals(that.key)) return false;
    return auth.equals(that.auth);
  }

  @Override
  public int hashCode() {
    int result = endpoint.hashCode();
    result = 31 * result + key.hashCode();
    result = 31 * result + auth.hashCode();
    return result;
  }
}
