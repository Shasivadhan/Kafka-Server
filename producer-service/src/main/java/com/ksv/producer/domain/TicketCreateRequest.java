package com.ksv.producer.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TicketCreateRequest(
        @NotBlank String customerId,
        @NotBlank String title,
        @Min(1) int priority
) {}
