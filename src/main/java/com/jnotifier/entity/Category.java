package com.jnotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

  @NotBlank
  @Column(name = "category_name", nullable = false)
  private String categoryName;

  @Column(name = "category_desc", columnDefinition = "TEXT")
  private String categoryDesc;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "application_id", nullable = false)
  private Application application;

  @NotNull
  @Column(name = "order_id", nullable = false)
  private Integer orderId;

  @NotNull
  @Column(name = "status", nullable = false)
  private Boolean status = true;

  public Category() {
  }

  public Category(String categoryName, String categoryDesc, Application application, Integer orderId, Boolean status) {
    this.categoryName = categoryName;
    this.categoryDesc = categoryDesc;
    this.application = application;
    this.orderId = orderId;
    this.status = status;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getCategoryDesc() {
    return categoryDesc;
  }

  public void setCategoryDesc(String categoryDesc) {
    this.categoryDesc = categoryDesc;
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  public Integer getOrderId() {
    return orderId;
  }

  public void setOrderId(Integer orderId) {
    this.orderId = orderId;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }
}
