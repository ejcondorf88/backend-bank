package com.bank.customer.domain.port.out;

import com.bank.customer.domain.event.DomainEvent;

/**
 * Puerto de salida del dominio para publicar eventos.
 * La implementacion concreta esta en la capa de infraestructura.
 * Esto sigue el principio de Dependency Inversion de Clean Architecture.
 */
public interface DomainEventPublisher {

    /**
     * Publica un evento de dominio al message broker.
     *
     * @param event el evento a publicar
     */
    void publish(DomainEvent event);

    /**
     * Publica un evento con un routing key especifico.
     *
     * @param event el evento a publicar
     * @param routingKey la clave de enrutamiento
     */
    void publish(DomainEvent event, String routingKey);
}