package com.jnotifier.payload.response;

import org.slf4j.MDC;

public class ApiResponse<T> {
  private boolean success;
  private T data;
  private ApiError error;
  private String requestId;

  public ApiResponse() {
    this.requestId = MDC.get("requestId");
  }

  public static <T> ApiResponse<T> success(T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setData(data);
    return response;
  }

  public static <T> ApiResponse<T> error(String code, String message) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setError(new ApiError(code, message));
    return response;
  }

  public static <T> ApiResponse<T> error(ApiError error) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setError(error);
    return response;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public ApiError getError() {
    return error;
  }

  public void setError(ApiError error) {
    this.error = error;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }
}
