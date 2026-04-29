package com.bank.account.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio de cuentas.
 * Define la conexión, conversión de mensajes y listener containers.
 *
 * <p>Como consumers, necesitamos:
 * <ul>
 * <li>Conexión a RabbitMQ</li>
 * <li>Queue para escuchar eventos de clientes</li>
 * <li>Binding al exchange de customer</li>
 * <li>Jackson converter para deserializar JSON</li>
 * <li>Listener container factory</li>
 * </ul>
 *
 * <p>Nota: El exchange y queue son definidos por ms-customer.
 * Nosotros solo nos conectamos a ellos.</p>
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.host:localhost}")
    private String host;

    @Value("${rabbitmq.port:5672}")
    private int port;

    @Value("${rabbitmq.username:guest}")
    private String username;

    @Value("${rabbitmq.password:guest}")
    private String password;

    @Value("${rabbitmq.exchange.customer.name:customer.events}")
    private String customerExchangeName;

    @Value("${rabbitmq.queue.customer.name:customer.events.queue}")
    private String customerQueueName;

    @Value("${rabbitmq.queue.customer.routing-key:customer.#}")
    private String routingKey;

    /**
     * Declaración del exchange de customer.
     * Como es durable, se crea si no existe.
     */
    @Bean
    public TopicExchange customerExchange() {
        return ExchangeBuilder
                .topicExchange(customerExchangeName)
                .durable(true)
                .build();
    }

    /**
     * Declaración de la queue de customer.
     * Necesitamos declararla para poder consumir de ella.
     */
    @Bean
    public Queue customerQueue() {
        return QueueBuilder
                .durable(customerQueueName)
                .build();
    }

    /**
     * Binding entre la queue y el exchange.
     * Escucha todos los mensajes que empiecen con "customer."
     */
    @Bean
    public Binding customerBinding(Queue customerQueue, TopicExchange customerExchange) {
        return BindingBuilder
                .bind(customerQueue)
                .to(customerExchange)
                .with(routingKey);
    }

    /**
     * Message converter para deserializar JSON a objetos.
     * Usa Jackson para convertir JSON → ClientEvent.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate configurado con el JSON converter.
     * Aunque somos consumers, el template puede ser útil para
     * enviar mensajes de confirmación si es necesario.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                        MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    /**
     * Factory para los listeners de RabbitMQ.
     * Configura el deserializador JSON para los mensajes entrantes.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(messageConverter);

        // Configuración adicional
        factory.setConcurrentConsumers(3);  // 3 consumidores concurrentes
        factory.setMaxConcurrentConsumers(10);  // Hasta 10 en peak
        factory.setPrefetchCount(1);  // Un mensaje a la vez por consumer

        return factory;
    }
}
