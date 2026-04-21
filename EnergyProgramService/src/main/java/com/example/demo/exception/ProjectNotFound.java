package com.example.demo.exception;

public class ProjectNotFound extends RuntimeException {

    public ProjectNotFound(String message) {
        super(message);
    }
}