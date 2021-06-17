package com.luv2code.springboot.cruddemo.rest;

public class ErrorResponse extends ApiResponse {

  private long timeStamp;

  public ErrorResponse() {
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }
}
