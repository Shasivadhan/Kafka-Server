package com.ksv.producer.kafka;

import com.ksv.producer.domain.TicketCreatedEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class EventPublisher {

    private final KafkaTemplate<String, Object> template;

    @Value("${app.topics.ticket-created}")
    private String topic;

    // Constructor injection for KafkaTemplate
    public EventPublisher(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    // Publish method that sends event to Kafka
    public String publish(TicketCreatedEvent event) {
        // Generate a correlationId (unique for each message)
        String correlationId = UUID.randomUUID().toString();

        // Generate a traceId (shorter version for tracing logs)
        String traceId = "trace-" + correlationId.substring(0, 8);

        // Create a ProducerRecord (represents a Kafka message)
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                topic,
                event.customerId(),   // Key: customerId
                event                 // Value: event object
        );

        // Add custom headers to the Kafka message
        record.headers()
                .add(new RecordHeader("eventVersion", "v1".getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("correlationId", correlationId.getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("traceId", traceId.getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader("priority", Integer.toString(event.priority()).getBytes(StandardCharsets.UTF_8)));

        // Send the message using KafkaTemplate
        template.send(record);

        // Return correlationId so caller can track this message
        return correlationId;
    }
}
