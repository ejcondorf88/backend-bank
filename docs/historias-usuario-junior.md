# Historias de Usuario - Perfil Junior
## Sistema Bancario de Microservicios

**Proyecto:** Backend Bank  
**Perfil:** Junior Developer  
**Fecha:** Abril 2026  
**Alcance:** Funcionalidades F1, F2, F3

---

## Principio INVEST

Todas las historias siguen el principio **INVEST**:
- **I**ndependiente: No dependen de otras para implementarse
- **N**egociable: Los detalles son discutibles
- **V**aliosa: Aportan valor al negocio
- **E**stimable: Esfuerzo claro y conocido
- **S**mall: Pequenas para completar en sprint
- **T**estable: Criterios verificables

---

## EPIC: Gestion de Clientes

### HU-001: Crear nuevo cliente
**Como** administrador del sistema bancario  
**Quiero** registrar un cliente con sus datos personales  
**Para** gestionar sus cuentas y movimientos

#### Criterios de Aceptacion (Gherkin)

```gherkin
Feature: Crear nuevo cliente

  Scenario: Crear cliente con datos validos
    Dado que el sistema esta funcionando
    Y no existe un cliente con identificacion "1720456325"
    Cuando envio una solicitud POST a "/clientes" con:
      - nombre: "Jose Lema"
      - genero: "Masculino"
      - edad: 35
      - identificacion: "1720456325"
      - direccion: "Otavalo sn y principal"
      - telefono: "098254785"
      - contrasena: "1234"
      - estado: true
    Entonces el sistema responde con codigo 201
    Y el cliente es guardado en la base de datos
    Y la respuesta incluye un identificador unico

  Scenario: No permitir identificacion duplicada
    Dado que existe un cliente con identificacion "1720456325"
    Cuando intento crear cliente con identificacion "1720456325"
    Entonces el sistema responde con codigo 409
    Y el mensaje indica "El cliente ya existe"

  Scenario: Validar campos obligatorios
    Dado que el sistema esta funcionando
    Cuando envio POST a "/clientes" sin el campo "nombre"
    Entonces el sistema responde con codigo 400
    Y el mensaje indica "El campo nombre es obligatorio"

  Scenario: Validar formato de telefono
    Dado que el sistema esta funcionando
    Cuando envio POST a "/clientes" con telefono "abc123"
    Entonces el sistema responde con codigo 400
    Y el mensaje indica "Formato de telefono invalido"
```

**Story Points:** 5  
**Funcionalidad:** F1

---

### HU-002: Consultar cliente
**Como** administrador del sistema bancario  
**Quiero** consultar la informacion de un cliente  
**Para** verificar sus datos personales

#### Criterios de Aceptacion

```gherkin
Feature: Consultar cliente

  Scenario: Obtener cliente existente
    Dado que existe un cliente con identificacion "1720456325"
    Cuando envio GET a "/clientes/1720456325"
    Entonces el sistema responde con codigo 200
    Y la respuesta incluye nombre, identificacion, direccion, telefono y estado

  Scenario: Cliente no encontrado
    Dado que no existe un cliente con identificacion "9999999999"
    Cuando envio GET a "/clientes/9999999999"
    Entonces el sistema responde con codigo 404
    Y el mensaje indica "Cliente no encontrado"

  Scenario: Listar todos los clientes
    Dado que existen clientes en el sistema
    Cuando envio GET a "/clientes"
    Entonces el sistema responde con codigo 200
    Y la respuesta incluye una lista de clientes
```

**Story Points:** 3  
**Funcionalidad:** F1

---

### HU-003: Actualizar cliente
**Como** administrador del sistema bancario  
**Quiero** actualizar la informacion de un cliente  
**Para** mantener sus datos actualizados

#### Criterios de Aceptacion

```gherkin
Feature: Actualizar cliente

  Scenario: Actualizar direccion de cliente
    Dado que existe un cliente con identificacion "1720456325"
    Y su direccion actual es "Otavalo sn y principal"
    Cuando envio PUT a "/clientes/1720456325" con direccion "Amazonas y NNUU, Quito"
    Entonces el sistema responde con codigo 200
    Y la direccion se actualiza en la base de datos

  Scenario: No permitir actualizar identificacion
    Dado que existe un cliente con identificacion "1720456325"
    Cuando intento actualizar el campo identificacion
    Entonces el sistema responde con codigo 400
    Y el mensaje indica "La identificacion no puede modificarse"

  Scenario: Actualizar cliente inexistente
    Dado que no existe un cliente con identificacion "9999999999"
    Cuando envio PUT a "/clientes/9999999999"
    Entonces el sistema responde con codigo 404
```

**Story Points:** 3  
**Funcionalidad:** F1

---

### HU-004: Eliminar cliente
**Como** administrador del sistema bancario  
**Quiero** eliminar un cliente del sistema  
**Para** dar de baja clientes

#### Criterios de Aceptacion

```gherkin
Feature: Eliminar cliente

  Scenario: Eliminar cliente sin cuentas
    Dado que existe un cliente con identificacion "1720456325"
    Y el cliente no tiene cuentas asociadas
    Cuando envio DELETE a "/clientes/1720456325"
    Entonces el sistema responde con codigo 204
    Y el cliente se elimina de la base de datos

  Scenario: No permitir eliminar con cuentas
    Dado que existe un cliente con identificacion "1720456325"
    Y el cliente tiene cuentas asociadas
    Cuando envio DELETE a "/clientes/1720456325"
    Entonces el sistema responde con codigo 409
    Y el mensaje indica "No se puede eliminar: tiene cuentas asociadas"

  Scenario: Eliminar cliente inexistente
    Dado que no existe un cliente con identificacion "9999999999"
    Cuando envio DELETE a "/clientes/9999999999"
    Entonces el sistema responde con codigo 404
```

**Story Points:** 3  
**Funcionalidad:** F1

---

## EPIC: Gestion de Cuentas

### HU-005: Crear cuenta bancaria
**Como** administrador del sistema bancario  
**Quiero** crear una cuenta para un cliente  
**Para** que realice operaciones bancarias

#### Criterios de Aceptacion

```gherkin
Feature: Crear cuenta bancaria

  Scenario: Crear cuenta de ahorros
    Dado que existe un cliente con identificacion "1720456325"
    Y no existe cuenta con numero "478758"
    Cuando envio POST a "/cuentas" con:
      - numeroCuenta: "478758"
      - tipoCuenta: "Ahorro"
      - saldoInicial: 2000.00
      - estado: true
      - identificacion: "1720456325"
    Entonces el sistema responde con codigo 201
    Y la cuenta se asocia al cliente
    Y el saldo inicial es 2000.00

  Scenario: Crear cuenta de corriente
    Dado que existe un cliente con identificacion "0926548751"
    Cuando envio POST a "/cuentas" con tipoCuenta "Corriente"
    Entonces el sistema responde con codigo 201
    Y el tipo es "Corriente"

  Scenario: No permitir cuenta duplicada
    Dado que existe cuenta con numero "478758"
    Cuando intento crear cuenta con numero "478758"
    Entonces el sistema responde con codigo 409
    Y el mensaje indica "La cuenta ya existe"

  Scenario: No permitir cuenta para cliente inexistente
    Dado que no existe cliente con identificacion "9999999999"
    Cuando intento crear cuenta para este cliente
    Entonces el sistema responde con codigo 404
    Y el mensaje indica "Cliente no encontrado"

  Scenario: Validar saldo inicial negativo
    Cuando envio POST a "/cuentas" con saldoInicial -100
    Entonces el sistema responde con codigo 400
    Y el mensaje indica "El saldo inicial no puede ser negativo"
```

**Story Points:** 5  
**Funcionalidad:** F1

---

### HU-006: Consultar cuentas
**Como** administrador del sistema bancario  
**Quiero** consultar las cuentas de un cliente  
**Para** verificar su estado

#### Criterios de Aceptacion

```gherkin
Feature: Consultar cuentas

  Scenario: Obtener cuenta por numero
    Dado que existe cuenta con numero "478758"
    Cuando envio GET a "/cuentas/478758"
    Entonces el sistema responde con codigo 200
    Y la respuesta incluye numero, tipo, saldo y estado

  Scenario: Listar cuentas de cliente
    Dado que existe cliente con identificacion "1720456325"
    Y el cliente tiene 2 cuentas
    Cuando envio GET a "/clientes/1720456325/cuentas"
    Entonces el sistema responde con codigo 200
    Y la respuesta incluye 2 cuentas
```

**Story Points:** 3  
**Funcionalidad:** F1

---

### HU-007: Actualizar cuenta
**Como** administrador del sistema bancario  
**Quiero** actualizar el estado de una cuenta  
**Para** activar o desactivarla

#### Criterios de Aceptacion

```gherkin
Feature: Actualizar cuenta

  Scenario: Desactivar cuenta existente
    Dado que existe cuenta "478758" activa
    Cuando envio PUT a "/cuentas/478758" con estado false
    Entonces el sistema responde con codigo 200
    Y la cuenta se desactiva

  Scenario: No permitir modificar numero de cuenta
    Dado que existe cuenta "478758"
    Cuando intento actualizar el numero de cuenta
    Entonces el sistema responde con codigo 400
```

**Story Points:** 2  
**Funcionalidad:** F1

---

### HU-008: Eliminar cuenta
**Como** administrador del sistema bancario  
**Quiero** eliminar una cuenta  
**Para** dar de baja cuentas

#### Criterios de Aceptacion

```gherkin
Feature: Eliminar cuenta

  Scenario: Eliminar cuenta sin movimientos
    Dado que existe cuenta "478758"
    Y la cuenta no tiene movimientos
    Cuando envio DELETE a "/cuentas/478758"
    Entonces el sistema responde con codigo 204

  Scenario: No permitir eliminar cuenta con saldo
    Dado que existe cuenta "478758"
    Y el saldo actual es mayor a 0
    Cuando intento eliminar la cuenta
    Entonces el sistema responde con codigo 409
    Y el mensaje indica "No se puede eliminar: tiene saldo disponible"
```

**Story Points:** 2  
**Funcionalidad:** F1

---

## EPIC: Gestion de Movimientos

### HU-009: Registrar deposito
**Como** cliente del banco  
**Quiero** realizar un deposito  
**Para** aumentar mi saldo

#### Criterios de Aceptacion

```gherkin
Feature: Registrar deposito

  Scenario: Deposito exitoso en cuenta de ahorros
    Dado que existe cuenta "225487" con saldo 100.00
    Cuando envio POST a "/movimientos" con:
      - numeroCuenta: "225487"
      - tipoMovimiento: "Deposito"
      - valor: 600.00
    Entonces el sistema responde con codigo 201
    Y se registra el movimiento con fecha actual
    Y el saldo de la cuenta se actualiza a 700.00

  Scenario: Deposito en cuenta de corriente
    Dado que existe cuenta "495878" con saldo 0.00
    Cuando envio POST a "/movimientos" con valor 150.00
    Entonces el sistema responde con codigo 201
    Y el saldo se actualiza a 150.00

  Scenario: Deposito en cuenta inexistente
    Dado que no existe cuenta "999999"
    Cuando envio POST a "/movimientos" para cuenta "999999"
    Entonces el sistema responde con codigo 404
    Y el mensaje indica "Cuenta no encontrada"
```

**Story Points:** 5  
**Funcionalidad:** F2

---

### HU-010: Registrar retiro
**Como** cliente del banco  
**Quiero** realizar un retiro  
**Para** disponer de efectivo

#### Criterios de Aceptacion

```gherkin
Feature: Registrar retiro

  Scenario: Retiro exitoso con saldo suficiente
    Dado que existe cuenta "478758" con saldo 2000.00
    Cuando envio POST a "/movimientos" con:
      - numeroCuenta: "478758"
      - tipoMovimiento: "Retiro"
      - valor: 575.00
    Entonces el sistema responde con codigo 201
    Y el saldo se actualiza a 1425.00
    Y el movimiento registra valor -575.00

  Scenario: Retiro que deja saldo en cero
    Dado que existe cuenta "496825" con saldo 540.00
    Cuando envio POST a "/movimientos" con valor 540.00
    Entonces el sistema responde con codigo 201
    Y el saldo se actualiza a 0.00
```

**Story Points:** 5  
**Funcionalidad:** F2

---

### HU-011: Validar saldo insuficiente (F3)
**Como** cliente del banco  
**Quiero** recibir alerta cuando no tenga saldo  
**Para** evitar operaciones invalidas

#### Criterios de Aceptacion

```gherkin
Feature: Validar saldo insuficiente

  Scenario: Retiro rechazado por saldo insuficiente
    Dado que existe cuenta "495878" con saldo 0.00
    Cuando envio POST a "/movimientos" con:
      - numeroCuenta: "495878"
      - tipoMovimiento: "Retiro"
      - valor: 100.00
    Entonces el sistema responde con codigo 400
    Y el mensaje es "Saldo no disponible"
    Y el saldo permanece en 0.00
    Y no se registra ningun movimiento

  Scenario: Retiro rechazado cuando monto excede saldo
    Dado que existe cuenta "478758" con saldo 2000.00
    Cuando intento retirar 2500.00
    Entonces el sistema responde con codigo 400
    Y el mensaje es "Saldo no disponible"
    Y el saldo permanece en 2000.00

  Scenario: Retiro rechazado en cuenta inactiva
    Dado que existe cuenta "478758" desactivada
    Cuando intento retirar 100.00
    Entonces el sistema responde con codigo 400
    Y el mensaje indica "La cuenta no esta activa"
```

**Story Points:** 5  
**Funcionalidad:** F3

---

### HU-012: Consultar movimientos
**Como** administrador del sistema  
**Quiero** consultar los movimientos de una cuenta  
**Para** revisar el historial

#### Criterios de Aceptacion

```gherkin
Feature: Consultar movimientos

  Scenario: Listar movimientos de cuenta
    Dado que existe cuenta "225487" con 3 movimientos
    Cuando envio GET a "/movimientos?numeroCuenta=225487"
    Entonces el sistema responde con codigo 200
    Y la respuesta incluye 3 movimientos
    Y cada movimiento incluye fecha, tipo, valor y saldo

  Scenario: Filtrar por rango de fechas
    Dado que existen movimientos en fechas especificas
    Cuando envio GET a "/movimientos?fechaInicio=2022-02-01&fechaFin=2022-02-15"
    Entonces el sistema responde con codigo 200
    Y la respuesta incluye solo movimientos del rango
```

**Story Points:** 3  
**Funcionalidad:** F1/F2

---

## Resumen de Historias - Junior

| Historia | Descripcion | Story Points | Funcionalidad |
|----------|-------------|--------------|---------------|
| HU-001 | Crear cliente | 5 | F1 |
| HU-002 | Consultar cliente | 3 | F1 |
| HU-003 | Actualizar cliente | 3 | F1 |
| HU-004 | Eliminar cliente | 3 | F1 |
| HU-005 | Crear cuenta | 5 | F1 |
| HU-006 | Consultar cuentas | 3 | F1 |
| HU-007 | Actualizar cuenta | 2 | F1 |
| HU-008 | Eliminar cuenta | 2 | F1 |
| HU-009 | Registrar deposito | 5 | F2 |
| HU-010 | Registrar retiro | 5 | F2 |
| HU-011 | Validar saldo insuficiente | 5 | F3 |
| HU-012 | Consultar movimientos | 3 | F1/F2 |

**Total:** 44 Story Points

---

## Endpoints REST a Implementar

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| POST | /clientes | Crear cliente |
| GET | /clientes | Listar clientes |
| GET | /clientes/{id} | Obtener cliente |
| PUT | /clientes/{id} | Actualizar cliente |
| DELETE | /clientes/{id} | Eliminar cliente |
| POST | /cuentas | Crear cuenta |
| GET | /cuentas | Listar cuentas |
| GET | /cuentas/{numero} | Obtener cuenta |
| GET | /clientes/{id}/cuentas | Cuentas de cliente |
| PUT | /cuentas/{numero} | Actualizar cuenta |
| DELETE | /cuentas/{numero} | Eliminar cuenta |
| POST | /movimientos | Registrar movimiento |
| GET | /movimientos | Listar movimientos |

---

## Notas Tecnicas

### Patrones a aplicar:
1. Repository Pattern para acceso a datos
2. DTO Pattern para separar entidades de API
3. Exception Handling centralizado
4. Bean Validation (@NotNull, @Size, etc.)

### Estructura de paquetes sugerida:
```
com.bank.msaccount/
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── repository/
└── service/
    └── impl/
```

### Validaciones importantes:
- Cliente: identificacion unica, campos obligatorios
- Cuenta: numero unico, saldo >= 0, tipos permitidos (Ahorro/Corriente)
- Movimiento: validar saldo antes de retiro, registrar fecha automatica
