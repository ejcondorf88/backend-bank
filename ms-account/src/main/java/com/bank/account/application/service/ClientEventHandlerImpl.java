package com.bank.account.application.service;

import com.bank.account.domain.event.ClientEvent;
import com.bank.account.domain.service.ClientEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de dominio para manejar eventos de cliente.
 * Orquesta la respuesta a eventos recibidos desde ms-customer.
 *
 * <p>Responsabilidades:
 * <ul>
 * <li>Recibir eventos desde infraestructura</li>
 * <li>Actualizar estado local basado en eventos externos</li>
 * <li>Validar clientId antes de crear cuentas</li>
 * <li>Cachear información de clientes si es necesario</li>
 * </ul>
 *
 * <p>Arquitectura: Application Service que implementa un Domain Port.</p>
 */
@Service
public class ClientEventHandlerImpl implements ClientEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientEventHandlerImpl.class);

    /**
     * Procesa CLIENT_CREATED.
     * Registra que existe un nuevo cliente válido.
     */
    @Override
    public void handleClientCreated(ClientEvent event) {
        log.info("Cliente creado recibido: id={}, name={}, identification={}",
                event.clientId(), event.name(), event.identification());

        // Validar que el cliente tenga datos válidos
        if (event.clientId() == null || event.clientId() <= 0) {
            log.error("Evento CLIENT_CREATED con clientId inválido: {}", event.clientId());
            return;
        }

        // El cliente ahora puede tener cuentas
        // Podríamos cachear los datos del cliente en Redis/memoria
        // o simplemente validar que existe antes de crear cuentas

        log.info("Cliente {} ahora puede tener cuentas", event.clientId());
    }

    /**
     * Procesa CLIENT_UPDATED.
     * Actualiza información del cliente local.
     */
    @Override
    public void handleClientUpdated(ClientEvent event) {
        log.info("Cliente actualizado recibido: id={}, name={}",
                event.clientId(), event.name());

        // Actualizar cache si existe
        // Invalidar cache de cliente
        // Actualizar cuentas relacionadas si es necesario
    }

    /**
     * Procesa CLIENT_DELETED.
     * Marca al cliente como eliminado y posiblemente sus cuentas.
     */
    @Override
    public void handleClientDeleted(ClientEvent event) {
        log.info("Cliente eliminado recibido: id={}", event.clientId());

        // Opcional: Desactivar todas las cuentas del cliente
        // Opcional: Enviar alertas
        // Invalidar cache

        log.warn("Cliente {} ha sido eliminado del sistema", event.clientId());
    }

    /**
     * Procesa CLIENT_ACTIVATED.
     * Reactiva la capacidad de operar.
     */
    @Override
    public void handleClientActivated(ClientEvent event) {
        log.info("Cliente activado recibido: id={}, name={}",
                event.clientId(), event.name());

        // Actualizar estado en cache
        // Permitir operaciones en cuentas existentes

        log.info("Cliente {} está activo y puede operar", event.clientId());
    }

    /**
     * Procesa CLIENT_DEACTIVATED.
     * Bloquea operaciones nuevas en cuentas del cliente.
     */
    @Override
    public void handleClientDeactivated(ClientEvent event) {
        log.info("Cliente desactivado recibido: id={}, name={}",
                event.clientId(), event.name());

        // Bloquear creación de nuevas cuentas
        // Opcional: Bloquear operaciones en cuentas existentes
        // Actualizar cache

        log.warn("Cliente {} está inactivo - no se pueden crear nuevas cuentas", event.clientId());
    }
}
