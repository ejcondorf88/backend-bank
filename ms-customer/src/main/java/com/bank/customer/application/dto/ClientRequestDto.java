package com.bank.customer.application.dto;

import javax.validation.constraints.*;

/**
 * DTO para recibir datos de creación/actualización de Cliente.
 * Usado en la capa de aplicación para transferir datos desde los controllers.
 * Incluye validaciones Bean Validation.
 *
 * @author Backend Bank Team
 * @version 1.0.0
 */
public class ClientRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "Name can only contain letters and spaces")
    private String name;

    @Size(max = 20, message = "Gender cannot exceed 20 characters")
    private String gender;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 150, message = "Age cannot exceed 150")
    private Integer age;

    @NotBlank(message = "Identification is required")
    @Size(min = 5, max = 20, message = "Identification must be between 5 and 20 characters")
    private String identification;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Phone can only contain numbers")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 50, message = "Password must be between 4 and 50 characters")
    private String password;

    private Boolean active;

    public ClientRequestDto() {
    }

    public ClientRequestDto(String name, String gender, Integer age,
                            String identification, String address, String phone,
                            String password, Boolean active) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.identification = identification;
        this.address = address;
        this.phone = phone;
        this.password = password;
        this.active = active;
    }

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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

}
