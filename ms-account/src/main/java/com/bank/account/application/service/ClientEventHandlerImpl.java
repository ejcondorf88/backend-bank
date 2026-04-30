package com.bank.account.application.service;

import com.bank.account.domain.event.ClientEvent;
import com.bank.account.domain.port.in.ClientEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementación del servicio de dominio para manejar eventos de cliente.
 * Orquesta la respuesta a eventos recibidos desde ms-customer via RabbitMQ.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Recibir eventos desde infraestructura</li>
 *   <li>Mantener proyección local de nombres de clientes (para F4 — reportes)</li>
 *   <li>Actualizar estado local basado en eventos externos</li>
 * </ul>
 *
 * <p>Arquitectura: Application Service que implementa un Domain Port.</p>
 */
@Service
public class ClientEventHandlerImpl implements ClientEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientEventHandlerImpl.class);

    /**
     * Proyección local: clientId → nombre del cliente.
     * Bean ConcurrentHashMap inyectado desde RabbitMQConfig.
     * Compartido con ReportApplicationService para resolver nombres en reportes F4.
     *
     * <p>En producción esto sería Redis o una tabla local de proyección SQL.
     */
    private final Map<Long, String> clientNameCache;

    public ClientEventHandlerImpl(Map<Long, String> clientNameCache) {
        this.clientNameCache = clientNameCache;
    }

    /**
     * Procesa CLIENT_CREATED.
     * Almacena nombre en proyección local — disponible para reportes F4.
     */
    @Override
    public void handleClientCreated(ClientEvent event) {
        log.info("Evento CLIENT_CREATED: id={}, name={}, identification={}",
                event.clientId(), event.name(), event.identification());

        if (event.clientId() == null || event.clientId() <= 0) {
            log.error("Evento CLIENT_CREATED con clientId inválido: {}", event.clientId());
            return;
        }

        clientNameCache.put(event.clientId(), event.name());
        log.info("Proyección local actualizada: cliente {} → '{}'", event.clientId(), event.name());
    }

    /**
     * Procesa CLIENT_UPDATED.
     * Actualiza nombre en proyección local.
     */
    @Override
    public void handleClientUpdated(ClientEvent event) {
        log.info("Evento CLIENT_UPDATED: id={}, name={}", event.clientId(), event.name());

        if (event.clientId() != null && event.name() != null) {
            clientNameCache.put(event.clientId(), event.name());
            log.info("Proyección local actualizada: cliente {} → '{}'", event.clientId(), event.name());
        }
    }

    /**
     * Procesa CLIENT_DELETED.
     * Elimina cliente de la proyección local.
     */
    @Override
    public void handleClientDeleted(ClientEvent event) {
        log.info("Evento CLIENT_DELETED: id={}", event.clientId());

        clientNameCache.remove(event.clientId());
        log.warn("Cliente {} eliminado de la proyección local", event.clientId());
    }

    /**
     * Procesa CLIENT_ACTIVATED.
     * Mantiene/restaura cliente en la proyección local.
     */
    @Override
    public void handleClientActivated(ClientEvent event) {
        log.info("Evento CLIENT_ACTIVATED: id={}, name={}", event.clientId(), event.name());

        if (event.clientId() != null && event.name() != null) {
            clientNameCache.put(event.clientId(), event.name());
        }
        log.info("Cliente {} activado — operaciones habilitadas", event.clientId());
    }

    /**
     * Procesa CLIENT_DEACTIVATED.
     * Mantiene al cliente en cache (existe, solo inactivo).
     */
    @Override
    public void handleClientDeactivated(ClientEvent event) {
        log.info("Evento CLIENT_DEACTIVATED: id={}, name={}", event.clientId(), event.name());

        if (event.clientId() != null && event.name() != null) {
            clientNameCache.put(event.clientId(), event.name());
        }
        log.warn("Cliente {} desactivado — no se pueden crear nuevas cuentas", event.clientId());
    }
}
