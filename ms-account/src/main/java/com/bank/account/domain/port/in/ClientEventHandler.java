package com.bank.account.domain.port.in;

import com.bank.account.domain.event.ClientEvent;

public interface ClientEventHandler {

    void handleClientCreated(ClientEvent event);

    void handleClientUpdated(ClientEvent event);

    void handleClientDeleted(ClientEvent event);

    void handleClientActivated(ClientEvent event);

    void handleClientDeactivated(ClientEvent event);
}