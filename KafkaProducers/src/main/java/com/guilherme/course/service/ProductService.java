package com.guilherme.course.service;

import com.guilherme.course.command.CreateProductCommand;
import com.guilherme.course.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    // Kafka template -> wrapper do Kafka Producer para mandar mensagens para os tópicos
    // É necessário especificar o tipo da chave e valor da mensagem:
    //      KafkaTemplate<TipoDaChave, TipoDoValor>
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public String createProductAsynchronously(CreateProductCommand command) {
        String productId = UUID.randomUUID().toString();

        // Lógica para persistir Product

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                command.title(),
                command.price(),
                command.quantity()
        );

        // Faz o envio de forma assíncrona
        // SendResult<TipoDaChave, TipoDoValor> -> classe que encapsula resultado do envio de uma mensagem
        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
                kafkaTemplate.send("product-created-events", productId, productCreatedEvent);

        // Callback que será executado quando a operação de cima finalizar
        future.whenComplete((result, exception) -> {
            if (exception != null) {    // Operação deu falha
                log.error("Failed to send message: ", exception);
            } else {    // Operação bem sucedida
                logMessageSentSuccessfully(result);
            }
        });

        // Bloqueia a thread atual até que o `future` seja finalizado
        // Torna a operação síncrona
        //future.join();

        logCreatedProduct(productId);

        return productId;
    }

    public String createProductSynchronously(CreateProductCommand command) throws Exception {
        String productId = UUID.randomUUID().toString();

        // Lógica para persistir Product

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                command.title(),
                command.price(),
                command.quantity()
        );

        // Faz o envio de forma síncrona
        // .get() é necessário para esperar o CompletableFuture finalizar de ser executado
        SendResult<String, ProductCreatedEvent> result = kafkaTemplate
                .send("product-created-events", productId, productCreatedEvent)
                .get();

        log.info("Offset: {}", result.getRecordMetadata().offset());
        log.info("Timestamp: {}", result.getRecordMetadata().timestamp());
        log.info("Serialized Key Size: {}", result.getRecordMetadata().serializedKeySize());
        log.info("Serialized Value Size: {}", result.getRecordMetadata().serializedValueSize());
        log.info("Topic Partition: {}\n", result.getRecordMetadata().partition());

        logMessageSentSuccessfully(result);
        logCreatedProduct(productId);

        return productId;
    }

    private void logMessageSentSuccessfully(SendResult<String, ProductCreatedEvent> result) {
        log.info("Message sent successfully: {}", result.getRecordMetadata());
    }

    private void logCreatedProduct(String productId) {
        log.info("Product created with ID: {}", productId);
    }
}
