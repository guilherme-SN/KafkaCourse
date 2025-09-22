package com.guilherme.course.transferservice.events;

import java.math.BigDecimal;

public record WithdrawalRequestedEvent(
        String senderId,
        String recipientId,
        BigDecimal amount
) {
}
