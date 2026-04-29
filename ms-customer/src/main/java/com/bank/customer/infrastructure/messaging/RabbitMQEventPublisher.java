package com.bank.customer.infrastructure.messaging;

import com.bank.customer.domain.event.DomainEvent;
import com.bank.customer.domain.port.out.DomainEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementacion del puerto DomainEventPublisher usando RabbitMQ.
 * Este es un adaptador de infraestructura que conecta el dominio con RabbitMQ.
 */
@Component
public class RabbitMQEventPublisher implements DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate,
                                   @Value("${rabbitmq.exchange.customer.name:customer.events}") String exchangeName) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    @Override
    public void publish(DomainEvent event) {
        // Usa el tipo de evento como routing key por defecto
        String routingKey = "customer." + event.eventType().toLowerCase().replace("_", ".");
        publish(event, routingKey);
    }

    @Override
    public void publish(DomainEvent event, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
    }
}
