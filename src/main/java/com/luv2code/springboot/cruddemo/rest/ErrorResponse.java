package com.luv2code.springboot.cruddemo.rest;

public class ErrorResponse extends ApiResponse {

  private long timeStamp;

  public ErrorResponse() {

  }

  public ErrorResponse(int status, String message, long timeStamp) {
    super(status, message);
    this.timeStamp = timeStamp;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }
}
