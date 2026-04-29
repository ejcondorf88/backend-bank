# 🚪 ms-gateway

## Descripción

**ms-gateway** es el **API Gateway** del sistema Backend Bank. Actúa como punto de entrada único para todas las peticiones de clientes externos, enrutando el tráfico a los microservicios apropiados.

## ¿Qué es un API Gateway?

En arquitecturas de microservicios, tenemos múltiples servicios. El problema:
- ❌ **Múltiples endpoints**: Clientes deben conocer IP:puerto de cada servicio
- ❌ **Seguridad dispersa**: Cada servicio implementa autenticación
- ❌ **Cross-cutting concerns**: Logging, rate limiting duplicados

La solución:
- ✅ **Single Entry Point**: Un solo endpoint para todos los clientes
- ✅ **Enrutamiento**: Gateway decide a qué servicio enviar cada petición
- ✅ **Load Balancing**: Distribuye carga entre instancias
- ✅ **Centralización**: Seguridad, logging, rate limiting en un lugar

## Arquitectura

```
                    ┌─────────────────────────────────┐
                    │           Clientes              │
                    │  (Web App, Mobile, Postman)    │
                    └──────────────┬──────────────────┘
                                   │
                                   │ HTTP /api/**
                                   │
                    ┌───────────────▼──────────────────┐
                    │        API Gateway             │
                    │         :8080                  │
                    │  ┌─────────────────────────┐   │
                    │  │  Routing                │   │
                    │  │  - /api/clients/**     │   │
                    │  │  - /api/accounts/**    │   │
                    │  │  - /api/movements/**   │   │
                    │  └─────────────────────────┘   │
                    └───────────────┬───────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
            ┌───────▼──────┐ ┌────▼─────┐ ┌─────▼──────┐
            │ ms-customer  │ │ ms-account│ │ Eureka     │
            │    :8081     │ │   :8082   │ │  :8761     │
            └──────────────┘ └───────────┘ └────────────┘
```

## Responsabilidades

1. **Enrutamiento de Peticiones**: Decide a qué servicio enviar cada URL
2. **Load Balancing**: Distribuye entre instancias de un servicio
3. **Service Discovery**: Consulta Eureka para encontrar servicios
4. **Traducción de Protocolos**: Puede transformar HTTP a otros protocolos
5. **Rate Limiting**: Limitar requests por cliente (no implementado)
6. **SSL Termination**: Manejar HTTPS (no implementado)

## Endpoints y Rutas

| Ruta Entrante | Servicio Destino | Endpoint Final |
|---------------|------------------|----------------|
| `/api/clients/**` | ms-customer | ms-customer:8081/api/clients/** |
| `/api/accounts/**` | ms-account | ms-account:8082/api/accounts/** |
| `/api/movements/**` | ms-account | ms-account:8082/api/movements/** |

## Configuración de Rutas

```yaml
# application.yml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true          # Descubre rutas desde Eureka
          lower-case-service-id: true
      routes:
        # Ruta a ms-customer
        - id: ms-customer
          uri: lb://ms-customer   # "lb" = load balancer via Eureka
          predicates:
            - Path=/api/clients/**
          
        # Ruta a ms-account
        - id: ms-account
          uri: lb://ms-account
          predicates:
            - Path=/api/accounts/**
            
        # Ruta a ms-account (movimientos)
        - id: ms-account-movements
          uri: lb://ms-account
          predicates:
            - Path=/api/movements/**
```

## Cómo Funciona el Enrutamiento

```
1. Cliente envía: GET http://localhost:8080/api/accounts/12345

2. Gateway recibe la petición
   └─ Revisa las rutas configuradas

3. Match: Path=/api/accounts/** → uri=lb://ms-account
   └─ "lb://" significa "usar Eureka para balanceo de carga"

4. Gateway consulta Eureka
   └─ "¿dónde está ms-account?"
   └─ Eureka responde: "En IP:8082, estado UP"

5. Gateway reenvía la petición
   └─ GET http://IP:8082/api/accounts/12345

6. ms-account responde

7. Gateway retorna la respuesta al cliente
```

## Ventajas para el Cliente

### Sin Gateway (Directo a servicios):
```javascript
// Cliente necesita conocer cada servicio
const customerUrl = 'http://localhost:8081';
const accountUrl = 'http://localhost:8082';

fetch(`${customerUrl}/api/clients/1`);   // Si cambia puerto, se rompe
fetch(`${accountUrl}/api/accounts/123`); // Si hay múltiples instancias, manual
```

### Con Gateway:
```javascript
// Cliente solo conoce el Gateway
const apiUrl = 'http://localhost:8080';

fetch(`${apiUrl}/api/clients/1`);    // Gateway decide
fetch(`${apiUrl}/api/accounts/123`); // Gateway decide
```

## Load Balancing Automático

Si hay múltiples instancias de un servicio:

```bash
# Escala ms-account a 3 instancias
docker-compose up -d --scale ms-account=3
```

El Gateway distribuye automáticamente:
```
Request 1 → ms-account (instancia 1) :8082
Request 2 → ms-account (instancia 2) :8082
Request 3 → ms-account (instancia 3) :8082
Request 4 → ms-account (instancia 1) :8082
... (round-robin)
```

## Health Checks

Gateway verifica disponibilidad antes de enrutar:
```
1. Petición a /api/accounts/**
2. Gateway consulta Eureka: "¿ms-account está UP?"
3. Si SÍ → Enruta
4. Si NO → Intenta otra instancia o devuelve error 503
```

## Logs de Enrutamiento

```
# Petición entrante
Route matched: ms-account -> lb://ms-account

# Reenvío
Forwarding to URI: lb://ms-account/api/accounts/123

# Respuesta
Completed 200 OK
```

## Configuración Importante

### Conexión a Eureka
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
```

### Timeout (para evitar esperas indefinidas)
```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 5000    # 5s para conectar
        response-timeout: 10s    # 10s para respuesta
```

## Docker

```dockerfile
FROM eclipse-temurin:17-jre-alpine
ENV SERVER_PORT=8080
ENV EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka/
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

## Puertos

| Puerto | Uso |
|--------|-----|
| `8080` | Entry point único para clientes |

## Testing

```bash
# Desde fuera del sistema, solo conocemos Gateway

# Probar ms-customer a través del Gateway
curl http://localhost:8080/api/clients

# Probar ms-account a través del Gateway
curl http://localhost:8080/api/accounts

# Probar movimientos a través del Gateway
curl http://localhost:8080/api/movements
```

## Troubleshooting

### Gateway devuelve 503 Service Unavailable

**Causa**: El servicio destino está DOWN o no registrado en Eureka

**Solución**:
1. Verificar que el servicio esté corriendo: `docker-compose ps`
2. Verificar registro en Eureka: `http://localhost:8761`
3. Revisar logs del servicio destino

### Timeout

**Causa**: Servicio destino lento

**Solución**:
1. Aumentar timeout en configuración
2. Verificar rendimiento del servicio
3. Revisar logs por errores

### Ruta no encontrada (404)

**Causa**: URL no coincide con ninguna ruta configurada

**Solución**:
1. Verificar ruta exacta en configuración
2. Verificar prefijo `/api/`
3. Revisar `Path` predicate

## Tecnologías

| Tecnología | Uso |
|------------|-----|
| **Spring Boot** | Framework base |
| **Spring Cloud Gateway** | API Gateway |
| **Spring Cloud Netflix Eureka** | Descubrimiento |
| **Spring Boot Actuator** | Health checks |

## Patrones Relacionados

- **API Gateway Pattern**: Centralizar entry points
- **BFF (Backend for Frontend)**: Gateway específico por tipo de cliente
- **Circuit Breaker**: Evitar cascada de fallos (no implementado)

## Referencias

- [Spring Cloud Gateway](https://cloud.spring.io/spring-cloud-gateway/reference/html/)
- [API Gateway Pattern](https://microservices.io/patterns/apigateway.html)
