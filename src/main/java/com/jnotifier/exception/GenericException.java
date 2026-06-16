package com.jnotifier.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.jnotifier.payload.response.ApiResponse;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GenericException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final ApiResponse<?> responseMap;

  public GenericException(ApiResponse<?> responseMap) {

    super(responseMap.getError().getMessage());
    this.responseMap = responseMap;
  }

  public ApiResponse<?> getResponseMap() {
    return responseMap;
  }
}
