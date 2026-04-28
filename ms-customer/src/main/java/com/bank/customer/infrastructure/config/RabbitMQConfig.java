package com.bank.customer.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de RabbitMQ para el microservicio de clientes.
 * Define exchanges, queues y bindings para la publicacion de eventos de dominio.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.customer.name:customer.events}")
    private String exchangeName;

    @Value("${rabbitmq.exchange.customer.type:topic}")
    private String exchangeType;

    @Value("${rabbitmq.exchange.customer.durable:true}")
    private boolean exchangeDurable;

    @Value("${rabbitmq.queue.customer.name:customer.events.queue}")
    private String queueName;

    @Value("${rabbitmq.queue.customer.durable:true}")
    private boolean queueDurable;

    @Value("${rabbitmq.queue.customer.routing-key:customer.#}")
    private String routingKey;

    /**
     * Exchange de tipo topic para eventos de clientes.
     * Permite routing flexible basado en patrones.
     */
    @Bean
    public TopicExchange customerExchange() {
        return ExchangeBuilder
                .topicExchange(exchangeName)
                .durable(exchangeDurable)
                .build();
    }

    /**
     * Queue para consumir eventos de clientes (si se necesita en el futuro).
     * Por ahora se usa principalmente para publicacion.
     */
    @Bean
    public Queue customerQueue() {
        return QueueBuilder
                .durable(queueName)
                .build();
    }

    /**
     * Binding entre la queue y el exchange con el routing key.
     */
    @Bean
    public Binding customerBinding(Queue customerQueue, TopicExchange customerExchange) {
        return BindingBuilder
                .bind(customerQueue)
                .to(customerExchange)
                .with(routingKey);
    }

    /**
     * Message converter para serializar/deserializar eventos a JSON.
     * Usa Jackson para manejar records y otros tipos de Java.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate configurado con el JSON converter.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
