# Historias de Usuario - Perfil Senior
## Sistema Bancario de Microservicios

**Proyecto:** Backend Bank  
**Perfil:** Senior Developer  
**Fecha:** Abril 2026  
**Alcance:** Funcionalidades F1, F2, F3, F4, F5, F6, F7  
**Arquitectura:** 2 Microservicios + Comunicación Asíncrona

---

## Arquitectura de Microservicios

### Microservicio 1: ms-customer-persona
**Responsabilidad:** Gestión de Clientes y Personas
- Entidades: Persona, Cliente
- Base de datos: PostgreSQL
- Comunicación: REST + Eventos (RabbitMQ/Kafka)

### Microservicio 2: ms-account-movement
**Responsabilidad:** Gestión de Cuentas y Movimientos
- Entidades: Cuenta, Movimiento
- Base de datos: PostgreSQL
- Comunicación: REST + Eventos (RabbitMQ/Kafka)

### Patrones de Arquitectura a Aplicar (Contemplar, no necesariamente implementar)
- **Event-Driven Architecture:** Comunicación asíncrona entre microservicios
- **CQRS:** Separación de comandos y consultas
- **Saga Pattern:** Transacciones distribuidas
- **Circuit Breaker:** Resiliencia en llamadas HTTP
- **Rendimiento:** Paginación, índices, caché
- **Escalabilidad:** Stateless, base de datos por servicio
- **Resiliencia:** Retry, timeout, circuit breaker

**Nota:** Según el enunciado para Senior: "La solución debe contemplar (no necesariamente implementado) factores como: rendimiento, escalabilidad, resiliencia."

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

## EPIC 1: Arquitectura y Comunicación

### HU-ARQ-001: Configurar Service Discovery con Eureka
**Como** arquitecto de soluciones  
**Quiero** un servidor de descubrimiento de servicios  
**Para** que los microservicios se registren y descubran dinámicamente

#### Criterios de Aceptación

```gherkin
Feature: Service Discovery

  Scenario: Eureka Server disponible
    Dado que el servidor Eureka está iniciado
    Cuando accedo a la consola en http://localhost:8761
    Entonces veo el dashboard de Eureka
    Y el estado es UP

  Scenario: Microservicio se registra en Eureka
    Dado que el Eureka Server está corriendo
    Cuando inicio el microservicio ms-customer
    Entonces el servicio aparece registrado en Eureka
    Y el estado es UP

  Scenario: Health check de servicio registrado
    Dado que el microservicio está registrado en Eureka
    Cuando el servicio deja de responder
    Entonces Eureka marca el servicio como DOWN
    Y lo remueve del registro después de 30 segundos
```

**Story Points:** 5  
**Funcionalidad:** Arquitectura

---

### HU-ARQ-002: Implementar API Gateway
**Como** arquitecto de soluciones  
**Quiero** un API Gateway  
**Para** centralizar el acceso a los microservicios

#### Criterios de Aceptación

```gherkin
Feature: API Gateway

  Scenario: Redirección a microservicio de clientes
    Dado que el API Gateway está configurado
    Cuando envío GET a /api/clientes/123
    Entonces el Gateway redirige a ms-customer-persona:8081/clientes/123
    Y retorna la respuesta del microservicio

  Scenario: Redirección a microservicio de cuentas
    Dado que el API Gateway está configurado
    Cuando envío GET a /api/cuentas/456
    Entonces el Gateway redirige a ms-account-movement:8082/cuentas/456
    Y retorna la respuesta del microservicio

  Scenario: Balanceo de carga (opcional)
    Dado que hay 2 instancias de ms-customer-persona registradas
    Cuando envío múltiples peticiones
    Entonces las peticiones se distribuyen entre las instancias
    
  Scenario: Circuit Breaker
    Dado que ms-customer-persona no está disponible
    Cuando envío una petición a /api/clientes/123
    Entonces el Gateway retorna respuesta alternativa
    Y el código de estado es 503
    Y el mensaje indica "Servicio no disponible temporalmente"
```

**Story Points:** 8  
**Funcionalidad:** Arquitectura

---

### HU-ARQ-003: Configurar Broker de Mensajes (RabbitMQ/Kafka)
**Como** arquitecto de soluciones  
**Quiero** un broker de mensajes  
**Para** comunicación asíncrona entre microservicios

#### Criterios de Aceptación

```gherkin
Feature: Broker de Mensajes

  Scenario: RabbitMQ/Kafka disponible
    Dado que el broker está configurado
    Cuando accedo al panel de administración
    Entonces veo las colas/exchanges configurados
    Y no hay mensajes pendientes

  Scenario: Publicación de evento
    Dado que el broker está disponible
    Cuando ms-customer-persona publica un evento "CLIENTE_CREADO"
    Entonces el mensaje llega a la cola correspondiente
    Y puede ser consumido por otros servicios

  Scenario: Consumo de evento
    Dado que existe un mensaje en la cola
    Cuando ms-account-movement consume el evento
    Entonces procesa el mensaje correctamente
    Y confirma la recepción (ack)

  Scenario: Dead Letter Queue
    Dado que un mensaje falla al procesarse 3 veces
    Cuando se alcanza el límite de reintentos
    Entonces el mensaje se mueve a la Dead Letter Queue
    Y se registra el error para análisis
```

**Story Points:** 8  
**Funcionalidad:** Arquitectura

---

### HU-ARQ-004: Implementar Docker Compose para orquestación
**Como** DevOps engineer  
**Quiero** un docker-compose completo  
**Para** levantar toda la infraestructura

#### Criterios de Aceptación

```gherkin
Feature: Docker Orchestration

  Scenario: Levantar infraestructura completa
    Dado que existe docker-compose.yml
    Cuando ejecuto docker-compose up
    Entonces se levantan:
      - Eureka Server (puerto 8761)
      - API Gateway (puerto 8080)
      - RabbitMQ/Kafka (puertos correspondientes)
      - ms-customer-persona (puerto 8081)
      - ms-account-movement (puerto 8082)
    Y todos los servicios están healthy

  Scenario: Servicios se registran correctamente
    Dado que docker-compose está corriendo
    Cuando verifico Eureka
    Entonces veo los servicios registrados
    Y el estado de cada uno es UP

  Scenario: Comunicación entre servicios
    Dado que todos los servicios están corriendo
    Cuando creo un cliente a través del Gateway
    Entonces el cliente se guarda en ms-customer-persona
    Y se emite el evento correspondiente
```

**Story Points:** 5  
**Funcionalidad:** F7

---

## EPIC 2: Microservicio - Cliente y Persona

### HU-CP-001: Crear entidad Persona
**Como** desarrollador  
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
**Como** desarrollador  
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
    Y la contraseña está encriptada

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
    Y el cliente no tiene cuentas asociadas (verificación asíncrona)
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
**Como** desarrollador  
**Quiero** publicar eventos cuando cambian los datos de cliente  
**Para** que otros microservicios reaccionen

#### Criterios de Aceptación

```gherkin
Feature: Eventos de Dominio - Cliente

  Scenario: Evento CLIENTE_CREADO
    Dado que el broker está disponible
    Cuando se crea un nuevo cliente
    Entonces se publica evento "CLIENTE_CREADO"
    Y el payload incluye:
      - clienteId
      - nombre
      - identificacion
      - fechaCreacion

  Scenario: Evento CLIENTE_ACTUALIZADO
    Dado que el broker está disponible
    Cuando se actualiza un cliente
    Entonces se publica evento "CLIENTE_ACTUALIZADO"
    Y el payload incluye los campos modificados

  Scenario: Evento CLIENTE_ELIMINADO
    Dado que el broker está disponible
    Cuando se elimina un cliente
    Entonces se publica evento "CLIENTE_ELIMINADO"
    Y el payload incluye el clienteId
```

**Story Points:** 5  
**Funcionalidad:** Arquitectura

---

### HU-CP-005: Prueba unitaria de entidad Cliente (F5)
**Como** desarrollador  
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
      - Teléfono tiene formato válido

  Scenario: Calcular edad - prueba unitaria adicional
    Dado que tengo una fecha de nacimiento
    Cuando ejecuto el método calcularEdad()
    Entonces retorna la edad correcta
    
  Scenario: Encriptación de contraseña - prueba unitaria adicional
    Dado que tengo una contraseña en texto plano
    Cuando ejecuto el método encriptarContrasena()
    Entonces la contraseña se transforma con BCrypt
    Y no es igual al texto original
```

**Story Points:** 5  
**Funcionalidad:** F5

---

## EPIC 3: Microservicio - Cuenta y Movimiento

### HU-CM-001: Crear entidad Cuenta
**Como** desarrollador  
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
**Como** desarrollador  
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
    Y el formato es ISO 8601
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
    Y se retorna respuesta JSON con:
      - timestamp
      - status: 400
      - error: "Bad Request"
      - message: "Saldo no disponible"
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
      - Información del cliente
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

  Scenario: Reporte para cliente sin cuentas
    Dado que cliente "999" no tiene cuentas
    Cuando solicito GET a /reportes?clienteId=999
    Entonces el sistema responde con código 200
    Y el reporte incluye lista de cuentas vacía
```

**Story Points:** 8  
**Funcionalidad:** F4

---

## EPIC 4: Comunicación Asíncrona

### HU-COMM-001: Consumir eventos de cliente creado
**Como** ms-account-movement  
**Quiero** escuchar eventos de nuevos clientes  
**Para** mantener sincronizada la información de clientes

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

### HU-COMM-002: Saga Pattern para transacciones distribuidas
**Como** arquitecto  
**Quiero** implementar Saga Pattern  
**Para** mantener consistencia entre microservicios

#### Criterios de Aceptación

```gherkin
Feature: Saga Pattern

  Scenario: Saga exitosa - Crear cliente y cuenta
    Dado que inicio transacción para crear cliente con cuenta
    Cuando:
      1. ms-customer-persona crea cliente exitosamente
      2. Emite evento CLIENTE_CREADO
      3. ms-account-movement crea cuenta exitosamente
    Entonces ambas operaciones completan exitosamente
    Y el cliente tiene su cuenta asociada

  Scenario: Saga con compensación - Error al crear cuenta
    Dado que inicio transacción para crear cliente con cuenta
    Cuando:
      1. ms-customer-persona crea cliente exitosamente
      2. ms-account-movement falla al crear cuenta
    Entonces se ejecuta compensación
    Y ms-customer-persona elimina el cliente creado
    Y se retorna error al usuario
```

**Story Points:** 8  
**Funcionalidad:** Arquitectura

---

## EPIC 5: Calidad y Testing

### HU-TEST-001: Pruebas unitarias adicionales
**Como** desarrollador  
**Quiero** cobertura de pruebas unitarias  
**Para** garantizar la calidad del código

#### Criterios de Aceptación

```gherkin
Feature: Pruebas Unitarias Completas

  Scenario: Prueba unitaria - CuentaService
    Dado que tengo mocks de CuentaRepository
    Cuando ejecuto los tests de CuentaServiceTest
    Entonces cubren:
      - Crear cuenta exitosamente
      - Validar saldo inicial negativo
      - Buscar cuenta por número

  Scenario: Prueba unitaria - MovimientoService
    Dado que tengo mocks configurados
    Cuando ejecuto MovimientoServiceTest
    Entonces cubren:
      - Registrar depósito
      - Registrar retiro
      - Validar saldo insuficiente (lanza excepción)
      - Calcular nuevo saldo

  Scenario: Prueba unitaria - Entidad Cuenta
    Cuando ejecuto CuentaTest
    Entonces verifica:
      - Constructor con builder
      - Getters y setters
      - Relación con movimientos
```

**Story Points:** 5  
**Funcionalidad:** F5

---

### HU-TEST-002: Pruebas de integración (F6)
**Como** QA engineer  
**Quiero** pruebas de integración  
**Para** verificar el funcionamiento end-to-end

#### Criterios de Aceptación

```gherkin
Feature: Pruebas de Integración - F6

  Scenario: Prueba de integración - Crear cliente y cuenta
    Dado que la aplicación está corriendo
    Y la base de datos está disponible
    Cuando ejecuto ClienteCuentaIntegrationTest
    Entonces:
      - Se crea cliente en la base de datos
      - Se verifica que el cliente existe
      - Se crea cuenta asociada al cliente
      - Se verifica que la cuenta tiene el clienteId correcto

  Scenario: Prueba de integración - Depósito y actualización de saldo
    Dado que existe cuenta con saldo inicial
    Cuando ejecuto MovimientoIntegrationTest.testDepositoActualizaSaldo()
    Entonces:
      - Se registra el movimiento
      - El saldo de la cuenta se actualiza
      - Se verifica en base de datos el nuevo saldo

  Scenario: Prueba de integración - Retiro con saldo insuficiente
    Dado que existe cuenta con saldo 0
    Cuando ejecuto testRetiroSinSaldo()
    Entonces:
      - Se lanza SaldoNoDisponibleException
      - El saldo permanece en 0
      - Se verifica que no se creó movimiento

  Scenario: Prueba de integración end-to-end
    Dado que uso @SpringBootTest
    Cuando ejecuto:
      1. Crear cliente
      2. Crear cuenta para el cliente
      3. Realizar depósito
      4. Realizar retiro
      5. Consultar saldo
    Entonces todo el flujo funciona correctamente
    Y los datos persisten en la base de datos
```

**Story Points:** 8  
**Funcionalidad:** F6

---

### HU-TEST-003: Pruebas de integración entre microservicios
**Como** QA engineer  
**Quiero** verificar comunicación entre microservicios  
**Para** validar la arquitectura distribuida

#### Criterios de Aceptación

```gherkin
Feature: Pruebas de Integración - Microservicios

  Scenario: Verificar registro en Eureka
    Dado que Eureka Server está corriendo
    Cuando inicio ms-customer-persona y ms-account-movement
    Entonces ambos aparecen registrados en Eureka
    Y el health check es positivo

  Scenario: Comunicación vía Feign Client
    Dado que los microservicios están registrados
    Cuando ms-account-movement necesita datos de cliente
    Entonces realiza llamada HTTP a ms-customer-persona
    Y recibe respuesta exitosa

  Scenario: Comunicación asíncrona vía RabbitMQ
    Dado que RabbitMQ está configurado
    Cuando ms-customer-persona publica evento
    Entonces ms-account-movement lo recibe y procesa
    Y se puede verificar en logs
```

**Story Points:** 5  
**Funcionalidad:** F6

---

## EPIC 6: Rendimiento, Escalabilidad y Resiliencia

### HU-NFR-001: Consideraciones de Rendimiento
**Como** arquitecto  
**Quiero** considerar rendimiento  
**Para** soportar alta carga

#### Criterios de Aceptación

```gherkin
Feature: Consideraciones de Rendimiento

  Scenario: Paginación en listados
    Dado que existen 1000 cuentas
    Cuando solicito GET /cuentas?page=0&size=20
    Entonces retorna solo 20 registros
    Y incluye metadata de paginación

  Scenario: Índices en base de datos
    Dado que se crean las entidades
    Entonces deben existir índices en:
      - identificacion (Cliente)
      - numeroCuenta (Cuenta)
      - fecha (Movimiento)
      - cuenta_id (Movimiento)

  Scenario: Caché de consultas frecuentes (opcional)
    Dado que implemento Spring Cache
    Cuando consulto cliente frecuentemente
    Entonces la segunda consulta es más rápida
```

**Story Points:** 3  
**Funcionalidad:** NFR

---

### HU-NFR-002: Consideraciones de Escalabilidad
**Como** arquitecto  
**Quiero** diseñar para escalar  
**Para** soportar crecimiento

#### Criterios de Aceptación

```gherkin
Feature: Consideraciones de Escalabilidad

  Scenario: Microservicios stateless
    Dado que los servicios están diseñados
    Entonces no mantienen estado en memoria
    Y pueden escalar horizontalmente

  Scenario: Base de datos por microservicio
    Dado que sigo Database-per-Service pattern
    Entonces cada microservicio tiene su propia BD
    Y pueden escalar independientemente

  Scenario: Event-driven para acoplamiento débil
    Dado que uso comunicación asíncrona
    Entonces los servicios están débilmente acoplados
    Y pueden escalar independientemente
```

**Story Points:** 3  
**Funcionalidad:** NFR

---

### HU-NFR-003: Consideraciones de Resiliencia
**Como** arquitecto  
**Quiero** diseñar para resiliencia  
**Para** tolerar fallos

#### Criterios de Aceptación

```gherkin
Feature: Consideraciones de Resiliencia

  Scenario: Circuit Breaker implementado
    Dado que uso Resilience4j
    Cuando un servicio externo falla
    Entonces el Circuit Breaker se abre
    Y se retorna respuesta fallback

  Scenario: Retry policy
    Dado que operaciones pueden fallar temporalmente
    Cuando una operación falla
    Entonces se reintenta hasta 3 veces
    Con backoff exponencial

  Scenario: Timeout en llamadas HTTP
    Dado que configuro Feign Client
    Entonces las llamadas tienen timeout de 5 segundos
    Y después retornan error controlado

  Scenario: Health checks
    Dado que implemento Spring Boot Actuator
    Cuando accedo a /actuator/health
    Entonces veo estado de conexiones (BD, RabbitMQ, etc.)
```

**Story Points:** 5  
**Funcionalidad:** NFR

---

## EPIC 7: Despliegue (F7)

### HU-DEP-001: Docker Multi-stage Build
**Como** DevOps engineer  
**Quiero** imágenes Docker optimizadas  
**Para** despliegue en producción

#### Criterios de Aceptación

```gherkin
Feature: Docker Multi-stage Build

  Scenario: Imagen ligera con Alpine
    Dado que existe Dockerfile
    Cuando ejecuto docker build
    Entonces:
      - Usa imagen base eclipse-temurin:8-jre-alpine
      - Es multi-stage (build + runtime)
      - Tamaño final < 200MB
      - Usuario no-root

  Scenario: Dockerfile por microservicio
    Dado que existen 2 microservicios
    Entonces cada uno tiene su Dockerfile
    Y siguen las mejores prácticas:
      - Layer caching optimizado
      - Solo copia archivos necesarios
      - Health check configurado
```

**Story Points:** 5  
**Funcionalidad:** F7

---

### HU-DEP-002: Docker Compose completo
**Como** DevOps engineer  
**Quiero** orquestación con Docker Compose  
**Para** levantar todo el ecosistema

#### Criterios de Aceptación

```gherkin
Feature: Docker Compose Orchestration

  Scenario: Servicios definidos en docker-compose
    Dado que existe docker-compose.yml
    Entonces incluye:
      - eureka-server (puerto 8761)
      - api-gateway (puerto 8080)
      - rabbitmq (puerto 5672, 15672)
      - ms-customer-persona (puerto 8081)
      - ms-account-movement (puerto 8082)

  Scenario: Variables de entorno configuradas
    Dado que existe archivo .env o config
    Entonces cada servicio tiene:
      - Puerto configurado
      - Conexión a base de datos
      - Conexión a Eureka
      - Conexión a RabbitMQ

  Scenario: Dependencias entre servicios
    Dado que existen dependencias
    Entonces:
      - Eureka debe iniciar primero
      - RabbitMQ debe estar disponible
      - Los microservicios esperan dependencias healthy

  Scenario: Volúmenes para persistencia
    Dado que se requiere persistencia
    Entonces:
      - Datos de H2 persisten en volumen
      - Logs se escriben en volumen
      - Configuraciones externas montadas
```

**Story Points:** 5  
**Funcionalidad:** F7

---

## Resumen de Historias - Senior

### Por Funcionalidad

| Funcionalidad | Historias | Story Points |
|--------------|-----------|--------------|
| **F1 - CRUDs** | HU-CP-001 a HU-CP-004, HU-CM-001 a HU-CM-003 | 33 |
| **F2 - Movimientos** | HU-CM-004 | 8 |
| **F3 - Validación Saldo** | HU-CM-005 | 5 |
| **F4 - Reportes** | HU-CM-006 | 8 |
| **F5 - Pruebas Unitarias** | HU-CP-005, HU-TEST-001 | 10 |
| **F6 - Pruebas Integración** | HU-TEST-002, HU-TEST-003 | 13 |
| **F7 - Docker** | HU-ARQ-004, HU-DEP-001, HU-DEP-002 | 15 |
| **Arquitectura** | HU-ARQ-001 a HU-ARQ-003, HU-COMM-001, HU-COMM-002 | 34 |
| **NFR** | HU-NFR-001 a HU-NFR-003 | 11 |

**Total:** 137 Story Points

---

## Entregables del Perfil Senior

1. ✅ **Microservicios implementados:**
   - ms-customer-persona (Cliente + Persona)
   - ms-account-movement (Cuenta + Movimiento)

2. ✅ **Comunicación Asíncrona:**
   - RabbitMQ/Kafka configurado
   - Eventos publicados y consumidos
   - Saga Pattern implementado

3. ✅ **Todas las funcionalidades F1-F7:**
   - CRUDs completos
   - Movimientos con actualización de saldo
   - Validación de saldo insuficiente
   - Reporte de estado de cuenta
   - Pruebas unitarias
   - Pruebas de integración
   - Despliegue en Docker

4. ✅ **Arquitectura Distribuida:**
   - Service Discovery (Eureka)
   - API Gateway
   - Circuit Breaker
   - Resiliencia implementada

5. ✅ **Consideraciones NFR:**
   - Rendimiento (paginación, índices)
   - Escalabilidad (stateless, BD independiente)
   - Resiliencia (circuit breaker, retry, timeout)

---

## Tecnologías Recomendadas

| Componente | Tecnología |
|------------|------------|
| Framework | Spring Boot 2.7.x |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Comunicación Async | RabbitMQ / Apache Kafka |
| Base de Datos | PostgreSQL |
| Circuit Breaker | Resilience4j |
| Feign Client | OpenFeign |
| Testing | JUnit 5, Mockito, Testcontainers |
| Docker | Multi-stage build, Alpine |
| Documentación | OpenAPI (Swagger) |

---

## Diagramas de Arquitectura

### Diagrama de Componentes
```
┌─────────────────────────────────────────────────────────┐
│                      Cliente/Navegador                  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                   API Gateway (8080)                     │
│              - Spring Cloud Gateway                      │
│              - Circuit Breaker                         │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Service Discovery (Eureka)                  │
│                      (8761)                              │
└────────────┬─────────────────────────────┬──────────────┘
             │                             │
             ▼                             ▼
┌──────────────────────────┐    ┌──────────────────────────┐
│  ms-customer-persona     │    │  ms-account-movement     │
│       (8081)             │    │       (8082)             │
│                          │    │                          │
│  - Entidades:            │◄───┤  - Entidades:            │
│    Persona, Cliente      │    │    Cuenta, Movimiento    │
│                          │    │                          │
│  - BD: H2                │    │  - BD: H2                │
│                          │    │                          │
└───────────┬──────────────┘    └───────────┬──────────────┘
            │                               │
            └───────────────┬─────────────────┘
                            │
                            ▼
            ┌───────────────────────────────┐
            │    RabbitMQ / Kafka           │
            │    (Broker de Mensajes)       │
            └───────────────────────────────┘
```

---

**Nota:** Este documento cubre TODAS las funcionalidades requeridas para el perfil Senior, incluyendo arquitectura de microservicios, comunicación asíncrona, y consideraciones de rendimiento, escalabilidad y resiliencia.
