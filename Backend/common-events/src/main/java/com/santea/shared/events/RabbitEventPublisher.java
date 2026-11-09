package com.santea.shared.events;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitEventPublisher {
    public static final String EXCHANGE = "santea.domain.events";

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(DomainEvent event) {
        rabbitTemplate.convertAndSend(EXCHANGE, event.eventType(), event);
    }
}
