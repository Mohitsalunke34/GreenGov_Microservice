package com.example.demo.exception;

public class InvalidDisbursementException extends RuntimeException {

    public InvalidDisbursementException(String message) {
        super(message);
    }
}