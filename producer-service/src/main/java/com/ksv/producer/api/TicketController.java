package com.ksv.producer.api;

import com.ksv.producer.domain.TicketCreateRequest;
import com.ksv.producer.domain.TicketCreatedEvent;
import com.ksv.producer.kafka.EventPublisher;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final EventPublisher publisher;

    @Value("${app.topics.ticket-created}")
    private String topic;

    public TicketController(EventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TicketCreateRequest req) {
        var event = new TicketCreatedEvent(
                UUID.randomUUID().toString(),
                req.customerId(),
                req.title(),
                req.priority(),
                Instant.now()
        );

        String correlationId = publisher.publish(event);

        return ResponseEntity.accepted().body(
                Map.of(
                        "topic", topic,
                        "eventId", event.eventId(),
                        "correlationId", correlationId
                )
        );
    }
}
