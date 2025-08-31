package com.ksv.consumer.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestTopicListener {
    private static final Logger log = LoggerFactory.getLogger(TestTopicListener.class);

    // reads plain String messages
    @KafkaListener(topics = "TestTopic", groupId = "ksv-consumer")
    public void onMessage(String message) {
        log.info("✅ Consumed: {}", message);
    }
}
