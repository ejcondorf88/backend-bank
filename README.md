# Backend Bank - Microservices Architecture

Sistema de banco con arquitectura de microservicios Spring Boot.

## Estructura del Proyecto

```
backend-bank/
├── ms-eureka-server/    # Servidor de descubrimiento (puerto 8761)
├── ms-gateway/          # API Gateway (puerto 8080)
├── ms-customer/         # Microservicio de Clientes (puerto 8081)
├── ms-account/          # Microservicio de Cuentas (puerto 8082)
└── README.md
```

## Microservicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| ms-eureka-server | 8761 | Servidor de descubrimiento de servicios |
| ms-gateway | 8080 | API Gateway - punto de entrada único |
| ms-customer | 8081 | Gestión de clientes (CRUD) |
| ms-account | 8082 | Gestión de cuentas bancarias |

## Requisitos

- Java 1.8+
- Maven 3.6+

## Inicio Rápido

### 1. Iniciar Eureka Server
```bash
cd ms-eureka-server
mvn spring-boot:run
```

### 2. Iniciar los Microservicios (en terminales separados)
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
- `GET http://localhost:8080/api/customers` → ms-customer
- `GET http://localhost:8080/api/accounts` → ms-account

### Eureka
- `GET http://localhost:8761/eureka/apps` - Lista de servicios registrados

### Actuator (Health checks)
- `GET http://localhost:{port}/actuator/health`
- `GET http://localhost:{port}/actuator/info`

## Dependencias

Cada microservicio incluye:
- Spring Boot Starter
- Spring Cloud Netflix Eureka Client/Server
- Spring Boot Actuator (monitoreo)
- Spring Data JPA (ms-customer, ms-account)
- H2 Database (desarrollo)
- Lombok (opcional)
