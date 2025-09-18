package com.guilherme.course.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data                   // Getters e setters serão usados pelos Producers e Consumers para (des)serializar
@NoArgsConstructor      // Usado pelos Consumers na hora de desserializar
@AllArgsConstructor
public class ProductCreatedEvent {
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
