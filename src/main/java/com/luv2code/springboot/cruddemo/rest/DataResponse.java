package com.luv2code.springboot.cruddemo.rest;

public class DataResponse<T> extends ApiResponse {
  private T data;

  public DataResponse(int status, T data) {
    super(status);
    this.data = data;
  }

  public DataResponse(int status, String message, T data) {
    super(status, message);
    this.data = data;
  }

  public T getData() {
    return data;
  }
}
