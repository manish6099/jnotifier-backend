package com.jnotifier.payload.response;

import java.time.LocalDate;

public class JobApplicationResponse {
  private String title;
  private String tags;
  private LocalDate applicationStartDate;
  private LocalDate applicationEndDate;
  private String shortDescription;

  public JobApplicationResponse() {
  }

  public JobApplicationResponse(String title, String tags, LocalDate applicationStartDate, LocalDate applicationEndDate, String shortDescription) {
    this.title = title;
    this.tags = tags;
    this.applicationStartDate = applicationStartDate;
    this.applicationEndDate = applicationEndDate;
    this.shortDescription = shortDescription;
  }

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
}
