package com.ksv.producer.domain;

import java.time.Instant;

public record TicketCreatedEvent(
        String eventId,
        String customerId,
        String title,
        int priority,
        Instant createdAt
) {}
