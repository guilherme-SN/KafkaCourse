package com.guilherme.course.depositservice.events;

import java.math.BigDecimal;

public record DepositRequestedEvent(
        String senderId,
        String recipientId,
        BigDecimal amount
) {
}
