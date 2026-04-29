# 🔍 ms-eureka-server

## Descripción

**ms-eureka-server** es el componente de **Service Discovery** (descubrimiento de servicios) del sistema Backend Bank. Implementado con **Netflix Eureka**, permite que los microservicios se registren dinámicamente y se descubran entre sí sin configuración estática de URLs.

## ¿Qué es Service Discovery?

En arquitecturas de microservicios, los servicios necesitan comunicarse entre sí. El problema:
- ❌ **IPs y puertos hardcodeados**: Si un servicio cambia de IP, todo se rompe
- ❌ **Load balancing manual**: Difícil distribuir carga entre instancias
- ❌ **Escalabilidad**: Agregar instancias requiere reconfiguración

La solución:
- ✅ **Registro dinámico**: Cada servicio se registra al iniciar
- ✅ **Descubrimiento**: Los servicios consultan "¿dónde está X?"
- ✅ **Balanceo**: Distribución automática de peticiones

## Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    Service Discovery                        │
│                      (Eureka Server)                      │
├─────────────────────────────────────────────────────────────┤
│  Registro                                                  │
│  ├── ms-customer → IP:8081 (UP)                           │
│  ├── ms-account  → IP:8082 (UP)                            │
│  └── ms-gateway  → IP:8080 (UP)                            │
│                                                             │
│  Health Checks                                              │
│  ├── Heartbeat cada 30s                                    │
│  └── Marca DOWN si no responde                             │
└─────────────────────────────────────────────────────────────┘
```

## Responsabilidades

1. **Registro de Servicios**: Cada microservicio se registra al iniciar
2. **Health Checking**: Verifica que los servicios estén vivos (heartbeats)
3. **Service Registry**: Base de datos de servicios disponibles
4. **Load Balancing**: Proporciona información para balancear carga

## Endpoints

| Endpoint | Descripción |
|----------|-------------|
| `http://localhost:8761/` | Dashboard web de Eureka |
| `http://localhost:8761/eureka/apps` | API REST con lista de servicios |

## Dashboard

Accede a `http://localhost:8761` para ver:
- Lista de servicios registrados
- Estado (UP, DOWN, STARTING)
- IP y puerto de cada instancia
- Último heartbeat recibido

## Configuración

```yaml
# application.yml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false    # No se registra a sí mismo
    fetch-registry: false         # No consulta otros servicios
  server:
    enable-self-preservation: true    # Modo de protección
    eviction-interval-timer-in-ms: 60000  # Eliminar servicios caídos
```

## Uso desde otros Servicios

### Registro (ms-customer, ms-account, ms-gateway)

```yaml
# application.yml de cada microservicio
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true
```

### Descubrimiento (ms-gateway)

```yaml
# Usa Eureka para encontrar servicios
spring:
  cloud:
    gateway:
      routes:
        - id: ms-customer
          uri: lb://ms-customer    # "lb" = load balancer via Eureka
          predicates:
            - Path=/api/clients/**
```

## Flujo de Trabajo

```
1. ms-account inicia
   ↓
2. Se registra en Eureka con nombre "ms-account" + IP:8082
   ↓
3. Cada 30s envía heartbeat
   ↓
4. Gateway consulta: "¿dónde está ms-account?"
   ↓
5. Eureka responde: "En IP:8082, estado UP"
   ↓
6. Gateway enruta petición al servicio
```

## Tecnologías

| Tecnología | Uso |
|------------|-----|
| **Spring Boot** | Framework base |
| **Spring Cloud Netflix Eureka** | Service Discovery |
| **Spring Boot Actuator** | Health checks |

## Docker

```dockerfile
FROM eclipse-temurin:17-jre-alpine
EXPOSE 8761
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

## Puertos

| Puerto | Uso |
|--------|-----|
| `8761` | Dashboard web y API REST |

## Logs Importantes

```
# Registro exitoso de un servicio
Registered instance MS-ACCOUNT/192.168.1.100:ms-account:8082

# Heartbeat recibido
Current node registry size: 3

# Servicio no responde (marcado como DOWN)
Evicting MS-ACCOUNT/192.168.1.100:ms-account:8082
```

## Troubleshooting

### Servicio no aparece en Eureka

1. Verificar que Eureka URL esté configurado
2. Verificar conectividad de red
3. Revisar logs del servicio: `Fetching config from server at : http://eureka-server:8761`

### Servicio aparece como DOWN

1. Verificar que el servicio responda en `/actuator/health`
2. Revisar logs de conectividad
3. Verificar firewall entre servicios

## Escalabilidad

Eureka soporta múltiples instancias de cada servicio:

```bash
# Escala ms-account a 3 instancias
docker-compose up -d --scale ms-account=3

# Eureka registra:
# - ms-account:8082 (instancia 1)
# - ms-account:8082 (instancia 2)
# - ms-account:8082 (instancia 3)

# Gateway balancea automáticamente entre ellas
```

## Referencias

- [Spring Cloud Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
- [Netflix Eureka Wiki](https://github.com/Netflix/eureka/wiki)
