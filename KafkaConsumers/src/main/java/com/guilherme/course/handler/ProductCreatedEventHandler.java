package com.guilherme.course.handler;

import com.guilherme.course.entity.ProcessedEventEntity;
import com.guilherme.course.events.ProductCreatedEvent;
import com.guilherme.course.exceptions.NotRetryableException;
import com.guilherme.course.exceptions.RetryableException;
import com.guilherme.course.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
/*
 * @KafkaListener pode estar na classe ou no método
 *  - Se estiver no método, então o método vai receber mensagem naquele tópico e vai converter para a classe que passar como parâmetro
 *  - Se estiver na classe, então todos os métodos vão receber mensagem naquele tópicos, mas cada um vai ser responsável
 *      por receber um determinado tipo de mensagem. Além disso, cada um dos métodos deverá ser anotado com @KafkaHandler
 */
@KafkaListener(topics = { "product-created-events" })
public class ProductCreatedEventHandler {
    private final WebClient webClient;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional      // Faz com que o JPA trate as operações no banco de dados como transações
    @KafkaHandler
    public void handle(
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,   // Captura a chave da mensagem recebida
            @Header(value = "messageId") String messageId,          // Captura o header "messageId" da mensagem recebida
            @Payload ProductCreatedEvent productCreatedEvent        // Captura o payload (conteúdo) da mensagem recebida

            // Outra solução é passar apenas um `ConsumerRecord<KeyType, ValueType> consumerRecord` no parâmetro,
            //  dessa forma, é possível acessar todos esses campos (headers, key, value, etc) pelo `consumerRecord`
    ) {
        log.info("Receiving new event for {}, with messageId: {}", productCreatedEvent.title(), messageId);

        Optional<ProcessedEventEntity> optionalProcessedEvent = processedEventRepository.findByMessageId(messageId);
        if (optionalProcessedEvent.isPresent()) {
            log.warn("Message already processed. Returning.");
            return;
        }

        this.businessLogic(productCreatedEvent);
        this.saveEvent(messageId, productCreatedEvent);
    }

    private void businessLogic(ProductCreatedEvent productCreatedEvent) {
        log.info("Processing product logic with productId: {}", productCreatedEvent.productId());

        try {
            webClient.get()
                    .uri("/response/200")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (ResourceAccessException ex) {
            log.error("[RETRYABLE ERROR]: {}", ex.getMessage());
            throw new RetryableException(ex);
        } catch (Exception ex) {
            log.error("[NOT RETRYABLE ERROR]: {}", ex.getMessage());
            throw new NotRetryableException(ex);
        }

        log.info("Finishing processing product with no errors");
    }

    private void saveEvent(String messageId, ProductCreatedEvent productCreatedEvent) {
        log.info("Trying to save event to database");

        try {
            processedEventRepository.save(
                    ProcessedEventEntity.builder()
                            .messageId(messageId)
                            .productId(productCreatedEvent.productId())
                            .build()
            );
        } catch (DataIntegrityViolationException ex) {
            log.error("[NOT RETRYABLE ERROR] while saving to database: {}", ex.getMessage());
            throw new NotRetryableException(ex);
        }

        log.info("Event successfully saved to database");
    }
}
