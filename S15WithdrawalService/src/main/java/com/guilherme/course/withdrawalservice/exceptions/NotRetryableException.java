package com.guilherme.course.withdrawalservice.exceptions;

public class NotRetryableException extends RuntimeException {
    public NotRetryableException(Throwable cause) {
        super(cause);
    }
}
