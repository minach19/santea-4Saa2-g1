package com.santea.notification;

import com.santea.shared.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DomainEventListener {
    private static final Logger logger = LoggerFactory.getLogger(DomainEventListener.class);

    @RabbitListener(queues = "santea.notifications")
    public void onEvent(DomainEvent event) {
        logger.info("Received event type={} eventId={} aggregateId={}",
                event.eventType(), event.eventId(), event.aggregateId());
    }
}
