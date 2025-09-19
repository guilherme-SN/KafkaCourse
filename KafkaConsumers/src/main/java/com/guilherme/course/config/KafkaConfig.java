package com.guilherme.course.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final Environment environment;

    /**
     * O ConsumerFactory é uma fábrica de consumers Kafka.
     * Ele:
     *  - Centraliza a criação de consumers com configurações consistentes
     *  - Reutiliza configurações para múltiplos consumers
     *  - Abstrai a complexidade de criar consumers Kafka manualmente
     *
     * Por que precisa existir?
     *  - O Spring Kafka precisa saber como criar consumers
     *  - Garante que todos os consumers usem as mesmas configurações base
     *  - Permite injeção de dependênciae gerenciamento pelo Spring
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() { // Similar ao ProducerFactory, mas para os consumidores
        Map<String, Object> config = new HashMap<>();

        String placeholder = "{}: {}";
        log.info(placeholder, ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        log.info(placeholder, ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"));
        log.info(placeholder, JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        log.info(placeholder, ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        log.info(placeholder, ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"));
        config.put(JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    /**
     * O KafkaListenerContainerFactory é o gerenciador dos containers que executam os @KafkaListener.
     * Ele:
     *  - Cria containers que executam os listeners em background
     *  - Gerencia threads para processar mensagens
     *  - Controla comportamentos como commits, error handling, retry, etc.
     *
     * Responsabilidades:
     *  - Thread pool management
     *  - Error handling
     *  - Offset commits
     *  - Connection management
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}
