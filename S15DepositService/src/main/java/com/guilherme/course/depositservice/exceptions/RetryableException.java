package com.guilherme.course.depositservice.exceptions;

public class RetryableException extends RuntimeException {
    public RetryableException(Throwable cause) {
        super(cause);
    }
}
