package com.santea.notification;

import com.santea.shared.events.RabbitEventPublisher;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationQueueConfiguration {
    @Bean
    Queue notificationsQueue() {
        return new Queue("santea.notifications", true);
    }

    @Bean
    Binding notificationEventsBinding(Queue notificationsQueue, TopicExchange santeaDomainEventsExchange) {
        return BindingBuilder.bind(notificationsQueue)
                .to(santeaDomainEventsExchange)
                .with("#");
    }
}
