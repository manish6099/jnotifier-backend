package com.jnotifier.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

  @NotBlank
  private String title;

  @NotBlank
  @Column(columnDefinition = "TEXT")
  private String content;

  private String recipient;

  private String sender;

  @Column(name = "delivery_status")
  private String deliveryStatus = "PENDING";

  public Notification() {
  }

  public Notification(String title, String content, String recipient, String sender) {
    this.title = title;
    this.content = content;
    this.recipient = recipient;
    this.sender = sender;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getDeliveryStatus() {
    return deliveryStatus;
  }

  public void setDeliveryStatus(String deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
  }
}
