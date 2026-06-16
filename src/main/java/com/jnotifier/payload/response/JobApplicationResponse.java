package com.jnotifier.payload.response;

import java.time.LocalDate;

public class JobApplicationResponse {
  private String title;
  private String tags;
  private LocalDate applicationStartDate;
  private LocalDate applicationEndDate;
  private String shortDescription;
  private String applyLink;
  private String notificationPdfFilename;

  public JobApplicationResponse() {
  }

  public JobApplicationResponse(String title, String tags, LocalDate applicationStartDate, LocalDate applicationEndDate, String shortDescription, String applyLink, String notificationPdfFilename) {
    this.title = title;
    this.tags = tags;
    this.applicationStartDate = applicationStartDate;
    this.applicationEndDate = applicationEndDate;
    this.shortDescription = shortDescription;
    this.applyLink = applyLink;
    this.notificationPdfFilename = notificationPdfFilename;
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

  public String getApplyLink() {
    return applyLink;
  }

  public void setApplyLink(String applyLink) {
    this.applyLink = applyLink;
  }

  public String getNotificationPdfFilename() {
    return notificationPdfFilename;
  }

  public void setNotificationPdfFilename(String notificationPdfFilename) {
    this.notificationPdfFilename = notificationPdfFilename;
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
