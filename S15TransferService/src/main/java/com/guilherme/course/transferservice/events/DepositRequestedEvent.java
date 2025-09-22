package com.guilherme.course.transferservice.events;

import java.math.BigDecimal;

public record DepositRequestedEvent(
        String senderId,
        String recipientId,
        BigDecimal amount
) {
}
