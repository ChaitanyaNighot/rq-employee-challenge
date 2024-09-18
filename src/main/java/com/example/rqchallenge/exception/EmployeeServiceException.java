package com.example.rqchallenge.exception;

public class EmployeeServiceException extends RuntimeException {
    public EmployeeServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmployeeServiceException(String message) {
        super(message);
    }

}
