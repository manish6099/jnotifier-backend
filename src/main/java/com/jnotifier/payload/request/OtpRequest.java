package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotBlank;

public class OtpRequest {
  @NotBlank
  private String username;

  @NotBlank
  private String otpCode;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getOtpCode() {
    return otpCode;
  }

  public void setOtpCode(String otpCode) {
    this.otpCode = otpCode;
  }
}
