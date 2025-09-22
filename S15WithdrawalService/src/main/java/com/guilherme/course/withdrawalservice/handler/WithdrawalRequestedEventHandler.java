package com.guilherme.course.withdrawalservice.handler;

import com.guilherme.course.withdrawalservice.events.WithdrawalRequestedEvent;
import com.guilherme.course.withdrawalservice.util.ConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = ConstantUtil.WITHDRAW_MONEY_TOPIC)
public class WithdrawalRequestedEventHandler {
    @KafkaHandler
    public void handle(@Payload WithdrawalRequestedEvent event) {
        log.info("Received a new withdrawal event: {}", event.amount());
    }
}
