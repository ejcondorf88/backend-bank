# Backend Bank - Microservices Architecture

Sistema de banco con arquitectura de microservicios Spring Boot.

## Estructura del Proyecto

```
backend-bank/
├── ms-eureka-server/    # Servidor de descubrimiento (puerto 8761)
├── ms-gateway/          # API Gateway (puerto 8080)
├── ms-customer/         # Microservicio de Clientes (puerto 8081)
├── ms-account/          # Microservicio de Cuentas (puerto 8082)
├── database/
│   └── init-schemas.sql # Script para crear esquemas PostgreSQL
├── docker-compose.yml   # Orquestación de contenedores
└── README.md
```

## Microservicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| ms-eureka-server | 8761 | Servidor de descubrimiento de servicios |
| ms-gateway | 8080 | API Gateway - punto de entrada único |
| ms-customer | 8081 | Gestión de clientes (CRUD) |
| ms-account | 8082 | Gestión de cuentas bancarias |

## Base de Datos

**PostgreSQL 15** con una única base de datos `bankdb` y esquemas separados:

| Microservicio | Esquema | Tablas |
|--------------|---------|--------|
| ms-customer | `customer` | customers, addresses, etc. |
| ms-account | `account` | accounts, transactions, etc. |

### Configuración de Conexión (Local)

```yaml
# ms-customer: jdbc:postgresql://localhost:5432/bankdb?currentSchema=customer
# ms-account:  jdbc:postgresql://localhost:5432/bankdb?currentSchema=account
```

### Crear Esquemas Manualmente

```bash
# Conectar a PostgreSQL
psql -U postgres -d bankdb

# O ejecutar el script
psql -U postgres -f database/init-schemas.sql
```

```sql
-- Crear esquemas manualmente
CREATE SCHEMA IF NOT EXISTS customer;
CREATE SCHEMA IF NOT EXISTS account;
```

## Requisitos

- Java 1.8+
- Maven 3.6+
- PostgreSQL 15+ (o usar Docker)

## Inicio Rápido

### Opción 1: Con Docker (Recomendado)

```bash
# Levantar toda la infraestructura
docker-compose up -d

# Verificar servicios
docker-compose ps

# Logs
docker-compose logs -f
```

### Opción 2: Local (Desarrollo)

#### 1. Iniciar PostgreSQL

```bash
# Con Docker solo para la base de datos
docker run -d \
  --name bank-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=bankdb \
  -p 5432:5432 \
  -v $(pwd)/database/init-schemas.sql:/docker-entrypoint-initdb.d/01-init-schemas.sql \
  postgres:15-alpine

# Crear esquemas
psql -U postgres -d bankdb -f database/init-schemas.sql
```

#### 2. Iniciar Eureka Server

```bash
cd ms-eureka-server
mvn spring-boot:run
```

#### 3. Iniciar los Microservicios (en terminales separados)

```bash
cd ms-customer && mvn spring-boot:run
cd ms-account && mvn spring-boot:run
cd ms-gateway && mvn spring-boot:run
```

### 3. Verificar el estado

- Eureka Dashboard: http://localhost:8761
- Gateway: http://localhost:8080
- Customer directo: http://localhost:8081
- Account directo: http://localhost:8082

## Endpoints Disponibles

### A través del Gateway (recomendado)

```
GET http://localhost:8080/api/customers     → ms-customer
GET http://localhost:8080/api/accounts    → ms-account
```

### Eureka

```
GET http://localhost:8761/eureka/apps     - Lista de servicios registrados
```

### Actuator (Health checks)

```
GET http://localhost:{port}/actuator/health
GET http://localhost:{port}/actuator/info
```

## Arquitectura de Paquetes

Cada microservicio sigue arquitectura hexagonal/clean:

```
ms-{service}/
└── src/main/java/com/bank/{service}/
    ├── application/        # Casos de uso
    │   ├── dto/           # Data Transfer Objects
    │   ├── exception/     # Excepciones de aplicación
    │   ├── mapper/        # Mappers DTO ↔ Entity
    │   └── service/       # Servicios de aplicación
    ├── domain/            # Core del negocio
    │   ├── entity/        # Entidades JPA
    │   ├── repository/    # Interfaces de repositorio
    │   └── service/       # Servicios de dominio
    └── infrastructure/    # Adaptadores técnicos
        ├── config/        # Configuraciones Spring
        ├── exception/     # Handlers de excepciones
        ├── persistence/   # Implementaciones JPA
        └── rest/          # Controllers REST
```

## Dependencias

Cada microservicio incluye:

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Cloud Netflix Eureka Client
- Spring Boot Actuator
- **PostgreSQL Driver**
- Lombok (opcional)
- Spring Boot Starter Test

## Docker Compose

### Servicios

| Servicio | Imagen | Puerto | Descripción |
|----------|--------|--------|-------------|
| postgres | postgres:15-alpine | 5432 | Base de datos compartida |
| eureka-server | (build) | 8761 | Service Discovery |
| gateway | (build) | 8080 | API Gateway |
| ms-account | (build) | 8082 | Account Service |
| ms-customer | (build) | 8081 | Customer Service |

### Comandos útiles

```bash
# Construir imágenes
docker-compose build

# Iniciar
docker-compose up -d

# Detener
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Ver logs de un servicio
docker-compose logs -f ms-customer

# Escalar un servicio
docker-compose up -d --scale ms-customer=2
```

## Configuración de Hibernate

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Desarrollo: create-drop
                              # Producción: validate o none
```

| Valor | Descripción |
|-------|-------------|
| `create` | Crea tablas al iniciar |
| `create-drop` | Crea y elimina al detener (dev) |
| `update` | Actualiza sin perder datos (dev) |
| `validate` | Solo valida, no modifica (prod) |
| `none` | No hace nada (prod con Flyway) |

## Notas

- Cada microservicio usa su propio **schema** en PostgreSQL
- El esquema `public` NO se usa para tablas de negocio
- La conexión a BD se configura vía `currentSchema` en la URL
- Para producción, considerar usar **Flyway** o **Liquibase** para migraciones
