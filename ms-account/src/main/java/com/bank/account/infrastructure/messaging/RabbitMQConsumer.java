package com.bank.account.infrastructure.messaging;

import com.bank.account.application.service.ClientEventHandlerImpl;
import com.bank.account.domain.event.ClientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Adaptador de infraestructura que consume eventos de RabbitMQ.
 * Escucha los eventos publicados por ms-customer y los redirige
 * al servicio de aplicación.
 *
 * <p>Este es un Adapter (Infrastructure Layer) que implementa el
 * consumo de mensajes sin acoplar al dominio a RabbitMQ.</p>
 *
 * <p>Eventos escuchados:
 * <ul>
 * <li>customer.created - CLIENT_CREATED</li>
 * <li>customer.updated - CLIENT_UPDATED</li>
 * <li>customer.deleted - CLIENT_DELETED</li>
 * <li>customer.activated - CLIENT_ACTIVATED</li>
 * <li>customer.deactivated - CLIENT_DEACTIVATED</li>
 * </ul>
 */
@Component
public class RabbitMQConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final ClientEventHandlerImpl clientEventHandler;

    public RabbitMQConsumer(ClientEventHandlerImpl clientEventHandler) {
        this.clientEventHandler = clientEventHandler;
    }

    /**
     * Listener para eventos de cliente.
     * Se suscribe a la queue 'customer.events.queue'.
     *
     * @param event el evento recibido desde RabbitMQ
     */
    @RabbitListener(
            queues = "${rabbitmq.queue.customer.name:customer.events.queue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void receiveCustomerEvent(@Payload ClientEvent event) {
        log.info("Evento recibido de ms-customer: type={}, clientId={}",
                event.eventType(), event.clientId());

        try {
            // Routing basado en el tipo de evento
            switch (event.eventType()) {
                case "CLIENT_CREATED":
                    log.info("Procesando CLIENT_CREATED para cliente {}", event.clientId());
                    clientEventHandler.handleClientCreated(event);
                    break;

                case "CLIENT_UPDATED":
                    log.info("Procesando CLIENT_UPDATED para cliente {}", event.clientId());
                    clientEventHandler.handleClientUpdated(event);
                    break;

                case "CLIENT_DELETED":
                    log.info("Procesando CLIENT_DELETED para cliente {}", event.clientId());
                    clientEventHandler.handleClientDeleted(event);
                    break;

                case "CLIENT_ACTIVATED":
                    log.info("Procesando CLIENT_ACTIVATED para cliente {}", event.clientId());
                    clientEventHandler.handleClientActivated(event);
                    break;

                case "CLIENT_DEACTIVATED":
                    log.info("Procesando CLIENT_DEACTIVATED para cliente {}", event.clientId());
                    clientEventHandler.handleClientDeactivated(event);
                    break;

                default:
                    log.warn("Tipo de evento desconocido: {}", event.eventType());
            }

            log.info("Evento {} procesado exitosamente", event.eventType());

        } catch (Exception e) {
            log.error("Error procesando evento {}: {}", event.eventType(), e.getMessage(), e);
            // Opcional: enviar a Dead Letter Queue (DLQ)
            // Opcional: reintentar con backoff
            throw e; // Re-lanzar para que RabbitMQ haga requeue
        }
    }
}
