package com.guilherme.course.depositservice.handler;

import com.guilherme.course.depositservice.events.DepositRequestedEvent;
import com.guilherme.course.depositservice.util.ConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = ConstantUtil.DEPOSIT_MONEY_TOPIC)
public class DepositRequestedEventHandler {
    @KafkaHandler
    public void handle(DepositRequestedEvent event) {
        log.info("Received a new deposit event: {}", event.amount());
    }
}
