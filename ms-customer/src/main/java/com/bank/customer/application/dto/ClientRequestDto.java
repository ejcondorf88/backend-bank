package com.bank.customer.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para la creacion y actualizacion de clientes")
public class ClientRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "Name can only contain letters and spaces")
    @Schema(description = "Nombre completo del cliente", example = "Juan Perez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 20, message = "Gender cannot exceed 20 characters")
    @Schema(description = "Genero del cliente", example = "Masculino", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String gender;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 150, message = "Age cannot exceed 150")
    @Schema(description = "Edad del cliente (debe ser mayor o igual a 18)", example = "30", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "18", maximum = "150")
    private Integer age;

    @NotBlank(message = "Identification is required")
    @Size(min = 5, max = 20, message = "Identification must be between 5 and 20 characters")
    @Schema(description = "Numero de identificacion unico del cliente", example = "1234567890", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 5, maxLength = 20)
    private String identification;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Schema(description = "Direccion del cliente", example = "Av. Principal 123, Ciudad", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 255)
    private String address;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Phone can only contain numbers")
    @Schema(description = "Numero de telefono del cliente (solo numeros)", example = "0987654321", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 20)
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 50, message = "Password must be between 4 and 50 characters")
    @Schema(description = "Contrasena del cliente (minimo 4 caracteres)", example = "secret123", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 4, maxLength = 50)
    private String password;

    @Schema(description = "Estado activo/inactivo del cliente", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
