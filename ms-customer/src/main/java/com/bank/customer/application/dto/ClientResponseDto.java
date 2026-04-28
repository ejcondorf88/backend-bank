package com.bank.customer.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta con los datos del cliente")
public class ClientResponseDto {

    @Schema(description = "ID interno del cliente en la base de datos", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre completo del cliente", example = "Juan Perez", accessMode = Schema.AccessMode.READ_ONLY)
    private String name;

    @Schema(description = "Genero del cliente", example = "Masculino", accessMode = Schema.AccessMode.READ_ONLY)
    private String gender;

    @Schema(description = "Edad del cliente", example = "30", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer age;

    @Schema(description = "Numero de identificacion del cliente", example = "1234567890", accessMode = Schema.AccessMode.READ_ONLY)
    private String identification;

    @Schema(description = "Direccion del cliente", example = "Av. Principal 123, Ciudad", accessMode = Schema.AccessMode.READ_ONLY)
    private String address;

    @Schema(description = "Numero de telefono del cliente", example = "0987654321", accessMode = Schema.AccessMode.READ_ONLY)
    private String phone;

    @Schema(description = "Estado activo del cliente", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean active;

    public ClientResponseDto() {
    }

    public ClientResponseDto(Long id, String name, String gender,
                             Integer age, String identification, String address,
                             String phone, Boolean active) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.identification = identification;
        this.address = address;
        this.phone = phone;
        this.active = active;
    }

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

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "ClientResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", identification='" + identification + '\'' +
                ", active=" + active +
                '}';
    }
}