# Karate Framework - API Tests

Proyecto de pruebas de API para el sistema **Backend Bank** usando [Karate Framework](https://karate.github.io/karate/).

## ¿Qué es Karate?

Karate es un framework de testing de APIs open-source que permite:
- Tests de HTTP/API de forma simple y declarativa
- Validaciones de JSON/XML con sintaxis natural
- Reutilización de código entre tests
- Reportes HTML integrados
- Tests de contratos (schema validation)
- Tests de performance (integrado con Gatling)

## Estructura del Proyecto

```
karate-tests/
├── pom.xml                                    # Maven configuration
├── src/test/java/
│   ├── karate-config.js                       # Configuración global
│   └── com/bank/karate/
│       ├── KarateTestRunner.java             # Runners JUnit5
│       └── features/
│           ├── common/
│           │   └── setup.feature             # Utilidades reutilizables
│           ├── customer/
│           │   └── client-crud.feature       # Tests CRUD de clientes
│           └── e2e/
│               └── (end-to-end tests)
└── target/
    └── karate-reports/                       # Reportes HTML
```

## Requisitos

- Java 17+
- Maven 3.6+
- Servicios backend corriendo (ms-customer en puerto 8081, o Gateway en 8080)

## Configuración

### Entornos (Environments)

El proyecto soporta múltiples entornos configurados en `karate-config.js`:

- **local** (default): `localhost:8081`
- **dev**: Entorno de desarrollo
- **test**: Entorno de pruebas

### Ejecutar con un entorno específico:

```bash
# Local (por defecto)
mvn test

# Desarrollo
mvn test -Dkarate.env=dev

# Testing
mvn test -Dkarate.env=test
```

## Cómo Ejecutar

### 1. Ejecutar Todos los Tests

```bash
cd karate-tests
mvn clean test
```

### 2. Ejecutar Tests Específicos

```bash
# Solo tests de cliente
mvn test -Dtest=KarateTestRunner#runCustomerTests

# Solo smoke tests
mvn test -Dtest=KarateTestRunner#runSmokeTests

# Solo tests end-to-end
mvn test -Dtest=KarateTestRunner#runE2ETests
```

### 3. Ejecutar por Tags

```bash
# Tests de creación
mvn test -Dkarate.options="--tags @create"

# Tests smoke
mvn test -Dkarate.options="--tags @smoke"

# Tests de lectura positivos
mvn test -Dkarate.options="--tags @read,@positive"
```

## Reportes

Después de ejecutar los tests, los reportes se generan en:

```
target/karate-reports/
├── karate-summary.html          # Resumen de ejecución
└── *.html                       # Reporte individual por feature
```

Abrir `karate-summary.html` en el navegador para ver los resultados.

## Estructura de un Feature

```gherkin
Feature: Customer Service - Client CRUD Operations

Background:
  * call read('classpath:com/bank/karate/features/common/setup.feature')
  * url customerServiceUrl

@smoke @create @positive
Scenario: Crear un cliente exitosamente
  # Generar datos
  * def testClient = generateTestClient()
  
  # Ejecutar request
  Given request testClient
  When method post
  
  # Validar respuesta
  Then status 201
  And match response == clientSchema
  And match response.name == testClient.name
  And match response.active == true
  
  # Cleanup
  * def createdId = response.id
  Given url customerServiceUrl + '/api/clients/' + createdId
  When method delete
```

## Tags Disponibles

| Tag | Descripción |
|-----|-------------|
| `@smoke` | Tests críticos que deben pasar siempre |
| `@customer` | Tests del microservicio de clientes |
| `@crud` | Tests de operaciones CRUD |
| `@create` | Tests de creación |
| `@read` | Tests de consulta |
| `@update` | Tests de actualización |
| `@delete` | Tests de eliminación |
| `@activate` | Tests de activación |
| `@deactivate` | Tests de desactivación |
| `@positive` | Casos de éxito |
| `@negative` | Casos de error |
| `@e2e` | Tests end-to-end |
| `@ignore` | Tests que se saltan |

## Utilidades Disponibles

### En `setup.feature`:

- `jsonHeaders` - Headers JSON estándar
- `clientSchema` - Schema de validación de cliente
- `errorSchema` - Schema de validación de error
- `generateTestClient()` - Genera datos de cliente aleatorios
- `extractIdFromResponse(response)` - Extrae ID de respuesta
- `contains(str, substr)` - Verifica si contiene substring

### Variables de Configuración:

- `customerServiceUrl` - URL base del servicio de clientes
- `baseUrl` - URL del Gateway (si aplica)
- `timeout` - Timeout de conexión

## Ejemplo de Flujo Completo

```gherkin
Feature: Flujo completo de cliente

Scenario: Crear, actualizar y eliminar cliente
  # Crear
  * def client = generateTestClient()
  Given url customerServiceUrl + '/api/clients'
  And request client
  When method post
  Then status 201
  * def clientId = response.id
  
  # Actualizar
  Given url customerServiceUrl + '/api/clients/' + clientId
  And request { name: 'Updated', ...client }
  When method put
  Then status 200
  And match response.name == 'Updated'
  
  # Desactivar
  Given url customerServiceUrl + '/api/clients/' + clientId + '/deactivate'
  When method patch
  Then status 200
  
  # Eliminar
  Given url customerServiceUrl + '/api/clients/' + clientId
  When method delete
  Then status 204
```

## Integración con CI/CD

```yaml
# Ejemplo para GitHub Actions
- name: Run Karate Tests
  run: |
    cd karate-tests
    mvn clean test -Dkarate.env=test
    
- name: Upload Reports
  uses: actions/upload-artifact@v3
  with:
    name: karate-reports
    path: karate-tests/target/karate-reports/
```

## Recursos

- [Documentación oficial de Karate](https://karate.github.io/karate/)
- [GitHub - Karate Framework](https://github.com/karatelabs/karate)
- [Cheat Sheet de Karate](https://gist.github.com/ptrthomas/62fe77f1f1cc8d284b9fdc213cf21d68)

## Tips

1. **Siempre limpiar datos de prueba**: Cada test debe limpiar los recursos creados
2. **Usar datos aleatorios**: Evitar colisiones entre tests con `generateTestClient()`
3. **Tags consistentes**: Facilita filtrar y ejecutar subconjuntos
4. **Schemas reutilizables**: Definir en `setup.feature` para mantener consistencia
