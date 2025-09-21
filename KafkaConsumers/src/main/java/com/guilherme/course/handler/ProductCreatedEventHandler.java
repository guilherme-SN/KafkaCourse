package com.guilherme.course.handler;

import com.guilherme.course.events.ProductCreatedEvent;
import com.guilherme.course.exceptions.NotRetryableException;
import com.guilherme.course.exceptions.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClient;

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

    @KafkaHandler
    public void handle(
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,   // Captura a chave da mensagem recebida
            @Header(value = "messageId") String messageId,          // Captura o header "messageId" da mensagem recebida
            @Payload ProductCreatedEvent productCreatedEvent        // Captura o payload (conteúdo) da mensagem recebida

            // Outra solução é passar apenas um `ConsumerRecord<KeyType, ValueType> consumerRecord` no parâmetro,
            //  dessa forma, é possível acessar todos esses campos (headers, key, value, etc) pelo `consumerRecord`
    ) {
        log.info("Receiving new event: {}", productCreatedEvent.title());
        this.businessLogic(productCreatedEvent);
    }

    private void businessLogic(ProductCreatedEvent productCreatedEvent) {
        log.info("Processing product logic with ID: {}", productCreatedEvent.productId());

        try {
            webClient.get()
                    .uri("/response/500")
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
}
