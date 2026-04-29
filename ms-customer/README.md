# 👤 ms-customer

## Descripción

**ms-customer** es el microservicio de **Gestión de Clientes** del sistema Backend Bank. Implementa el CRUD completo de clientes con arquitectura hexagonal y publicación de eventos de dominio mediante RabbitMQ.

## Funcionalidades (F1 - CRUD de Clientes)

| Funcionalidad | Endpoint | Descripción |
|---------------|----------|-------------|
| **Crear cliente** | `POST /api/clients` | Registrar nuevo cliente |
| **Obtener cliente** | `GET /api/clients/{id}` | Buscar cliente por ID |
| **Listar clientes** | `GET /api/clients` | Obtener todos los clientes |
| **Actualizar cliente** | `PUT /api/clients/{id}` | Modificar datos del cliente |
| **Eliminar cliente** | `DELETE /api/clients/{id}` | Eliminar cliente (si no tiene cuentas) |
| **Activar cliente** | `PATCH /api/clients/{id}/activate` | Activar cliente inactivo |
| **Desactivar cliente** | `PATCH /api/clients/{id}/deactivate` | Desactivar cliente activo |

## Arquitectura Hexagonal

Este microservicio implementa **Arquitectura Hexagonal (Ports & Adapters)** con tres capas bien definidas:

```
┌─────────────────────────────────────────────────────────────┐
│              👤 ms-customer (Port: 8081)                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  💙 INFRASTRUCTURE (Adaptadores)                            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ • REST Controllers (@RestController)                │   │
│  │ • JPA Entities (@Entity)                             │   │
│  │ • JPA Repositories                                  │   │
│  │ • RabbitMQ Event Publisher                          │   │
│  │ • Global Exception Handler                          │   │
│  └─────────────────────────────────────────────────────┘   │
│                          ▲                                  │
│                          │ (usa/implementa)                  │
│  🧡 APPLICATION          │                                  │
│  ┌───────────────────────▼─────────────────────────────┐   │
│  │                                                     │   │
│  │ • Application Services (@Service)                  │   │
│  │   - ClientApplicationService                        │   │
│  │                                                     │   │
│  │ • DTOs (Request/Response)                          │   │
│  │   - ClientRequestDto (validaciones)                 │   │
│  │   - ClientResponseDto                               │   │
│  │                                                     │   │
│  │ • Mappers (MapStruct)                              │   │
│  │   - ClientApplicationMapper                         │   │
│  │                                                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                          ▲                                  │
│                          │ (usa)                             │
│  💛 DOMAIN               │                                  │
│  ┌───────────────────────▼─────────────────────────────┐   │
│  │                                                     │   │
│  │ • Entities (Puras, sin Spring)                   │   │
│  │   - Person (atributos comunes)                    │   │
│  │   - Client (hereda de Person)                     │   │
│  │                                                     │   │
│  │ • Repository Ports (Interfaces)                    │   │
│  │   - ClientRepository                              │   │
│  │                                                     │   │
│  │ • Domain Services                                  │   │
│  │   - ClientService (lógica de negocio)            │   │
│  │                                                     │   │
│  │ • Domain Exceptions                                │   │
│  │   - ClientNotFoundException                       │   │
│  │   - ClientAlreadyExistsException                  │   │
│  │   - InvalidClientStateException                   │   │
│  │                                                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Las 3 Capas Explicadas

### 💛 Capa de Dominio (Core)

**Responsabilidad**: Contener la lógica de negocio pura de clientes.

**Contenido**:
- **Person**: Clase base con atributos comunes (nombre, género, edad, identificación, dirección, teléfono)
- **Client**: Hereda de Person, agrega contraseña y estado (activo/inactivo)
- **ClientRepository**: Interface que define operaciones de persistencia (findById, save, etc.)
- **ClientService**: Interface de servicio de dominio
- **Excepciones**: Errores específicos del negocio

**Reglas de Negocio**:
1. Identificación debe ser única
2. Campos obligatorios: nombre, identificación, contraseña
3. Cliente puede activarse/desactivarse
4. No se puede eliminar cliente activo (debe desactivarse primero)

**Características Clave**:
- ❌ **Sin dependencias de Spring**: No usa @Entity, @Service, @Autowired
- ❌ **Sin dependencias de JPA**: No importa jakarta.persistence.*
- ✅ **Solo Java SE**: Clases puras con lógica de negocio
- ✅ **Validaciones**: En constructores y métodos de negocio

**Ejemplo de Regla**:
```java
// Dominio: Cliente activo no puede eliminarse
public void validateDeletion() {
    if (this.active) {
        throw new InvalidClientStateException(
            "Cannot delete active client. Deactivate first."
        );
    }
}
```

### 🧡 Capa de Aplicación (Casos de Uso)

**Responsabilidad**: Orquestar los casos de uso del sistema.

**Contenido**:
- **ClientApplicationService**: Coordina operaciones CRUD
- **ClientRequestDto**: Define estructura de entrada con validaciones (@NotBlank, @Size)
- **ClientResponseDto**: Define estructura de salida
- **ClientApplicationMapper**: Convierte entre DTOs y Entidades de Dominio

**Flujo de Crear Cliente**:
```
1. Recibir ClientRequestDto del Controller
   └─ Validar con Bean Validation (@Valid)

2. Verificar duplicados
   └─ ¿Existe cliente con esta identificación?
   └─ Si sí → throw ClientAlreadyExistsException

3. Mapear DTO → Dominio
   └─ ClientRequestDto → Client (entity)

4. Guardar en repositorio
   └─ Llamar a ClientRepository.save(client)

5. Publicar evento (RabbitMQ)
   └─ Enviar mensaje CLIENTE_CREADO

6. Mapear resultado → DTO
   └─ Client (entity) → ClientResponseDto

7. Retornar al Controller
```

### 💙 Capa de Infraestructura (Adaptadores)

**Responsabilidad**: Conectar el dominio con el mundo exterior.

**Componentes**:

#### REST Controllers
- **ClientController**: Expone endpoints HTTP
- Usa Swagger/OpenAPI para documentación
- Maneja HTTP 201 (Created), 200 (OK), 404 (Not Found), etc.

#### Persistencia (JPA)
- **PersonJpaEntity**: Mapea a tabla "persons"
- **ClientJpaEntity**: Mapea a tabla "clients", hereda de Person
- **ClientJpaRepository**: Extends JpaRepository, queries personalizadas
- **ClientRepositoryImpl**: Implementa ClientRepository (dominio)
- **ClientJpaMapper**: Convierte entre JPA Entities y Dominio

#### Mensajería (RabbitMQ)
- **RabbitMQEventPublisher**: Publica eventos de dominio
- **RabbitMQConfig**: Configura exchanges, queues, bindings

#### Manejo de Errores
- **GlobalExceptionHandler**: Captura excepciones y devuelve HTTP apropiado
  - ClientNotFoundException → 404
  - ClientAlreadyExistsException → 409
  - InvalidClientStateException → 400

## Modelo de Dominio

### Jerarquía de Clases

```
Person (clase abstracta)
├── name: String
├── gender: String
├── age: int
├── identification: String (único)
├── address: String
├── phone: String
└── clientId: Long (relación)

Client (extiende Person)
├── password: String
├── active: boolean
└── hereda todos los atributos de Person
```

**Razón**: Un Cliente ES una Persona (relación "is-a"). En BD es una tabla unificada con discriminador.

## Eventos de Dominio (RabbitMQ)

### Patrón Event-Driven

Cuando ocurren cambios en clientes, publicamos eventos para que otros servicios reaccionen:

| Evento | Descripción | Payload |
|--------|-------------|---------|
| **CLIENTE_CREADO** | Nuevo cliente registrado | { id, name, identification, ... } |
| **CLIENTE_ACTUALIZADO** | Datos del cliente cambiaron | { id, cambios } |
| **CLIENTE_ELIMINADO** | Cliente eliminado del sistema | { id } |

### Flujo de Eventos

```
1. Cliente creado exitosamente
   ↓
2. Application Service llama a RabbitMQEventPublisher
   ↓
3. Publisher envía mensaje a Exchange "client.events"
   ↓
4. Exchange enruta a Queue "client.created"
   ↓
5. Otros servicios (ej: ms-account) pueden consumir el evento
   ↓
6. Ejemplo: Auditoría, Notificaciones, Estadísticas
```

### Configuración RabbitMQ

```yaml
# Exchange para eventos de cliente
exchange: client.exchange
type: topic
durable: true

# Routing keys
- client.created    → CLIENTE_CREADO
- client.updated    → CLIENTE_ACTUALIZADO
- client.deleted    → CLIENTE_ELIMINADO
```

## Endpoints REST

### Crear Cliente
```http
POST /api/clients
Content-Type: application/json

{
  "name": "Jose Lema",
  "gender": "Masculino",
  "age": 35,
  "identification": "1720456325",
  "address": "Otavalo sn y principal",
  "phone": "098254785",
  "password": "1234",
  "active": true
}

Respuesta: 201 Created
{
  "id": 1,
  "name": "Jose Lema",
  ...
}
```

### Obtener Cliente
```http
GET /api/clients/1

Respuesta: 200 OK
{
  "id": 1,
  "name": "Jose Lema",
  "identification": "1720456325",
  ...
}
```

### Buscar por Identificación
```http
GET /api/clients/identification/1720456325

Respuesta: 200 OK
{ ... }
```

### Listar Clientes
```http
GET /api/clients

Respuesta: 200 OK
[
  { "id": 1, "name": "Jose Lema", ... },
  { "id": 2, "name": "Maria Perez", ... }
]
```

### Listar Clientes Activos
```http
GET /api/clients/active

Respuesta: 200 OK
[ ... ]
```

### Actualizar Cliente
```http
PUT /api/clients/1
Content-Type: application/json

{
  "name": "Jose Lema Actualizado",
  "address": "Nueva direccion"
}

Respuesta: 200 OK
{ ... }
```

### Eliminar Cliente
```http
DELETE /api/clients/1

Respuesta: 204 No Content

Error (si está activo): 400 Bad Request
{ "message": "Cannot delete active client" }
```

### Activar Cliente
```http
PATCH /api/clients/1/activate

Respuesta: 200 OK
{ ..., "active": true }
```

### Desactivar Cliente
```http
PATCH /api/clients/1/deactivate

Respuesta: 200 OK
{ ..., "active": false }
```

## Validaciones

### Bean Validation (Request DTO)

| Campo | Validación | Mensaje Error |
|-------|------------|---------------|
| name | @NotBlank | "El nombre es obligatorio" |
| identification | @NotBlank, @Size(min=5) | "Identificación inválida" |
| password | @NotBlank, @Size(min=4) | "Contraseña muy corta" |
| age | @Min(18) | "Debe ser mayor de edad" |

## Tests

### Tests Unitarios

| Tipo | Cantidad | Descripción |
|------|----------|-------------|
| **Domain Entity Tests** | ~15 | Person, Client, validaciones |
| **Domain Service Tests** | ~10 | ClientServiceImpl |
| **Application Service Tests** | ~20 | ClientApplicationService |
| **Total** | ~45 | Cobertura dominio: 80%+ |

### Tests de Integración (Karate)

Ubicación: `karate-tests/src/test/java/.../customer/`

**Escenarios**:
- Crear cliente con datos válidos
- Crear cliente duplicado (409)
- Cliente no encontrado (404)
- Eliminar cliente activo (400)
- Activar/desactivar cliente

## Docker

```dockerfile
# Multi-stage build
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B
RUN java -Djarmode=layertools -jar target/*.jar extract

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -s /bin/sh -D appuser
WORKDIR /app
COPY --from=builder --chown=appuser:appgroup /build/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /build/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /build/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /build/application/ ./
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

## Dependencias

| Servicio | Uso |
|----------|-----|
| **Eureka** | Registro del servicio |
| **PostgreSQL** | Persistencia de clientes |
| **RabbitMQ** | Publicación de eventos |
| **Gateway** | Enrutamiento de peticiones |

## Puertos

| Puerto | Uso |
|--------|-----|
| `8081` | API REST y Swagger |

## Accesos

| Recurso | URL |
|---------|-----|
| API REST | http://localhost:8081/api/clients |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| Health | http://localhost:8081/actuator/health |

## Tecnologías

| Tecnología | Uso |
|------------|-----|
| **Spring Boot** | Framework base |
| **Spring Data JPA** | Persistencia |
| **Spring AMQP** | RabbitMQ |
| **MapStruct** | Mapeo DTOs |
| **OpenAPI/Swagger** | Documentación API |
| **PostgreSQL** | Base de datos |
| **RabbitMQ** | Mensajería |

## Valor Agregado de la Arquitectura

### Para Desarrolladores

- **Tests rápidos**: Dominio puro se testea sin Spring ni BD
- **Refactoring seguro**: Cambios en infraestructura no rompen dominio
- **Claridad**: Separación de responsabilidades clara

### Para el Negocio

- **Escalabilidad**: Eventos permiten procesamiento asíncrono
- **Auditoría**: Eventos de dominio facilitan compliance
- **Integración**: Fácil conectar nuevos servicios consumiendo eventos

## Referencias

- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [RabbitMQ with Spring](https://docs.spring.io/spring-amqp/docs/current/reference/html/)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
