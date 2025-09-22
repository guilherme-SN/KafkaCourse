package com.guilherme.course.transferservice.service;

import com.guilherme.course.transferservice.command.TransferCommand;
import com.guilherme.course.transferservice.events.DepositRequestedEvent;
import com.guilherme.course.transferservice.events.WithdrawalRequestedEvent;
import com.guilherme.course.transferservice.exceptions.TransferServiceException;
import com.guilherme.course.transferservice.util.ConstantUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient webClient;

    public boolean transfer(TransferCommand command) {
        WithdrawalRequestedEvent withdrawalEvent = new WithdrawalRequestedEvent(
                command.senderId(),
                command.recipientId(),
                command.amount()
        );

        DepositRequestedEvent depositEvent = new DepositRequestedEvent(
                command.senderId(),
                command.recipientId(),
                command.amount()
        );

        try {
            kafkaTemplate.send(ConstantUtil.WITHDRAW_MONEY_TOPIC, withdrawalEvent);
            log.info("Sent event to withdrawal topic");

            this.callRemoteService();

            kafkaTemplate.send(ConstantUtil.DEPOSIT_MONEY_TOPIC, depositEvent);
            log.info("Sent event to deposit topic");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new TransferServiceException(ex);
        }

        return true;
    }

    private void callRemoteService() {
        String response = webClient.get()
                .uri("/response/200")
                .retrieve()
                .onStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        clientResponse -> Mono.error(new Exception("Destination Microservice not available"))
                )
                .bodyToMono(String.class)
                .block();

        log.info("Received response from mock service: {}", response);
    }
}
