package com.guilherme.course.events;

import java.math.BigDecimal;

/*
 * Essa classe é a mesma que foi utilizada no KafkaProducers
 * Às vezes pode ser interessante criar um terceiro "serviço"/biblioteca que vai conter essa entidade
 * Dessa forma, não seria necessário ficar duplicando em todos os lugares o mesmo evento
 */
public record ProductCreatedEvent(
        String productId,
        String title,
        BigDecimal price,
        Integer quantity
) {
}
