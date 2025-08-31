package com.ksv.consumer.kafka;

import com.ksv.consumer.domain.TicketCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Component
public class TicketEventListener {

    private final Map<String, Boolean> processedCache;

    public TicketEventListener(Map<String, Boolean> processedCache) {
        this.processedCache = processedCache;
    }

    @KafkaListener(
            topics = "${app.topics.ticket-created}",
            containerFactory = "kafkaListenerContainerFactory"   // 👈 make sure we use the manual-ack factory
    )
    public void onTicket(
            TicketCreatedEvent event,
            ConsumerRecord<String, TicketCreatedEvent> record,
            Acknowledgment ack
    ) {
        if (event.eventId() == null || event.eventId().isBlank()) {
            throw new IllegalArgumentException("eventId missing");
        }

        // Idempotency: skip duplicates
        if (processedCache.putIfAbsent(event.eventId(), Boolean.TRUE) != null) {
            System.out.printf("Duplicate %s%n", event.eventId());
            ack.acknowledge();
            return;
        }

        String corr = header(record, "correlationId").orElse("n/a");
        String pr = header(record, "priority").orElse("0");

        System.out.printf("[consumer] id=%s corr=%s key=%s p=%s title=%s%n",
                event.eventId(), corr, record.key(), pr, event.title());

        // Simulate failure for DLT demo
        if (Integer.parseInt(pr) >= 5 && event.title().toLowerCase().contains("fail")) {
            throw new RuntimeException("Simulated failure");
        }

        // ✅ commit only after successful processing
        ack.acknowledge();
    }

    private Optional<String> header(ConsumerRecord<?, ?> rec, String name) {
        Header h = rec.headers().lastHeader(name);
        if (h == null) return Optional.empty();
        return Optional.of(new String(h.value(), StandardCharsets.UTF_8));
    }
}
