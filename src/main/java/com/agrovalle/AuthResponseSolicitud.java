package com.agrovalle;

public class AuthResponseSolicitud {

  private String accessToken;
  private String tokenType;
  private long expiresIn;

  public AuthResponseSolicitud(String accessToken, long expiresIn) {
    this.accessToken = accessToken;
    this.tokenType = "Bearer";
    this.expiresIn = expiresIn;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(long expiresIn) {
    this.expiresIn = expiresIn;
  }
}
