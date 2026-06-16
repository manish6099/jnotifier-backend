package com.jnotifier.payload.request;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class SignupRequest {
  @NotBlank
  @Size(max = 100)
  private String fullname;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  private String mobile;

  @NotNull
  private LocalDate dob;

  @NotBlank
  @Pattern(regexp = "^(M|F|T)$", message = "Gender must be M, F, or T")
  private String gender;

  private String role;

  @NotBlank
  private String captchaId;

  @NotBlank
  private String captchaValue;

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public LocalDate getDob() {
    return dob;
  }

  public void setDob(LocalDate dob) {
    this.dob = dob;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
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
