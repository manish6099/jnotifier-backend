package com.jnotifier.payload.response;

public class CategoryPublicResponse {
  private String categoryName;
  private String categoryDesc;
  private Integer orderId;

  public CategoryPublicResponse() {
  }

  public CategoryPublicResponse(String categoryName, String categoryDesc, Integer orderId) {
    this.categoryName = categoryName;
    this.categoryDesc = categoryDesc;
    this.orderId = orderId;
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

  public Integer getOrderId() {
    return orderId;
  }

  public void setOrderId(Integer orderId) {
    this.orderId = orderId;
  }
}
