package com.guilherme.course.depositservice.exceptions;

public class NotRetryableException extends RuntimeException {
    public NotRetryableException(Throwable cause) {
        super(cause);
    }
}
