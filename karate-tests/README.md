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
│   ├── karate-config.js                       # Configuracion global + callsingle
│   └── com/bank/karate/
│       ├── KarateTestRunner.java             # Runners JUnit5
│       └── features/
│           ├── common/
│           │   ├── setup.feature             # Utilidades reutilizables
│           │   ├── setup-once.feature        # Datos compartidos (callonce)
│           │   ├── init-data.feature         # Datos globales (callsingle)
│           │   ├── cleanup.feature           # Limpieza de datos
│           │   └── create-inactive-client.feature
│           ├── customer/
│           │   └── client-crud.feature       # Tests CRUD
│           ├── examples/
│           │   └── call-patterns.feature     # Ejemplos de call/callonce
│           └── e2e/
│               └── (end-to-end tests)
└── README.md                                  # Este documento
```

## Conceptos Clave

### Call vs CallOnce vs CallSingle

| Método | Alcance | Uso |
|--------|---------|-----|
| `call` | Cada escenario | Crear datos temporales, ejecutar lógica específica |
| `callonce` | Una vez por feature | Datos compartidos entre scenarios del mismo feature |
| `callsingle` | Una vez por suite | Datos globales de toda la ejecución (configurado en karate-config.js) |

#### Ejemplos:

```gherkin
# CALL - Se ejecuta en cada scenario
* def tempData = call read('helper.feature')

# CALLONCE - Se ejecuta UNA VEZ por feature
* def sharedData = callonce read('setup.feature')

# CALLSINGLE - Configurado en karate-config.js, disponible globalmente
* def globalData = testData  # Variable del callsingle
```

### Wildcards en Karate

Los wildcards permiten validaciones flexibles sin ser estrictos:

| Wildcard | Significado | Ejemplo |
|----------|-------------|---------|
| `##` | Campo opcional (puede ser null o no existir) | `{ name: '#string', gender: '##string' }` |
| `#ignore` | Ignorar campos adicionales | `{ name: '#string', "#ignore": "##" }` |
| `#? _` | Validación con expresión | `{ age: '#? _ >= 18' }` |
| `#[]` | Array vacío o con items | `match response == '#[] { id: "#number" }'` |
| `contains` | Validación parcial (solo campos especificados) | `match response contains { id: '#number' }` |
| `each` | Cada item cumple el criterio | `match each response[*] contains { id: '#number' }` |

#### Ejemplos de Wildcards:

```gherkin
# Campo opcional con ##
And match response == { id: '#number', name: '#string', email: '##string' }

# Validación con expresión
And match response.age == '#? _ >= 18 && _ <= 120'
And match response.name == '#? _.length > 0'

# Array vacío o con items
And match response == '#[]'  # Cualquier array
And match response == '#[2]' # Array con exactamente 2 items
And match response == '#[_ > 0]' # Array no vacío

# Contiene (match parcial)
And match response contains { id: 123, name: 'Juan' }
And match response contains { active: true }

# Cada elemento del array
And match each response contains { id: '#number', active: '#boolean' }
```

## Requisitos

- Java 17+
- Maven 3.6+
- Servicios backend corriendo

## Configuración

### Entornos (Environments)

Configurados en `karate-config.js`:

- **local** (default): Servicios en localhost
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

# Ejemplos de patterns
mvn test -Dtest=KarateTestRunner#runAllTests -Dkarate.options="classpath:com/bank/karate/features/examples"
```

### 3. Ejecutar por Tags

```bash
# Tests de creación
mvn test -Dkarate.options="--tags @create"

# Tests smoke
mvn test -Dkarate.options="--tags @smoke"

# Tests positivos de lectura
mvn test -Dkarate.options="--tags @read,@positive"
```

### 4. Ejecutar un Feature Específico

```bash
mvn test -Dkarate.options="classpath:com/bank/karate/features/examples/call-patterns.feature"
```

## Reportes

Después de ejecutar los tests, los reportes se generan en:

```
target/karate-reports/
├── karate-summary.html          # Resumen de ejecución
└── *.html                       # Reporte individual por feature
```

## Ejemplos de Uso Avanzado

### CallOnce para Datos Compartidos

```gherkin
Feature: Tests que reusan datos

Background:
  # Se ejecuta UNA SOLA VEZ para todo el feature
  * def shared = callonce read('setup.feature')
  * def sharedId = shared.clientId

@positive
Scenario: Usar cliente compartido
  # sharedId es el mismo para todos los scenarios
  Given url baseUrl + '/api/clients/' + sharedId
  When method get
  Then status 200

@positive
Scenario: Otro scenario con mismo cliente
  # Mismo sharedId que el anterior
  Given url baseUrl + '/api/clients/' + sharedId + '/activate'
  When method patch
```

### Wildcards para Validación Flexible

```gherkin
Scenario: Validacion flexible de respuesta
  When method get
  Then status 200
  
  # Schema con campos opcionales
  And match response == 
    """
    {
      id: '#number',
      name: '#string',
      email: '##string',           # Opcional
      phone: '##string',           # Opcional
      address: '##string',         # Opcional
      active: '#boolean'
    }
    """
  
  # Solo validar ciertos campos
  And match response contains { id: '#number', name: '#string' }
  
  # Validar expresiones
  And match response.age == '#? _ >= 18'
  And match response.name == '#? _.length >= 2'
```

### Call para Crear Datos Temporales

```gherkin
Scenario: Crear y usar datos temporales
  # Crear cliente temporal (se ejecuta en cada scenario)
  * def temp = call read('create-client.feature')
  
  # Usar el cliente creado
  Given url baseUrl + '/api/clients/' + temp.id
  When method get
  Then status 200
```

## Tags Disponibles

| Tag | Descripción |
|-----|-------------|
| `@smoke` | Tests críticos |
| `@customer` | Tests de ms-customer |
| `@crud` | Operaciones CRUD |
| `@create`, `@read`, `@update`, `@delete` | Por operación |
| `@positive`, `@negative` | Éxito vs error |
| `@callonce`, `@callsingle` | Demos de patterns |

## Recursos

- [Documentación oficial de Karate](https://karate.github.io/karate/)
- [GitHub - Karate Framework](https://github.com/karatelabs/karate)
- [Cheat Sheet](https://gist.github.com/ptrthomas/62fe77f1f1cc8d284b9fdc213cf21d68)

## Tips

1. **Usa callonce** para setup costoso que se reutiliza
2. **Usa callsingle** en karate-config.js para datos globales
3. **Wildcards ##** hacen tests más mantenibles
4. **contains** en lugar de `==` para validaciones parciales
5. **Siempre limpia** datos de prueba al final
