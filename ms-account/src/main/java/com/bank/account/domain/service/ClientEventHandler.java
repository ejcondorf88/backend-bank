package com.bank.account.domain.service;

import com.bank.account.domain.event.ClientEvent;

/**
 * Servicio de dominio para manejar eventos de cliente.
 * Define el contrato para procesar eventos recibidos de otros microservicios.
 *
 * <p>Este es un Port (interfaz) que permite que la infraestructura
 * invoque lógica de dominio sin acoplarla a RabbitMQ.</p>
 */
public interface ClientEventHandler {

    /**
     * Procesa un evento de cliente creado.
     * Se invoca cuando ms-customer publica CLIENT_CREATED.
     *
     * @param event el evento recibido
     */
    void handleClientCreated(ClientEvent event);

    /**
     * Procesa un evento de cliente actualizado.
     * Se invoca cuando ms-customer publica CLIENT_UPDATED.
     *
     * @param event el evento recibido
     */
    void handleClientUpdated(ClientEvent event);

    /**
     * Procesa un evento de cliente eliminado.
     * Se invoca cuando ms-customer publica CLIENT_DELETED.
     *
     * @param event el evento recibido
     */
    void handleClientDeleted(ClientEvent event);

    /**
     * Procesa un evento de cliente activado.
     * Se invoca cuando ms-customer publica CLIENT_ACTIVATED.
     *
     * @param event el evento recibido
     */
    void handleClientActivated(ClientEvent event);

    /**
     * Procesa un evento de cliente desactivado.
     * Se invoca cuando ms-customer publica CLIENT_DEACTIVATED.
     *
     * @param event el evento recibido
     */
    void handleClientDeactivated(ClientEvent event);
}
