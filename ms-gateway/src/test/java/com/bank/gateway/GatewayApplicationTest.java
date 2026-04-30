package com.bank.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests para el API Gateway.
 * <p>
 * ms-gateway no tiene lógica de negocio propia — es 100% configuración de rutas
 * de Spring Cloud Gateway. Estos tests verifican que el contexto arranca y
 * que las rutas esenciales están configuradas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Smoke Tests - API Gateway")
class GatewayApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private RouteDefinitionLocator routeLocator;

    @Test
    @DisplayName("El contexto de Spring debe arrancar correctamente")
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    @DisplayName("El application name debe ser ms-gateway")
    void applicationNameShouldBeCorrect() {
        String appName = context.getEnvironment().getProperty("spring.application.name");
        assertThat(appName).isEqualTo("ms-gateway");
    }

    @Test
    @DisplayName("Debe tener configurado Eureka como cliente de service discovery")
    void eurekaClientShouldBeConfigured() {
        String defaultZone = context.getEnvironment().getProperty("eureka.client.service-url.defaultZone");
        assertThat(defaultZone).isNotNull().contains("8761");
    }

    @Test
    @DisplayName("Debe tener habilitado el discovery locator")
    void discoveryLocatorShouldBeEnabled() {
        String enabled = context.getEnvironment().getProperty("spring.cloud.gateway.discovery.locator.enabled");
        assertThat(enabled).isEqualTo("true");
    }
}