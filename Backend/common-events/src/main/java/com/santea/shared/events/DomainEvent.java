package com.santea.shared.events;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DomainEvent(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        String aggregateId,
        Map<String, Object> payload
) {
    public static DomainEvent of(String eventType, String aggregateId, Map<String, Object> payload) {
        return new DomainEvent(UUID.randomUUID(), eventType, Instant.now(), aggregateId, payload);
    }
}
