package com.guilherme.course.exceptions;

import java.util.Date;

public record ErrorMessage(
        Date timestamp,
        String message,
        String details
) {
}
