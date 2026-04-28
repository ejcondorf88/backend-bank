# Historias de Usuario - Perfil SemiSenior
## Sistema Bancario de Microservicios

**Proyecto:** Backend Bank  
**Perfil:** SemiSenior Developer  
**Fecha:** Abril 2026  
**Alcance:** Funcionalidades F1, F2, F3, F4, F5 (F6 deseable)  
**Arquitectura:** 2 Microservicios + Comunicación Asíncrona

---

## Arquitectura de Microservicios

### Microservicio 1: ms-customer-persona
**Responsabilidad:** Gestión de Clientes y Personas
- Entidades: Persona, Cliente
- Base de datos: PostgreSQL
- Comunicación: REST + Eventos asíncronos (RabbitMQ)

### Microservicio 2: ms-account-movement
**Responsabilidad:** Gestión de Cuentas y Movimientos
- Entidades: Cuenta, Movimiento
- Base de datos: PostgreSQL
- Comunicación: REST + Eventos asíncronos (RabbitMQ)
- Consume eventos de cliente desde ms-customer-persona

### Componentes Compartidos
- **Message Broker:** RabbitMQ para comunicación asíncrona entre microservicios

**Nota:** Según el enunciado, solo se requiere:
- 2 microservicios con comunicación asíncrona
- No se especifica Service Discovery (Eureka) ni API Gateway

---

## Principio INVEST

Todas las historias siguen el principio **INVEST**:
- **I**ndependiente: No dependen de otras para implementarse
- **N**egociable: Los detalles son discutibles
- **V**aliosa: Aportan valor al negocio
- **E**stimable: Esfuerzo claro y conocido
- **S**mall: Pequeñas para completar en sprint
- **T**estable: Criterios verificables

---

## EPIC 1: Arquitectura Base

### HU-ARQ-001: Configurar comunicación entre microservicios
**Como** desarrollador SemiSenior  
**Quiero** establecer comunicación asíncrona  
**Para** que los microservicios se comuniquen mediante eventos

#### Criterios de Aceptación

```gherkin
Feature: Comunicación Asíncrona entre Microservicios

  Scenario: Configurar RabbitMQ
    Dado que se requiere comunicación asíncrona
    Cuando configuro RabbitMQ en docker-compose
    Entonces los microservicios pueden conectarse
    Y existe una cola para eventos de cliente

  Scenario: Publicar evento desde ms-customer-persona
    Dado que RabbitMQ está disponible
    Cuando se crea un cliente en ms-customer-persona
    Entonces se publica evento "CLIENTE_CREADO"
    Y el mensaje incluye clienteId y nombre

  Scenario: Consumir evento en ms-account-movement
    Dado que existe evento "CLIENTE_CREADO" en la cola
    Cuando ms-account-movement consume el mensaje
    Entonces registra la información del cliente localmente
    Y puede crear cuentas para ese cliente
```

**Story Points:** 8  
**Funcionalidad:** Arquitectura

---

### HU-ARQ-002: Docker Compose para microservicios
**Como** desarrollador SemiSenior  
**Quiero** un docker-compose funcional  
**Para** levantar los microservicios y dependencias

#### Criterios de Aceptación

```gherkin
Feature: Docker Orchestration

  Scenario: Levantar infraestructura completa
    Dado que existe docker-compose.yml
    Cuando ejecuto docker-compose up
    Entonces se levantan:
      - RabbitMQ (puerto 5672, 15672)
      - ms-customer-persona (puerto 8081)
      - ms-account-movement (puerto 8082)
    Y ambos microservicios están healthy

  Scenario: Comunicación entre servicios vía RabbitMQ
    Dado que docker-compose está corriendo
    Cuando creo un cliente en ms-customer-persona
    Entonces ms-account-movement recibe el evento
    Y puede crear una cuenta para ese cliente
```

**Story Points:** 5  
**Funcionalidad:** F7 (parcial)

---

## EPIC 2: Microservicio - Cliente y Persona

### HU-CP-001: Crear entidad Persona
**Como** desarrollador SemiSenior  
**Quiero** implementar la entidad Persona  
**Para** ser la clase base de Cliente

#### Criterios de Aceptación

```gherkin
Feature: Entidad Persona

  Scenario: Crear persona con datos válidos
    Dado que el sistema está operativo
    Cuando creo una persona con:
      | campo        | valor                  |
      | nombre       | Jose Lema              |
      | genero       | Masculino              |
      | edad         | 35                     |
      | identificacion | 1720456325           |
      | direccion    | Otavalo sn y principal |
      | telefono     | 098254785              |
    Entonces la persona se guarda en la base de datos
    Y tiene un ID único generado

  Scenario: Validar identificación única
    Dado que existe una persona con identificación "1720456325"
    Cuando intento crear otra con la misma identificación
    Entonces se lanza excepción de duplicado
    Y el código de error es 409
```

**Story Points:** 3  
**Funcionalidad:** F1

---

### HU-CP-002: Crear entidad Cliente (hereda de Persona)
**Como** desarrollador SemiSenior  
**Quiero** implementar la entidad Cliente  
**Para** gestionar clientes del banco

#### Criterios de Aceptación

```gherkin
Feature: Entidad Cliente

  Scenario: Crear cliente exitosamente
    Dado que no existe cliente con identificación "1720456325"
    Cuando creo un cliente con:
      | nombre       | Jose Lema              |
      | genero       | Masculino              |
      | edad         | 35                     |
      | identificacion | 1720456325           |
      | direccion    | Otavalo sn y principal |
      | telefono     | 098254785              |
      | contrasena   | 1234                   |
      | estado       | true                   |
    Entonces el cliente se guarda exitosamente
    Y hereda los atributos de Persona
    Y tiene clienteId único generado

  Scenario: Cliente debe tener identificación única
    Dado que existe cliente con identificación "1720456325"
    Cuando intento crear otro cliente con misma identificación
    Entonces se lanza DataIntegrityViolationException
    Y el mensaje indica "Identificación ya existe"
```

**Story Points:** 3  
**Funcionalidad:** F1

---

### HU-CP-003: CRUD completo de Clientes
**Como** administrador  
**Quiero** gestionar clientes (crear, leer, actualizar, eliminar)  
**Para** administrar la información de clientes

#### Criterios de Aceptación

```gherkin
Feature: CRUD Clientes

  Scenario: Crear cliente - POST /clientes
    Dado que el sistema está operativo
    Cuando envío POST a /clientes con datos válidos
    Entonces el sistema responde con código 201
    Y retorna el cliente creado con su ID
    Y emite evento "CLIENTE_CREADO" al broker

  Scenario: Obtener cliente - GET /clientes/{id}
    Dado que existe cliente con clienteId "1"
    Cuando envío GET a /clientes/1
    Entonces el sistema responde con código 200
    Y retorna todos los datos del cliente (sin contraseña)

  Scenario: Actualizar cliente - PUT /clientes/{id}
    Dado que existe cliente con clienteId "1"
    Cuando envío PUT a /clientes/1 con nueva dirección
    Entonces el sistema responde con código 200
    Y los datos se actualizan
    Y se emite evento "CLIENTE_ACTUALIZADO"

  Scenario: Eliminar cliente - DELETE /clientes/{id}
    Dado que existe cliente con clienteId "1"
    Y el cliente no tiene cuentas asociadas
    Cuando envío DELETE a /clientes/1
    Entonces el sistema responde con código 204
    Y se emite evento "CLIENTE_ELIMINADO"
    
  Scenario: No permitir eliminar cliente con cuentas
    Dado que existe cliente con clienteId "1"
    Y existe cuenta asociada al cliente
    Cuando envío DELETE a /clientes/1
    Entonces el sistema responde con código 409
    Y el mensaje indica "Cliente tiene cuentas asociadas"
```

**Story Points:** 8  
**Funcionalidad:** F1

---

### HU-CP-004: Publicar eventos de dominio
**Como** desarrollador SemiSenior  
**Quiero** publicar eventos cuando cambian los datos de cliente  
**Para** que ms-account-movement se sincronice

#### Criterios de Aceptación

```gherkin
Feature: Eventos de Dominio - Cliente

  Scenario: Evento CLIENTE_CREADO
    Dado que RabbitMQ está disponible
    Cuando se crea un nuevo cliente
    Entonces se publica evento "CLIENTE_CREADO"
    Y el payload incluye:
      - clienteId
      - nombre
      - identificacion
      - fechaCreacion

  Scenario: Evento CLIENTE_ACTUALIZADO
    Dado que RabbitMQ está disponible
    Cuando se actualiza un cliente
    Entonces se publica evento "CLIENTE_ACTUALIZADO"
    Y el payload incluye los campos modificados

  Scenario: Evento CLIENTE_ELIMINADO
    Dado que RabbitMQ está disponible
    Cuando se elimina un cliente
    Entonces se publica evento "CLIENTE_ELIMINADO"
    Y el payload incluye el clienteId
```

**Story Points:** 5  
**Funcionalidad:** Arquitectura

---

### HU-CP-005: Prueba unitaria de entidad Cliente (F5)
**Como** desarrollador SemiSenior  
**Quiero** pruebas unitarias para la entidad Cliente  
**Para** garantizar la calidad del código

#### Criterios de Aceptación

```gherkin
Feature: Pruebas Unitarias - Cliente

  Scenario: Crear cliente - prueba unitaria
    Dado que tengo un ClienteRepository mock
    Cuando ejecuto el test testCrearCliente()
    Entonces el test pasa exitosamente
    Y verifica:
      - Cliente se guarda correctamente
      - Identificación es única
      - Contraseña se encripta

  Scenario: Validar integridad de datos - prueba unitaria
    Dado que tengo un Cliente con datos inválidos
    Cuando ejecuto el test testValidarCliente()
    Entonces el test pasa exitosamente
    Y verifica:
      - Campos obligatorios no pueden ser nulos
      - Edad debe ser positiva
```

**Story Points:** 5  
**Funcionalidad:** F5

---

## EPIC 3: Microservicio - Cuenta y Movimiento

### HU-CM-001: Crear entidad Cuenta
**Como** desarrollador SemiSenior  
**Quiero** implementar la entidad Cuenta  
**Para** gestionar cuentas bancarias

#### Criterios de Aceptación

```gherkin
Feature: Entidad Cuenta

  Scenario: Crear cuenta exitosamente
    Dado que recibo evento CLIENTE_CREADO con clienteId "1"
    Cuando creo una cuenta con:
      | numeroCuenta | 478758           |
      | tipoCuenta   | Ahorro           |
      | saldoInicial | 2000.00          |
      | estado       | true             |
      | clienteId    | 1                |
    Entonces la cuenta se guarda exitosamente
    Y el saldo actual es 2000.00

  Scenario: Validar número de cuenta único
    Dado que existe cuenta con número "478758"
    Cuando intento crear otra cuenta con mismo número
    Entonces se lanza excepción de duplicado
    Y el código es 409

  Scenario: Validar tipos de cuenta permitidos
    Cuando intento crear cuenta con tipoCuenta "Invalido"
    Entonces se lanza excepción de validación
    Y el mensaje indica "Tipo de cuenta no válido"
    Y los tipos permitidos son: Ahorro, Corriente
```

**Story Points:** 3  
**Funcionalidad:** F1

---

### HU-CM-002: Crear entidad Movimiento
**Como** desarrollador SemiSenior  
**Quiero** implementar la entidad Movimiento  
**Para** registrar transacciones

#### Criterios de Aceptación

```gherkin
Feature: Entidad Movimiento

  Scenario: Registrar movimiento exitoso
    Dado que existe cuenta "478758" con saldo 2000.00
    Cuando registro un movimiento:
      | cuentaId       | 478758           |
      | fecha          | 2022-02-10       |
      | tipoMovimiento | Deposito         |
      | valor          | 600.00           |
      | saldo          | 2600.00          |
    Entonces el movimiento se guarda exitosamente
    Y el saldo de la cuenta se actualiza a 2600.00

  Scenario: Movimiento debe tener fecha automática
    Cuando registro un movimiento sin especificar fecha
    Entonces el sistema asigna la fecha y hora actual
```

**Story Points:** 3  
**Funcionalidad:** F1

---

### HU-CM-003: CRUD completo de Cuentas
**Como** administrador  
**Quiero** gestionar cuentas bancarias  
**Para** administrar las cuentas de los clientes

#### Criterios de Aceptación

```gherkin
Feature: CRUD Cuentas

  Scenario: Crear cuenta - POST /cuentas
    Dado que existe cliente con clienteId "1"
    Cuando envío POST a /cuentas con datos válidos
    Entonces el sistema responde con código 201
    Y la cuenta se crea con saldo inicial
    Y se puede verificar vía GET /cuentas/{numero}

  Scenario: Obtener cuenta - GET /cuentas/{numero}
    Dado que existe cuenta con número "478758"
    Cuando envío GET a /cuentas/478758
    Entonces el sistema responde con código 200
    Y retorna número, tipo, saldo, estado y clienteId

  Scenario: Listar cuentas de cliente - GET /cuentas?clienteId={id}
    Dado que el cliente "1" tiene 3 cuentas
    Cuando envío GET a /cuentas?clienteId=1
    Entonces retorna lista con 3 cuentas
    Y el código es 200

  Scenario: Actualizar cuenta - PUT /cuentas/{numero}
    Dado que existe cuenta "478758"
    Cuando envío PUT a /cuentas/478758 con estado false
    Entonces el sistema responde con código 200
    Y el estado cambia a inactivo

  Scenario: Eliminar cuenta - DELETE /cuentas/{numero}
    Dado que existe cuenta "478758"
    Y la cuenta no tiene movimientos
    Y el saldo es cero
    Cuando envío DELETE a /cuentas/478758
    Entonces el sistema responde con código 204
```

**Story Points:** 8  
**Funcionalidad:** F1

---

### HU-CM-004: CRUD de Movimientos con actualización de saldo (F2)
**Como** cliente del banco  
**Quiero** registrar movimientos  
**Para** que se actualice automáticamente mi saldo

#### Criterios de Aceptación

```gherkin
Feature: CRUD Movimientos con Actualización de Saldo

  Scenario: Depósito actualiza saldo (F2)
    Dado que cuenta "225487" tiene saldo 100.00
    Cuando envío POST a /movimientos con:
      | numeroCuenta   | 225487           |
      | tipoMovimiento | Deposito         |
      | valor          | 600.00           |
    Entonces el sistema responde con código 201
    Y el saldo de la cuenta es 700.00
    Y el movimiento tiene valor positivo 600.00

  Scenario: Retiro actualiza saldo (F2)
    Dado que cuenta "478758" tiene saldo 2000.00
    Cuando envío POST a /movimientos con:
      | numeroCuenta   | 478758           |
      | tipoMovimiento | Retiro           |
      | valor          | 575.00           |
    Entonces el sistema responde con código 201
    Y el saldo de la cuenta es 1425.00
    Y el movimiento tiene valor negativo -575.00

  Scenario: Retiro deja saldo en cero (F2)
    Dado que cuenta "496825" tiene saldo 540.00
    Cuando envío POST a /movimientos con:
      | numeroCuenta   | 496825           |
      | tipoMovimiento | Retiro           |
      | valor          | 540.00           |
    Entonces el sistema responde con código 201
    Y el saldo de la cuenta es 0.00

  Scenario: Listar movimientos de cuenta - GET /movimientos
    Dado que cuenta "225487" tiene 3 movimientos
    Cuando envío GET a /movimientos?numeroCuenta=225487
    Entonces el sistema retorna lista con 3 movimientos
    Y cada movimiento incluye: id, fecha, tipo, valor, saldo
```

**Story Points:** 8  
**Funcionalidad:** F2

---

### HU-CM-005: Validación de saldo insuficiente (F3)
**Como** sistema bancario  
**Quiero** validar el saldo antes de permitir retiros  
**Para** evitar sobregiros

#### Criterios de Aceptación

```gherkin
Feature: Validación de Saldo - F3

  Scenario: Retiro rechazado por saldo insuficiente
    Dado que cuenta "495878" tiene saldo 0.00
    Cuando envío POST a /movimientos con:
      | numeroCuenta   | 495878           |
      | tipoMovimiento | Retiro         |
      | valor          | 100.00         |
    Entonces el sistema responde con código 400
    Y el mensaje es "Saldo no disponible"
    Y el saldo permanece en 0.00
    Y no se registra el movimiento

  Scenario: Retiro rechazado cuando monto excede saldo
    Dado que cuenta "478758" tiene saldo 2000.00
    Cuando intento retirar 2500.00
    Entonces el sistema responde con código 400
    Y el mensaje es "Saldo no disponible"
    Y el saldo permanece en 2000.00

  Scenario: Retiro rechazado en cuenta inactiva
    Dado que cuenta "478758" está desactivada
    Cuando intento realizar un retiro
    Entonces el sistema responde con código 400
    Y el mensaje indica "La cuenta no está activa"

  Scenario: Captura y manejo de excepción
    Dado que intento retirar sin saldo suficiente
    Cuando se lanza SaldoNoDisponibleException
    Entonces se captura en el GlobalExceptionHandler
    Y se retorna respuesta JSON con código 400
    Y el mensaje es "Saldo no disponible"
```

**Story Points:** 5  
**Funcionalidad:** F3

---

### HU-CM-006: Reporte de Estado de Cuenta (F4)
**Como** cliente del banco  
**Quiero** ver un reporte de mis cuentas y movimientos  
**Para** revisar mi estado financiero

#### Criterios de Aceptación

```gherkin
Feature: Reporte Estado de Cuenta - F4

  Scenario: Generar reporte por rango de fechas
    Dado que el cliente "1" tiene cuentas con movimientos
    Cuando envío GET a /reportes?fechaInicio=2022-02-01&fechaFin=2022-02-28&clienteId=1
    Entonces el sistema responde con código 200
    Y el reporte incluye:
      - Información del cliente (obtenida vía eventos)
      - Lista de cuentas asociadas con sus saldos actuales
      - Detalle de movimientos en el rango de fechas

  Scenario: Reporte con datos específicos del ejemplo
    Dado que existen movimientos:
      | Fecha      | Cliente            | Cuenta | Tipo      | SaldoInicial | Movimiento | SaldoDisponible |
      | 2022-02-10 | Marianela Montalvo | 225487 | Corriente | 100          | 600        | 700             |
      | 2022-02-08 | Marianela Montalvo | 496825 | Ahorros   | 540          | -540       | 0               |
    Cuando solicito reporte para cliente "Marianela Montalvo" con fechas del ejemplo
    Entonces el JSON de respuesta tiene el formato:
      ```json
      {
        "fechaInicio": "2022-02-01",
        "fechaFin": "2022-02-28",
        "cliente": "Marianela Montalvo",
        "cuentas": [
          {
            "numeroCuenta": "225487",
            "tipo": "Corriente",
            "saldoActual": 700,
            "movimientos": [...]
          }
        ]
      }
      ```

  Scenario: Reporte sin parámetros de fecha
    Dado que no envío parámetros de fecha
    Cuando solicito GET a /reportes?clienteId=1
    Entonces el sistema usa rango por defecto (último mes)
    Y retorna reporte con código 200
```

**Story Points:** 8  
**Funcionalidad:** F4

---

### HU-CM-007: Consumir eventos de cliente
**Como** ms-account-movement  
**Quiero** escuchar eventos de nuevos clientes  
**Para** mantener sincronizada la información

#### Criterios de Aceptación

```gherkin
Feature: Consumo de Eventos - Cliente

  Scenario: Recibir evento CLIENTE_CREADO
    Dado que RabbitMQ está configurado
    Cuando ms-customer-persona publica "CLIENTE_CREADO"
    Entonces ms-account-movement consume el evento
    Y almacena información mínima del cliente (clienteId, nombre, identificacion)
    Y puede crear cuentas para ese cliente

  Scenario: Recibir evento CLIENTE_ELIMINADO
    Dado que existe cliente en ms-account-movement
    Cuando se recibe evento "CLIENTE_ELIMINADO"
    Entonces se marca cliente como inactivo
    Y no se pueden crear nuevas cuentas para ese cliente
```

**Story Points:** 5  
**Funcionalidad:** Arquitectura

---

## EPIC 4: Docker y Despliegue

### HU-DEP-001: Docker para microservicios (F7)
**Como** desarrollador SemiSenior  
**Quiero** Dockerfiles para los microservicios  
**Para** desplegar en contenedores

#### Criterios de Aceptación

```gherkin
Feature: Docker para Microservicios

  Scenario: Dockerfile para ms-customer-persona
    Dado que existe el código fuente
    Cuando creo el Dockerfile con multi-stage
    Entonces:
      - Usa imagen base ligera (Alpine)
      - Compila con Maven
      - Crea imagen final < 300MB
      - Expone puerto 8081

  Scenario: Dockerfile para ms-account-movement
    Dado que existe el código fuente
    Cuando creo el Dockerfile con multi-stage
    Entonces:
      - Usa imagen base ligera (Alpine)
      - Compila con Maven
      - Crea imagen final < 300MB
      - Expone puerto 8082

  Scenario: Docker Compose funcional
    Dado que existen los Dockerfiles
    Cuando ejecuto docker-compose up
    Entonces:
      - Se levanta RabbitMQ
      - Se levanta ms-customer-persona
      - Se levanta ms-account-movement
      - Los servicios se comunican correctamente
```

**Story Points:** 5  
**Funcionalidad:** F7

---

## EPIC 5: Testing (Opcional/F6 deseable)

### HU-TEST-001: Pruebas de integración entre microservicios (F6)
**Como** QA engineer  
**Quiero** pruebas de integración  
**Para** verificar la comunicación asíncrona

#### Criterios de Aceptación

```gherkin
Feature: Pruebas de Integración - F6 (Deseable)

  Scenario: Flujo completo cliente-cuenta
    Dado que RabbitMQ está corriendo
    Cuando:
      1. Creo cliente en ms-customer-persona
      2. Espero propagación del evento
      3. Creo cuenta en ms-account-movement
      4. Realizo movimientos
    Entonces todo el flujo funciona correctamente
    Y los datos son consistentes

  Scenario: Verificar eventos en RabbitMQ
    Dado que los servicios están corriendo
    Cuando creo un cliente
    Entonces puedo ver el evento en la cola de RabbitMQ
    Y ms-account-movement lo consume correctamente

  Scenario: Health checks
    Dado que implemento Spring Boot Actuator
    Cuando accedo a /actuator/health
    Entonces veo estado de conexiones (BD, RabbitMQ)
```

**Story Points:** 5  
**Funcionalidad:** F6 (deseable)

---

## Resumen de Historias - SemiSenior

### Por Funcionalidad

| Funcionalidad | Historias | Story Points | Estado |
|--------------|-----------|--------------|--------|
| **F1 - CRUDs** | HU-CP-001 a HU-CP-003, HU-CM-001 a HU-CM-003 | 30 | Obligatorio |
| **F2 - Movimientos** | HU-CM-004 | 8 | Obligatorio |
| **F3 - Validación Saldo** | HU-CM-005 | 5 | Obligatorio |
| **F4 - Reportes** | HU-CM-006 | 8 | Obligatorio |
| **F5 - Pruebas Unitarias** | HU-CP-005 | 5 | Obligatorio |
| **F6 - Pruebas Integración** | HU-TEST-001 | 5 | Deseable |
| **F7 - Docker** | HU-DEP-001 | 5 | Obligatorio |
| **Arquitectura** | HU-ARQ-001, HU-ARQ-002, HU-CP-004, HU-CM-007 | 23 | Obligatorio |

**Total Obligatorio:** 84 Story Points  
**Total con Opcional:** 89 Story Points

---

## Entregables del Perfil SemiSenior

### Obligatorios:
1. ✅ **2 Microservicios implementados:**
   - ms-customer-persona (Cliente + Persona)
   - ms-account-movement (Cuenta + Movimiento)

2. ✅ **Comunicación Asíncrona:**
   - RabbitMQ configurado
   - Eventos publicados y consumidos
   - Sincronización de datos entre servicios

3. ✅ **Funcionalidades F1-F5:**
   - F1: CRUDs completos
   - F2: Movimientos con actualización de saldo
   - F3: Validación de saldo insuficiente
   - F4: Reporte de estado de cuenta
   - F5: Pruebas unitarias

4. ✅ **Docker (F7):**
   - Dockerfiles multi-stage
   - Docker Compose funcional

### Deseables:
5. ⭐ **Pruebas de integración (F6):**
   - Test de flujo completo
   - Verificación de eventos
   - Health checks

---

## Diferencias SemiSenior vs Senior

| Aspecto | SemiSenior | Senior |
|---------|------------|--------|
| **Microservicios** | 2 básicos | 2 con patrones avanzados |
| **Comunicación** | RabbitMQ básico | RabbitMQ + Patrones de resiliencia |
| **Resiliencia** | No requerida | **Contemplar** (no obligatorio implementar) |
| **Escalabilidad** | No requerida | **Contemplar** (no obligatorio implementar) |
| **Rendimiento** | No requerido | **Contemplar** (no obligatorio implementar) |
| **Testing** | F5 obligatorio, F6 deseable | F5 y F6 obligatorios |
| **Funcionalidades** | F1-F5 obligatorias, F6 deseable | F1-F7 todas obligatorias |
| **Arquitectura** | Event-driven básico | + Patrones avanzados contemplados |

---

## Endpoints a Implementar

### ms-customer-persona (Puerto 8081)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /clientes | Crear cliente |
| GET | /clientes | Listar clientes |
| GET | /clientes/{id} | Obtener cliente |
| PUT | /clientes/{id} | Actualizar cliente |
| DELETE | /clientes/{id} | Eliminar cliente |

### ms-account-movement (Puerto 8082)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /cuentas | Crear cuenta |
| GET | /cuentas | Listar cuentas |
| GET | /cuentas/{numero} | Obtener cuenta |
| GET | /cuentas?clienteId={id} | Cuentas por cliente |
| PUT | /cuentas/{numero} | Actualizar cuenta |
| DELETE | /cuentas/{numero} | Eliminar cuenta |
| POST | /movimientos | Registrar movimiento |
| GET | /movimientos | Listar movimientos |
| GET | /reportes | Reporte estado de cuenta |

---

## Diagrama de Arquitectura SemiSenior

```
┌─────────────────────────────────────────────┐
│              Cliente/Navegador              │
└─────────────────────┬───────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────┐
│         ms-customer-persona (8081)          │
│                                             │
│  Entidades: Persona, Cliente               │
│  BD: H2                                     │
│                                             │
│  - POST /clientes                          │
│  - GET /clientes                           │
│  - PUT /clientes/{id}                      │
│  - DELETE /clientes/{id}                   │
│                                             │
└───────────┬───────────────────┬─────────────┘
            │                   │
            │ Publica eventos   │
            │ (CLIENTE_CREADO)  │
            ▼                   │
┌─────────────────────────────────────────────┐
│           RabbitMQ (Message Broker)         │
│              (Puerto 5672)                  │
└───────────┬─────────────────────────────────┘
            │ Consume eventos
            ▼
┌─────────────────────────────────────────────┐
│        ms-account-movement (8082)           │
│                                             │
│  Entidades: Cuenta, Movimiento             │
│  BD: H2                                     │
│                                             │
│  - POST /cuentas                           │
│  - GET /cuentas                            │
│  - POST /movimientos                       │
│  - GET /reportes                           │
│                                             │
└─────────────────────────────────────────────┘
```

---

**Nota:** Este documento cubre las funcionalidades requeridas para el perfil SemiSenior, con énfasis en la separación en 2 microservicios y la comunicación asíncrona entre ellos mediante RabbitMQ.
