package com.guilherme.course.withdrawalservice.events;

import java.math.BigDecimal;

public record WithdrawalRequestedEvent(
        String senderId,
        String recipientId,
        BigDecimal amount
) {
}
