package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ApplicationRequest {

  @NotBlank
  private String title;

  private String tags;

  @NotNull
  private LocalDate applicationStartDate;

  @NotNull
  private LocalDate applicationEndDate;

  @Size(max = 100)
  private String shortDescription;

  private Boolean status;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public LocalDate getApplicationStartDate() {
    return applicationStartDate;
  }

  public void setApplicationStartDate(LocalDate applicationStartDate) {
    this.applicationStartDate = applicationStartDate;
  }

  public LocalDate getApplicationEndDate() {
    return applicationEndDate;
  }

  public void setApplicationEndDate(LocalDate applicationEndDate) {
    this.applicationEndDate = applicationEndDate;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }
}
