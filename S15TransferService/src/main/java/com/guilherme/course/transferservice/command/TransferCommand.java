package com.guilherme.course.transferservice.command;

import java.math.BigDecimal;

public record TransferCommand(
        String senderId,
        String recipientId,
        BigDecimal amount
) {
}
