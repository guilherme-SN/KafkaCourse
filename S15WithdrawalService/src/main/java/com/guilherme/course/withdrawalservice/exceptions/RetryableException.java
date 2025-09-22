package com.guilherme.course.withdrawalservice.exceptions;

public class RetryableException extends RuntimeException {
    public RetryableException(Throwable cause) {
        super(cause);
    }
}
