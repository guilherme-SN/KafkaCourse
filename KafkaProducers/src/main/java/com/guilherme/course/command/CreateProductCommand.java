package com.guilherme.course.command;

import java.math.BigDecimal;

public record CreateProductCommand(
        String title,
        BigDecimal price,
        Integer quantity
) {
}
