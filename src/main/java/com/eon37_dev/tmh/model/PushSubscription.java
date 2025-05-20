package com.eon37_dev.tmh.model;

import java.util.Map;

public class PushSubscription {
  private String endpoint;
  private Map<String, String> keys;

  public PushSubscription(String endpoint, Map<String, String> keys) {
    this.endpoint = endpoint;
    this.keys = keys;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public Map<String, String> getKeys() {
    return keys;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PushSubscription that = (PushSubscription) o;

    if (!endpoint.equals(that.endpoint)) return false;
    return keys.equals(that.keys);
  }

  @Override
  public int hashCode() {
    int result = endpoint.hashCode();
    result = 31 * result + keys.hashCode();
    return result;
  }
}

