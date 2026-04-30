package com.bank.customer.infrastructure.persistence.entity;

import jakarta.persistence.*;

/**
 * Entidad JPA para Cliente.
 * Se relaciona con PersonJpaEntity via @OneToOne (tablas separadas).
 * Contiene solo los campos especificos del cliente: password, active.
 * Los datos personales (nombre, identificacion, etc.) estan en PersonJpaEntity.
 */
@Entity
@Table(name = "clients", schema = "customer")
public class ClientJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "id", referencedColumnName = "id")
    @MapsId
    private PersonJpaEntity person;

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active;

    public ClientJpaEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PersonJpaEntity getPerson() { return person; }
    public void setPerson(PersonJpaEntity person) { this.person = person; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}