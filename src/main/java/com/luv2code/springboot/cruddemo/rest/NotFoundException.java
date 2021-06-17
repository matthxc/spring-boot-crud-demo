package com.luv2code.springboot.cruddemo.rest;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
