package com.bank.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de rutas del API Gateway.
 * <p>
 * Verifica que las rutas definidas en application.yml se cargan correctamente
 * en el RouteDefinitionLocator y que los paths esperados están presentes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Route Tests - API Gateway")
class GatewayRoutesTest {

    @Autowired
    private RouteDefinitionLocator routeLocator;

    private List<RouteDefinition> getRoutes() {
        return routeLocator.getRouteDefinitions().collectList().block();
    }

    private RouteDefinition findRouteById(List<RouteDefinition> routes, String id) {
        return routes.stream()
                .filter(r -> id.equals(r.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No se encontró la ruta: " + id));
    }

    @Test
    @DisplayName("Debe tener definidas exactamente 3 rutas")
    void shouldHaveThreeRoutes() {
        List<RouteDefinition> routes = getRoutes();

        assertThat(routes)
                .as("Deben existir 3 rutas: ms-customer, ms-account, ms-account-movements")
                .hasSize(3);
    }

    @Test
    @DisplayName("La ruta ms-customer debe apuntar a /api/clients/** via lb://ms-customer")
    void customerRouteShouldPointToMsCustomer() {
        List<RouteDefinition> routes = getRoutes();
        RouteDefinition customerRoute = findRouteById(routes, "ms-customer");

        assertThat(customerRoute.getUri().toString()).isEqualTo("lb://ms-customer");
        assertThat(customerRoute.getPredicates())
                .anyMatch(p -> p.getArgs().containsValue("/api/clients/**"));
    }

    @Test
    @DisplayName("La ruta ms-account debe apuntar a /api/accounts/** via lb://ms-account")
    void accountRouteShouldPointToMsAccount() {
        List<RouteDefinition> routes = getRoutes();
        RouteDefinition accountRoute = findRouteById(routes, "ms-account");

        assertThat(accountRoute.getUri().toString()).isEqualTo("lb://ms-account");
        assertThat(accountRoute.getPredicates())
                .anyMatch(p -> p.getArgs().containsValue("/api/accounts/**"));
    }

    @Test
    @DisplayName("La ruta de movimientos debe apuntar a ms-account con /api/movements/**")
    void movementsRouteShouldPointToMsAccount() {
        List<RouteDefinition> routes = getRoutes();
        RouteDefinition movementsRoute = findRouteById(routes, "ms-account-movements");

        assertThat(movementsRoute.getUri().toString())
                .as("La ruta de movimientos debe apuntar a ms-account via load balancer")
                .isEqualTo("lb://ms-account");
        assertThat(movementsRoute.getPredicates())
                .as("Debe tener un predicate Path para /api/movements/**")
                .anyMatch(p -> p.getArgs().containsValue("/api/movements/**"));
    }

    @Test
    @DisplayName("Todas las rutas deben usar el esquema lb:// (load balancer)")
    void allRoutesShouldUseLoadBalancerScheme() {
        List<RouteDefinition> routes = getRoutes();

        List<String> nonLbRoutes = routes.stream()
                .filter(r -> !r.getUri().toString().startsWith("lb://"))
                .map(r -> r.getId() + " -> " + r.getUri())
                .collect(Collectors.toList());

        assertThat(nonLbRoutes)
                .as("Todas las rutas deben usar lb://. Rutas que no: " + nonLbRoutes)
                .isEmpty();
    }

    @Test
    @DisplayName("Cada ruta debe tener al menos un predicate de tipo Path")
    void eachRouteShouldHavePathPredicate() {
        List<RouteDefinition> routes = getRoutes();

        for (RouteDefinition route : routes) {
            boolean hasPathPredicate = route.getPredicates().stream()
                    .anyMatch(p -> "Path".equals(p.getName()));

            assertThat(hasPathPredicate)
                    .as("La ruta '%s' debe tener un predicate Path", route.getId())
                    .isTrue();
        }
    }

    @Test
    @DisplayName("No deben existir rutas duplicadas (mismo id)")
    void shouldNotHaveDuplicateRouteIds() {
        List<RouteDefinition> routes = getRoutes();

        List<String> ids = routes.stream()
                .map(RouteDefinition::getId)
                .collect(Collectors.toList());

        assertThat(ids)
                .as("Los IDs de ruta no deben estar duplicados")
                .doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Cada ruta debe tener un URI no nulo")
    void eachRouteShouldHaveNonNullUri() {
        List<RouteDefinition> routes = getRoutes();

        for (RouteDefinition route : routes) {
            assertThat(route.getUri())
                    .as("La ruta '%s' debe tener un URI definido", route.getId())
                    .isNotNull();
        }
    }
}