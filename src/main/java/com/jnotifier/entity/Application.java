package com.jnotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "applications")
public class Application extends BaseEntity {

  @NotBlank
  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "tags")
  private String tags;

  @NotNull
  @Column(name = "application_start_date", nullable = false)
  private LocalDate applicationStartDate;

  @NotNull
  @Column(name = "application_end_date", nullable = false)
  private LocalDate applicationEndDate;

  @Size(max = 100)
  @Column(name = "short_description", length = 100)
  private String shortDescription;

  @NotNull
  @Column(name = "apply_link")
  private String applyLink;

  @NotNull
  @Column(name = "notification_pdf_filename", length = 100)
  private String notificationPdfFilename;

  @NotNull
  @Column(name = "status", nullable = false)
  private Boolean status = true;

  public Application() {
  }

  public Application(String title, String tags, LocalDate applicationStartDate, LocalDate applicationEndDate,
      String shortDescription, Boolean status) {
    this.title = title;
    this.tags = tags;
    this.applicationStartDate = applicationStartDate;
    this.applicationEndDate = applicationEndDate;
    this.shortDescription = shortDescription;
    this.status = status;
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

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
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

}
