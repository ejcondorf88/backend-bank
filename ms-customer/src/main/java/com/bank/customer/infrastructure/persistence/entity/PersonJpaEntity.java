package com.bank.customer.infrastructure.persistence.entity;

import jakarta.persistence.*;

/**
 * Entidad JPA para Persona.
 * Tabla separada de Client. Se relacionan via @OneToOne.
 * Contiene los datos personales basicos: nombre, genero, edad, identificacion, direccion, telefono.
 */
@Entity
@Table(name = "persons", schema = "customer")
public class PersonJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @SequenceGenerator(name = "person_seq", sequenceName = "customer.person_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "identification", nullable = false, length = 20, unique = true)
    private String identification;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    public PersonJpaEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getIdentification() { return identification; }
    public void setIdentification(String identification) { this.identification = identification; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}