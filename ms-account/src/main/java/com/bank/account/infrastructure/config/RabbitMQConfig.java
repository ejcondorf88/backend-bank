package com.bank.account.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuración de RabbitMQ para ms-account.
 *
 * <p>Este microservicio actúa como CONSUMIDOR de eventos publicados por ms-customer.
 * Escucha la queue 'customer.events.queue' para recibir eventos de Cliente.
 *
 * <p>También provee el bean clientNameCache (ConcurrentHashMap) compartido
 * entre RabbitMQConsumer, ClientEventHandlerImpl y ReportApplicationService
 * para mantener una proyección local de nombres de clientes.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.customer.name:customer.events.queue}")
    private String customerQueueName;

    @Value("${rabbitmq.exchange.customer.name:customer.events}")
    private String customerExchangeName;

    // ==================== Shared Client Name Cache ====================

    /**
     * Proyección local de nombres de clientes.
     * Se actualiza en tiempo real cuando ms-customer publica eventos via RabbitMQ.
     * Compartido por: ClientEventHandlerImpl, ReportApplicationService.
     *
     * <p>En producción, esto sería Redis o una tabla local de proyección.
     */
    @Bean
    public Map<Long, String> clientNameCache() {
        return new ConcurrentHashMap<>();
    }

    // ==================== Message Converter ====================

    /**
     * Converter JSON para deserializar mensajes de RabbitMQ.
     * Usado para convertir el payload a ClientEvent.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * RabbitTemplate configurado con JSON converter.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * Container factory para @RabbitListener.
     * Configura el Jackson converter para que deserialice ClientEvent desde JSON.
     * Requerido explícitamente para que el @RabbitListener en RabbitMQConsumer
     * pueda convertir el mensaje a ClientEvent (record Java).
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    // ==================== Queue & Exchange (Consumer) ====================

    /**
     * Queue donde ms-account escucha los eventos de ms-customer.
     * Durable: sobrevive reinicios de RabbitMQ.
     */
    @Bean
    public Queue customerEventsQueue() {
        return QueueBuilder.durable(customerQueueName).build();
    }

    /**
     * Exchange del que ms-account recibe eventos.
     * Debe coincidir con el Exchange configurado en ms-customer.
     */
    @Bean
    public TopicExchange customerExchange() {
        return new TopicExchange(customerExchangeName);
    }

    /**
     * Binding: conecta la queue del consumidor al exchange de ms-customer.
     * Routing key "#" → acepta todos los eventos de cliente.
     */
    @Bean
    public Binding customerEventsBinding(Queue customerEventsQueue, TopicExchange customerExchange) {
        return BindingBuilder
                .bind(customerEventsQueue)
                .to(customerExchange)
                .with("customer.#");
    }
}
