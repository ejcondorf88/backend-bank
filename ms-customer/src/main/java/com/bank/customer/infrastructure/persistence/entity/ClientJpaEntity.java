package com.bank.customer.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clients", schema = "customer")
public class ClientJpaEntity extends PersonJpaEntity {

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
