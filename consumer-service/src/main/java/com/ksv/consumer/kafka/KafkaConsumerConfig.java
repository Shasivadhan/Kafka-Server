package com.ksv.consumer.kafka;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries; // ✅ correct


import java.time.Duration;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class KafkaConsumerConfig {

    @Value("${app.topics.ticket-created-dlt}")
    private String ticketCreatedDlt;

    @Bean
    public ConcurrentMap<String, Boolean> processedCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(10_000)
                .<String, Boolean>build()
                .asMap();
    }

    @Bean
    public DeadLetterPublishingRecoverer recoverer(KafkaTemplate<String, Object> template) {
        return new DeadLetterPublishingRecoverer(
                template,
                (record, ex) -> new TopicPartition(ticketCreatedDlt, record.partition())
        );
    }

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
        var backoff = new ExponentialBackOffWithMaxRetries(3);
        backoff.setInitialInterval(300L);
        backoff.setMultiplier(2.5);
        backoff.setMaxInterval(3000L);
        return new DefaultErrorHandler(recoverer, backoff);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        f.setConsumerFactory(consumerFactory);

        // ✅ Enforce MANUAL ack so Acknowledgment is injected into your listener
        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        f.setCommonErrorHandler(errorHandler);
        f.setConcurrency(3);
        return f;
    }
}
