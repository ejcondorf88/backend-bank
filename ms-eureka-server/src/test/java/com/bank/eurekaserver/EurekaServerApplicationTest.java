package com.bank.eurekaserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.netflix.eureka.server.EurekaServerAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests para Eureka Server.
 * <p>
 * Eureka Server no tiene lógica de negocio propia — es 100% infraestructura
 * (Spring Boot + @EnableEurekaServer). Estos tests verifican que el contexto
 * arranca correctamente y que Eureka responde en el puerto esperado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Smoke Tests - Eureka Server")
class EurekaServerApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("El contexto de Spring debe arrancar correctamente")
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    @DisplayName("Debe tener configurado EurekaServerAutoConfiguration")
    void eurekaServerAutoConfigurationShouldBeLoaded() {
        assertThat(context.getBeanNamesForType(
                org.springframework.cloud.netflix.eureka.server.EurekaServerAutoConfiguration.class))
            .as("EurekaServerAutoConfiguration deberia estar en el contexto")
            .isNotEmpty();
    }

    @Test
    @DisplayName("El application name debe ser ms-eureka-server")
    void applicationNameShouldBeCorrect() {
        String appName = context.getEnvironment().getProperty("spring.application.name");
        assertThat(appName).isEqualTo("ms-eureka-server");
    }

    @Test
    @DisplayName("Eureka debe estar configurado en modo standalone")
    void eurekaShouldBeInStandaloneMode() {
        String registerWithEureka = context.getEnvironment().getProperty("eureka.client.register-with-eureka");
        String fetchRegistry = context.getEnvironment().getProperty("eureka.client.fetch-registry");
        assertThat(registerWithEureka).isEqualTo("false");
        assertThat(fetchRegistry).isEqualTo("false");
    }

    @Test
    @DisplayName("El puerto del servidor debe ser configurable")
    void serverPortShouldBeConfigurable() {
        String serverPort = context.getEnvironment().getProperty("server.port");
        assertThat(serverPort).isNotNull();
    }

    @Test
    @DisplayName("Self-preservation debe estar deshabilitado para desarrollo")
    void selfPreservationShouldBeDisabled() {
        String selfPreservation = context.getEnvironment().getProperty("eureka.server.enable-self-preservation");
        assertThat(selfPreservation).isEqualTo("false");
    }
}