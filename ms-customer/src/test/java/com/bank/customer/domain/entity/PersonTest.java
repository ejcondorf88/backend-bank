package com.bank.customer.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del dominio - Entidad Person
 * Verifica logica de negocio pura sin infraestructura
 */
@DisplayName("Pruebas Unitarias - Entidad Person")
class PersonTest {

    @Test
    @DisplayName("Debe crear persona con datos validos")
    void shouldCreatePersonWithValidData() {
        Person person = new Person(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785"
        );

        assertNotNull(person);
        assertEquals("Jose Lema", person.getName());
        assertEquals("Masculino", person.getGender());
        assertEquals(35, person.getAge());
        assertEquals("1720456325", person.getIdentification());
        assertEquals("Otavalo sn y principal", person.getAddress());
        assertEquals("098254785", person.getPhone());
    }

    @Test
    @DisplayName("Debe normalizar nombre eliminando espacios extras")
    void shouldNormalizeNameRemovingExtraSpaces() {
        Person person = new Person(
            "  Jose   Lema  ",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785"
        );

        assertEquals("Jose Lema", person.getName());
    }

    @Test
    @DisplayName("Debe aceptar nombres con tildes y caracteres especiales")
    void shouldAcceptNamesWithAccents() {
        Person person = new Person(
            "Maria Fernandez",
            "Femenino",
            28,
            "0926548751",
            "Amazonas y NNUU",
            "097548965"
        );

        assertEquals("Maria Fernandez", person.getName());
    }

    @Test
    @DisplayName("Debe retornar true cuando es adulto")
    void shouldReturnTrueWhenIsAdult() {
        Person person = new Person(
            "Jose Lema",
            "Masculino",
            18,
            "1720456325",
            "Otavalo sn y principal",
            "098254785"
        );

        assertTrue(person.isAdult());
    }

    @Test
    @DisplayName("Debe considerar igualdos personas con misma identificacion")
    void shouldConsiderEqualPersonsWithSameIdentification() {
        Person person1 = new Person(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otavalo sn y principal",
            "098254785"
        );

        Person person2 = new Person(
            "Jose Lema",
            "Masculino",
            35,
            "1720456325",
            "Otra direccion",
            "099999999"
        );

        assertEquals(person1, person2);
        assertEquals(person1.hashCode(), person2.hashCode());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando nombre es nulo")
    void shouldThrowExceptionWhenNameIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Person(
                null,
                "Masculino",
                35,
                "1720456325",
                "Otavalo sn y principal",
                "098254785"
            );
        });

        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando nombre esta vacio")
    void shouldThrowExceptionWhenNameIsEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Person(
                "   ",
                "Masculino",
                35,
                "1720456325",
                "Otavalo sn y principal",
                "098254785"
            );
        });

        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando nombre tiene menos de 2 caracteres")
    void shouldThrowExceptionWhenNameTooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Person(
                "A",
                "Masculino",
                35,
                "1720456325",
                "Otavalo sn y principal",
                "098254785"
            );
        });

        assertEquals("Name must be at least 2 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando nombre contiene numeros")
    void shouldThrowExceptionWhenNameContainsNumbers() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Person(
                "Jose123",
                "Masculino",
                35,
                "1720456325",
                "Otavalo sn y principal",
                "098254785"
            );
        });

        assertEquals("Name can only contain letters", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando edad es nula")
    void shouldThrowExceptionWhenAgeIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Person(
                "Jose Lema",
                "Masculino",
                null,
                "1720456325",
                "Otavalo sn y principal",
                "098254785"
            );
        });

        assertEquals("Age is required", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando edad es menor a 18")
    void shouldThrowExceptionWhenAgeIsUnderMinimum() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Person(
                "Jose Lema",
                "Masculino",
                17,
                "1720456325",
                "Otavalo sn y principal",
                "098254785"
            );
        });

        assertEquals("Must be at least 18 years old", exception.getMessage());
    }
}
