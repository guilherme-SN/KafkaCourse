package com.guilherme.course.config;

import com.guilherme.course.events.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {
    // As configurações que estão aqui tem maior prioridade do que as que estão no application.properties

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
    private String deliveryTimeoutMs;

    @Value("${spring.kafka.producer.properties.linger.ms}")
    private String lingerMs;

    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    private String requestTimeoutMs;

    /**
     * ProducerFactory é uma fábrica responsável por criar instâncias do KafkaProducer.
     *
     * O que é: É um componente do Spring Kafka que encapsula a configuração necessária
     * para criar producers do Kafka de forma padronizada e reutilizável.
     *
     * Para que serve:
     * 1. Centraliza todas as configurações do producer em um único local
     * 2. Permite reutilização das configurações para múltiplos producers
     * 3. Facilita a injeção de dependência e gerenciamento pelo Spring
     * 4. Abstrai a complexidade de configuração do KafkaProducer nativo
     *
     * Como funciona:
     * - Recebe um Map com todas as configurações do Kafka
     * - Quando solicitado, cria uma nova instância de KafkaProducer com essas configurações
     * - É tipado com <String, ProductCreatedEvent>, indicando que as mensagens terão
     *   chaves do tipo String e valores do tipo ProductCreatedEvent
     */
    @Bean
    public ProducerFactory<String, ProductCreatedEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        String placeholder = "{}: {}";
        log.info(placeholder, ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        log.info(placeholder, ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        log.info(placeholder, ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        log.info(placeholder, ProducerConfig.ACKS_CONFIG, acks);
        log.info(placeholder, ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        log.info(placeholder, ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        log.info(placeholder, ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        config.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        /*
         * Para idempotência funcionar nos producers, é necessário que:
         *  1. acks = all -> garante que a mensagem seja replicada para todas as réplicas, oferecendo durabilidade e
         *      evitando perda de mensagens
         *  2. retries > 0 -> necessário reenviar outras mensagens caso uma antiga não tenha sido escrita
         *  3. max.in.flight.requests.per.connection <= 5 -> para conseguir manter a ordenação das mensagens
         */
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * KafkaTemplate é um wrapper de alto nível para envio de mensagens ao Kafka.
     *
     * O que é: É a principal classe do Spring Kafka para envio de mensagens,
     * fornecendo uma interface simplificada e integrada ao ecossistema Spring.
     *
     * Para que serve:
     * 1. Abstrai a complexidade do KafkaProducer nativo do Apache Kafka
     * 2. Fornece métodos síncronos e assíncronos para envio de mensagens
     * 3. Integra-se com o sistema de transações do Spring
     * 4. Oferece tratamento de exceções padronizado
     * 5. Suporte a callbacks para sucesso/falha no envio
     * 6. Serialização automática dos objetos usando os serializers configurados
     *
     * Como usar:
     * - kafkaTemplate.send(tópico, chave, valor) para envio básico
     * - Retorna CompletableFuture<SendResult> para programação assíncrona
     * - Pode ser usado de forma síncrona com .get() no CompletableFuture
     *
     * Vantagens sobre KafkaProducer nativo:
     * - Integração com Spring (injeção de dependência, configuração, etc.)
     * - Métodos mais simples e intuitivos
     * - Melhor tratamento de erros
     * - Suporte a métricas e monitoring automático
     */
    @Bean
    public KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder
                .name("product-created-events")
                .partitions(3)
                .replicas(3)
                .config("min.insync.replicas", "2")
                .build();
    }
}
