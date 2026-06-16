package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotBlank
  private String captchaId;

  @NotBlank
  private String captchaValue;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCaptchaId() {
    return captchaId;
  }

  public void setCaptchaId(String captchaId) {
    this.captchaId = captchaId;
  }

  public String getCaptchaValue() {
    return captchaValue;
  }

  public void setCaptchaValue(String captchaValue) {
    this.captchaValue = captchaValue;
  }
}
