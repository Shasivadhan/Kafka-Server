package com.ksv.producer.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicsConfig {

    @Value("${app.topics.ticket-created}")
    private String created;

    @Value("${app.topics.ticket-created-dlt}")
    private String dlt;

    // Create main topic for ticket-created events
    @Bean
    public NewTopic t1() {
        // 3 partitions, replication factor = 1
        return new NewTopic(created, 3, (short) 1);
    }

    // Create dead-letter topic for failed messages
    @Bean
    public NewTopic t2() {
        // 3 partitions, replication factor = 1
        return new NewTopic(dlt, 3, (short) 1);
    }
}
