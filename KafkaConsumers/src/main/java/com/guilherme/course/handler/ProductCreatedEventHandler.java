package com.guilherme.course.handler;

import com.guilherme.course.events.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
/*
 * @KafkaListener pode estar na classe ou no método
 *  - Se estiver no método, então o método vai receber mensagem naquele tópico e vai converter para a classe que passar como parâmetro
 *  - Se estiver na classe, então todos os métodos vão receber mensagem naquele tópicos, mas cada um vai ser responsável
 *      por receber um determinado tipo de mensagem. Além disso, cada um dos métodos deverá ser anotado com @KafkaHandler
 */
@KafkaListener(topics = { "product-created-events" })
public class ProductCreatedEventHandler {
    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent) {
        log.info("Receiving new event: {}", productCreatedEvent.title());
    }
}
