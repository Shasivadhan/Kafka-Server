package com.ksv.producer.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    // ProducerFactory: Creates Kafka Producer instances with given configuration
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        // 1. Kafka Broker (where messages will be sent)
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);

        // 2. Serializers: Convert key & value into bytes
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Avoid adding type information headers (makes JSON clean)
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        // 3. Custom Partitioner (ReverseKeyPartitioner distributes messages by key)
        config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, ReverseKeyPartitioner.class);

        // 4. Reliability configs
        config.put(ProducerConfig.ACKS_CONFIG, "all");              // Strong delivery guarantee
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Avoid duplicate messages

        return new DefaultKafkaProducerFactory<>(config);
    }

    // KafkaTemplate: Used to send messages to Kafka topics
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
