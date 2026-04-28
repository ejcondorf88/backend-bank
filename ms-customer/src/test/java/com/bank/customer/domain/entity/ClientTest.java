package com.bank.customer.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del dominio - Entidad Client
 * Verifica logica de negocio pura sin infraestructura
 * Solo prueba comportamiento del dominio, no integracion
 */
@DisplayName("Pruebas Unitarias - Entidad Client")
class ClientTest {

    @Test
    @DisplayName("Debe crear cliente con datos validos")
    void shouldCreateClientWithValidData() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        assertNotNull(client);
        assertEquals("Jose Lema", client.getName());
        assertEquals("Masculino", client.getGender());
        assertEquals(35, client.getAge());
        assertEquals("1720456325", client.getIdentification());
        assertEquals("Otavalo sn y principal", client.getAddress());
        assertEquals("098254785", client.getPhone());
        assertEquals("1234", client.getPassword());
        assertTrue(client.isActive());
    }

    @Test
    @DisplayName("Debe crear cliente con estado activo por defecto")
    void shouldCreateClientWithActiveTrue() {
        Client client = new Client(
            "Maria Montalvo",
            "Femenino",
            28,
            "0926548751",
            "Amazonas y NNUU",
            "097548965",
            "password123",
            true
        );

        assertTrue(client.isActive());
    }

    @Test
    @DisplayName("Debe crear cliente con estado inactivo")
    void shouldCreateClientWithActiveFalse() {
        Client client = new Client(
            "Juan Perez",
            "Masculino",
            45,
            "1234567890",
            "Calle Principal 123",
            "0999999999",
            "clave123",
            false
        );

        assertFalse(client.isActive());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando password es nula")
    void shouldThrowExceptionWhenPasswordIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Client(
                "Jose Lema",
                "Masculino",
                35,
                "1720456325",
                "Otavalo sn y principal",
                "098254785",
                null,
                true
            );
        });

        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando password esta vacia")
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Client(
                "Jose Lema",
                "Masculino",
                35,
                "1720456325",
                "Otavalo sn y principal",
                "098254785",
                "   ",
                true
            );
        });

        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando password tiene menos de 4 caracteres")
    void shouldThrowExceptionWhenPasswordTooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Client(
                "Jose Lema",
                "Masculino",
                35,
                "1720456325",
                "Otavalo sn y principal",
                "098254785",
                "123",
                true
            );
        });

        assertEquals("Password must be at least 4 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando password excede 50 caracteres")
    void shouldThrowExceptionWhenPasswordTooLong() {
        String longPassword = "a".repeat(51);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Client(
                "Jose Lema",
                "Masculino",
                35,
                "1720456325",
                "Otavalo sn y principal",
                "098254785",
                longPassword,
                true
            );
        });

        assertEquals("Password cannot exceed 50 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Debe permitir password exactamente de 4 caracteres")
    void shouldAllowPasswordExactlyFourCharacters() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        assertEquals("1234", client.getPassword());
    }

    @Test
    @DisplayName("Debe permitir password exactamente de 50 caracteres")
    void shouldAllowPasswordExactlyFiftyCharacters() {
        String fiftyCharPassword = "a".repeat(50);

        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            fiftyCharPassword,
            true
        );

        assertEquals(fiftyCharPassword, client.getPassword());
    }

    @Test
    @DisplayName("Debe heredar validaciones de Person")
    void shouldInheritPersonValidations() {
        // Si el nombre es invalido, debe lanzar excepcion de Person
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Client(
                "",
                "Masculino",
                35,
                "1720456325",
                "Otavalo sn y principal",
                "098254785",
                "1234",
                true
            );
        });

        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    @DisplayName("Debe heredar validaciones de edad de Person")
    void shouldInheritAgeValidations() {
        // Si la edad es menor a 18, debe lanzar excepcion de Person
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Client(
                "Jose Lema",
                "Masculino",
                17,
                "1720456325",
                "Otavalo sn y principal",
                "098254785",
                "1234",
                true
            );
        });

        assertEquals("Must be at least 18 years old", exception.getMessage());
    }

    @Test
    @DisplayName("Debe poder cambiar estado a activo")
    void shouldChangeStateToActive() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            false
        );

        assertFalse(client.isActive());

        client.setActive(true);

        assertTrue(client.isActive());
    }

    @Test
    @DisplayName("Debe poder cambiar estado a inactivo")
    void shouldChangeStateToInactive() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        assertTrue(client.isActive());

        client.setActive(false);

        assertFalse(client.isActive());
    }

    @Test
    @DisplayName("Debe poder cambiar password")
    void shouldChangePassword() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        assertEquals("1234", client.getPassword());

        client.changePassword("nuevaClave123");

        assertEquals("nuevaClave123", client.getPassword());
    }

    @Test
    @DisplayName("Debe lanzar excepcion al cambiar password por una invalida")
    void shouldThrowExceptionWhenChangingToInvalidPassword() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            client.changePassword("12");
        });

        assertEquals("Password must be at least 4 characters long", exception.getMessage());
        // El password original debe mantenerse
        assertEquals("1234", client.getPassword());
    }

    @Test
    @DisplayName("Debe considerar iguales clientes con misma identificacion")
    void shouldConsiderEqualClientsWithSameIdentification() {
        Client client1 = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        Client client2 = new Client(
            "Jose Lema Actualizado",
            "Masculino",
            36,
            "1720456325",
            "Nueva direccion",
            "0999999999",
            "5678",
            false
        );

        assertEquals(client1, client2);
        assertEquals(client1.hashCode(), client2.hashCode());
    }

    @Test
    @DisplayName("Debe considerar diferentes clientes con distinta identificacion")
    void shouldConsiderDifferentClientsWithDifferentIdentification() {
        Client client1 = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        Client client2 = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "9999999999",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        assertNotEquals(client1, client2);
    }

    @Test
    @DisplayName("Debe permitir comparar con null")
    void shouldHandleNullComparison() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        assertNotEquals(client, null);
    }

    @Test
    @DisplayName("Debe permitir comparar con otra clase")
    void shouldHandleDifferentClassComparison() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        assertNotEquals(client, "un string");
    }

    @Test
    @DisplayName("Debe retornar true para mismo objeto en equals")
    void shouldReturnTrueForSameObject() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        assertEquals(client, client);
    }

    @Test
    @DisplayName("Debe incluir password en toString")
    void shouldIncludePasswordInToString() {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785",
            "1234",
            true
        );

        String toString = client.toString();

        assertTrue(toString.contains("Client{"));
        assertTrue(toString.contains("Jose Lema"));
        assertTrue(toString.contains("1720456325"));
    }
}
