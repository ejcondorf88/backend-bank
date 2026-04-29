# 💰 ms-account

## Descripción

**ms-account** es el microservicio de **Gestión de Cuentas y Movimientos** del sistema Backend Bank. Implementa los requisitos funcionales F2 (registro de movimientos), F3 (saldo no disponible) y F4 (reportes por fechas) con arquitectura hexagonal.

## Funcionalidades

### F1 - Gestión de Cuentas (CRUD)

| Funcionalidad | Endpoint | Descripción |
|---------------|----------|-------------|
| **Crear cuenta** | `POST /api/accounts` | Nueva cuenta (Ahorro/Corriente) |
| **Obtener cuenta** | `GET /api/accounts/{number}` | Buscar por número de cuenta |
| **Listar cuentas** | `GET /api/accounts` | Todas las cuentas |
| **Cuentas por cliente** | `GET /api/accounts/client/{clientId}` | Filtrar por cliente |
| **Actualizar cuenta** | `PUT /api/accounts/{number}` | Modificar datos |
| **Eliminar cuenta** | `DELETE /api/accounts/{number}` | Eliminar si no tiene movimientos |
| **Activar/Desactivar** | `PATCH /api/accounts/{number}/activate` | Cambiar estado |

### F2 - Registro de Movimientos

| Funcionalidad | Endpoint | Descripción |
|---------------|----------|-------------|
| **Depositar** | `POST /api/movements/{number}/deposit` | Aumenta saldo |
| **Retirar** | `POST /api/movements/{number}/withdraw` | Disminuye saldo |
| **Crear movimiento** | `POST /api/movements` | Movimiento genérico |
| **Obtener movimiento** | `GET /api/movements/{id}` | Buscar por ID |
| **Movimientos por cuenta** | `GET /api/movements/account/{number}` | Historial de cuenta |
| **Listar movimientos** | `GET /api/movements` | Todos los movimientos |

### F3 - Validación de Saldo

| Escenario | Comportamiento |
|-----------|----------------|
| Retiro sin saldo | HTTP 400 - "Saldo no disponible" |
| Retiro > saldo | HTTP 400 - "Saldo no disponible" |
| Cuenta inactiva | HTTP 400 - "Cuenta inactiva" |

### F4 - Reportes

| Funcionalidad | Endpoint | Parámetros |
|---------------|----------|------------|
| **Reporte por fechas** | `GET /api/movements/report` | `fechaInicio`, `fechaFin` |
| **Reporte por cuenta** | `GET /api/movements/report/{number}` | `fechaInicio`, `fechaFin` |
| **Movimientos por cliente** | `GET /api/movements/client/{clientId}` | - |

## Arquitectura Hexagonal

```
┌─────────────────────────────────────────────────────────────┐
│              💰 ms-account (Port: 8082)                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  💙 INFRASTRUCTURE (Adaptadores)                            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ REST Controllers                                    │   │
│  │ • AccountController  (/api/accounts)                 │   │
│  │ • MovementController (/api/movements)              │   │
│  │                                                    │   │
│  │ Persistencia                                       │   │
│  │ • AccountJpaEntity, MovementJpaEntity              │   │
│  │ • AccountJpaRepository, MovementJpaRepository      │   │
│  │ • AccountRepositoryImpl, MovementRepositoryImpl    │   │
│  │ • AccountJpaMapper, MovementJpaMapper            │   │
│  │                                                    │   │
│  │ Exception Handling                                 │   │
│  │ • GlobalExceptionHandler                           │   │
│  └─────────────────────────────────────────────────────┘   │
│                          ▲                                  │
│                          │                                  │
│  🧡 APPLICATION          │                                  │
│  ┌───────────────────────▼─────────────────────────────┐   │
│  │                                                     │   │
│  │ Application Services                                 │   │
│  │ • AccountApplicationService  (Cuentas)             │   │
│  │ • MovementApplicationService (Movimientos)         │   │
│  │                                                     │   │
│  │ DTOs                                               │   │
│  │ • AccountRequestDto, AccountResponseDto            │   │
│  │ • MovementRequestDto, MovementResponseDto          │   │
│  │ • ReportRequestDto, ReportResponseDto (F4)         │   │
│  │                                                     │   │
│  │ Mappers (MapStruct)                                │   │
│  │ • AccountApplicationMapper                         │   │
│  │ • MovementApplicationMapper                        │   │
│  │                                                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                          ▲                                  │
│                          │                                  │
│  💛 DOMAIN               │                                  │
│  ┌───────────────────────▼─────────────────────────────┐   │
│  │                                                     │   │
│  │ Entities (Puras)                                   │   │
│  │ • Account  (Cuenta bancaria)                      │   │
│  │   - accountNumber, accountType                     │   │
│  │   - balance, active, clientId                      │   │
│  │   - deposit(), withdraw()                          │   │
│  │                                                    │   │
│  │ • Movement (Movimiento/Transacción)               │   │
│  │   - accountNumber, date, type                    │   │
│  │   - amount, balance                                │   │
│  │   - createDeposit(), createWithdrawal()            │   │
│  │                                                    │   │
│  │ Repository Ports (Interfaces)                      │   │
│  │ • AccountRepository                                │   │
│  │ • MovementRepository                               │   │
│  │                                                    │   │
│  │ Domain Services                                    │   │
│  │ • AccountService (interface)                       │   │
│  │ • MovementService (interface)                      │   │
│  │                                                    │   │
│  │ Domain Exceptions                                  │   │
│  │ • InsufficientBalanceException (F3)                 │   │
│  │ • AccountNotFoundException                         │   │
│  │ • AccountAlreadyExistsException                    │   │
│  │ • InvalidAccountStateException                       │   │
│  │ • MovementNotFoundException                        │   │
│  │ • InvalidAccountTypeException                        │   │
│  │ • InvalidMovementTypeException                       │   │
│  │                                                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Las 3 Capas Explicadas

### 💛 Capa de Dominio (Core)

**Responsabilidad**: Lógica de negocio pura de cuentas y movimientos.

#### Account (Entidad de Cuenta)

**Atributos**:
- `accountNumber`: Identificador único (ej: "478758")
- `accountType`: "Ahorro" o "Corriente"
- `balance`: Saldo actual (BigDecimal)
- `active`: Estado de la cuenta
- `clientId`: ID del cliente dueño

**Comportamiento** (métodos de negocio):
```java
// F2 - Depósito
public void deposit(BigDecimal amount) {
    if (!active) throw new InvalidAccountStateException("Inactive");
    if (amount <= 0) throw new IllegalArgumentException("Positive");
    this.balance = this.balance.add(amount);
}

// F2/F3 - Retiro con validación de saldo
public void withdraw(BigDecimal amount) {
    if (!active) throw new InvalidAccountStateException("Inactive");
    if (amount <= 0) throw new IllegalArgumentException("Positive");
    if (this.balance.compareTo(amount) < 0) {
        throw new InsufficientBalanceException("Saldo no disponible"); // F3
    }
    this.balance = this.balance.subtract(amount);
}
```

**Reglas de Negocio**:
1. Solo se puede depositar/retirar en cuentas activas
2. El monto debe ser positivo
3. No se puede retirar más del saldo disponible (F3)
4. El tipo solo puede ser "Ahorro" o "Corriente"

#### Movement (Entidad de Movimiento)

**Atributos**:
- `accountNumber`: Cuenta asociada
- `date`: Fecha/hora del movimiento (automática)
- `type`: "Deposito" o "Retiro"
- `amount`: Monto (positivo para depósito, negativo para retiro)
- `balance`: Saldo después del movimiento

**Factory Methods**:
```java
// F2 - Crear depósito
public static Movement createDeposit(
    String accountNumber, 
    BigDecimal amount, 
    BigDecimal balanceAfter
) {
    return new Movement(
        accountNumber, 
        LocalDateTime.now(), 
        "Deposito", 
        amount.abs(),           // Siempre positivo
        balanceAfter
    );
}

// F2 - Crear retiro
public static Movement createWithdrawal(
    String accountNumber, 
    BigDecimal amount, 
    BigDecimal balanceAfter
) {
    return new Movement(
        accountNumber, 
        LocalDateTime.now(), 
        "Retiro", 
        amount.abs().negate(),  // Siempre negativo
        balanceAfter
    );
}
```

**Reglas de Negocio**:
1. Tipo solo puede ser "Deposito" o "Retiro"
2. Monto no puede ser cero
3. Fecha se asigna automáticamente si no se provee
4. Balance debe ser no negativo

#### Excepciones de Dominio

| Excepción | Cuándo ocurre | Mensaje |
|-----------|---------------|---------|
| **InsufficientBalanceException** | F3 - Retiro sin saldo | "Saldo no disponible" |
| **AccountNotFoundException** | Cuenta no existe | "Account not found: {number}" |
| **AccountAlreadyExistsException** | Duplicado | "Account already exists: {number}" |
| **InvalidAccountStateException** | Cuenta inactiva | "Cannot deposit/withdraw: inactive" |
| **MovementNotFoundException** | Movimiento no existe | "Movement not found: {id}" |

**Características del Dominio**:
- ❌ **Sin Spring**: No usa @Entity, @Service, @Autowired
- ❌ **Sin JPA**: No importa jakarta.persistence
- ✅ **Solo Java SE**: Classes puras, JDK estándar
- ✅ **Inmutabilidad**: Objetos creados con constructores validados
- ✅ **Validaciones**: En constructores y métodos de negocio

### 🧡 Capa de Aplicación (Casos de Uso)

**Responsabilidad**: Orquestar operaciones entre el dominio y la infraestructura.

#### AccountApplicationService

**Casos de Uso**:
- Crear cuenta (validar número único, asociar a cliente)
- Buscar cuenta (por número, por ID, todas)
- Actualizar cuenta (cambiar tipo, estado)
- Activar/Desactivar cuenta
- Eliminar cuenta (validar que no tenga movimientos)

#### MovementApplicationService (F2)

**Casos de Uso Clave**:

**1. Crear Depósito**:
```
1. Buscar cuenta por número
2. Validar que esté activa
3. Ejecutar account.deposit(amount)  ← Lógica de dominio
4. Crear Movement.createDeposit(...)
5. Guardar cuenta y movimiento
6. Retornar MovementResponseDto
```

**2. Crear Retiro (con F3)**:
```
1. Buscar cuenta por número
2. Validar que esté activa
3. Ejecutar account.withdraw(amount)  ← Lanza InsufficientBalanceException si aplica
4. Si hay excepción → HTTP 400 con "Saldo no disponible"
5. Crear Movement.createWithdrawal(...)
6. Guardar cuenta y movimiento
7. Retornar MovementResponseDto
```

**3. Consultas para Reportes (F4)**:
```
- findByDateRange(fechaInicio, fechaFin)
- findByAccountAndDateRange(accountNumber, fechaInicio, fechaFin)
- findByClientId(clientId)
```

#### DTOs y Validaciones

**AccountRequestDto**:
- `accountNumber`: @NotBlank, @Pattern("^\d+$") (solo números)
- `accountType`: @Pattern("^(Ahorro|Corriente)$")
- `initialBalance`: @DecimalMin("0.0")
- `clientId`: @Positive

**MovementRequestDto**:
- `accountNumber`: @NotBlank
- `type`: @Pattern("^(Deposito|Retiro)$")
- `amount`: @DecimalMin("0.01")

### 💙 Capa de Infraestructura (Adaptadores)

#### REST Controllers

**AccountController** (`/api/accounts`):
```java
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
        @Valid @RequestBody AccountRequestDto dto
    ) {
        return ResponseEntity.status(201)
            .body(accountService.createAccount(dto));
    }
    
    @PostMapping("/{number}/deposit")
    public ResponseEntity<AccountResponseDto> deposit(
        @PathVariable String number,
        @RequestBody BigDecimal amount
    ) {
        // ...
    }
    
    @PostMapping("/{number}/withdraw")
    public ResponseEntity<AccountResponseDto> withdraw(
        @PathVariable String number,
        @RequestBody BigDecimal amount
    ) {
        // F3 - Puede lanzar InsufficientBalanceException
    }
}
```

**MovementController** (`/api/movements`):
- Endpoints para crear, consultar, listar, eliminar movimientos
- Endpoints de reportes (F4)

#### Persistencia

**AccountJpaEntity**:
```java
@Entity
@Table(name = "accounts", schema = "account")
public class AccountJpaEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;
    
    @Column(name = "account_type", nullable = false)
    private String accountType;
    
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @Column(name = "active", nullable = false)
    private boolean active;
    
    @Column(name = "client_id", nullable = false)
    private Long clientId;
}
```

**AccountRepositoryImpl** (implementación del Port):
```java
@Repository
public class AccountRepositoryImpl implements AccountRepository {
    
    private final AccountJpaRepository jpaRepository;
    private final AccountJpaMapper mapper;
    
    @Override
    public Account save(Account account) {
        // Domain → JPA Entity
        AccountJpaEntity jpaEntity = mapper.toJpaEntity(account);
        // Save
        AccountJpaEntity saved = jpaRepository.save(jpaEntity);
        // JPA Entity → Domain
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Account> findByAccountNumber(String number) {
        return jpaRepository.findByAccountNumber(number)
            .map(mapper::toDomain);
    }
}
```

#### Manejo Global de Excepciones

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(InsufficientBalanceException.class)  // F3
    public ResponseEntity<Map<String, Object>> handle(
        InsufficientBalanceException ex
    ) {
        return ResponseEntity.badRequest()
            .body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage()  // "Saldo no disponible"
            ));
    }
    
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handle(
        AccountNotFoundException ex
    ) {
        return ResponseEntity.status(404)
            .body(Map.of(
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
            ));
    }
}
```

## Flujos de Negocio Detallados

### F2 - Depósito Exitoso

```
1. Request: POST /api/movements/478758/deposit
   Body: 600.00
   
2. Controller recibe y valida
   
3. Application Service:
   a. Busca cuenta "478758" en repositorio
      └─ RepositoryImpl → JPA → PostgreSQL
   b. Valida que exista
      └─ Si no existe: throw AccountNotFoundException
      └─ Controller → HTTP 404
   c. Valida que esté activa
      └─ Si inactiva: throw InvalidAccountStateException
      └─ Controller → HTTP 400
   d. Cuenta.deposit(600.00)
      └─ Ejecuta lógica de dominio
      └─ Saldo: 2000 → 2600
   e. Crea Movement.createDeposit("478758", 600.00, 2600.00)
      └─ Genera ID, fecha actual, tipo "Deposito"
      └─ Amount: +600.00, Balance: 2600.00
   f. Guarda cuenta y movimiento
      └─ RepositoryImpl → JPA → PostgreSQL
   
4. Mapper: Movement → MovementResponseDto
   
5. Controller: HTTP 201 Created
   Response: {
     "id": 1,
     "accountNumber": "478758",
     "date": "2024-01-15T10:30:00",
     "type": "Deposito",
     "amount": 600.00,
     "balance": 2600.00
   }
```

### F3 - Retiro Rechazado (Saldo Insuficiente)

```
1. Request: POST /api/movements/495878/withdraw
   Body: 100.00
   
2. Application Service busca cuenta "495878"
   └─ Saldo actual: 0.00
   
3. Cuenta.withdraw(100.00)
   └─ Valida: balance (0.00) < amount (100.00)
   └─ throw new InsufficientBalanceException("Saldo no disponible")
   
4. GlobalExceptionHandler captura
   
5. Controller: HTTP 400 Bad Request
   Response: {
     "timestamp": "2024-01-15T10:30:00",
     "status": 400,
     "error": "Bad Request",
     "message": "Saldo no disponible"
   }
   
6. Saldo permanece en 0.00 (no se modificó)
```

### F4 - Reporte por Rango de Fechas

```
1. Request: GET /api/movements/report
   Params: fechaInicio=2022-02-01, fechaFin=2022-02-15
   
2. Application Service:
   a. Parsea fechas: String → LocalDate
   b. Calcula rango:
      fechaInicio = 2022-02-01T00:00:00
      fechaFin = 2022-02-15T23:59:59
   c. Consulta repositorio:
      findByDateBetween(start, end)
      └─ RepositoryImpl → JPA → PostgreSQL
   d. Mapea resultados a DTOs
   
3. Controller: HTTP 200 OK
   Response: [
     { "id": 1, "accountNumber": "225487", "date": "2022-02-10", ... },
     { "id": 2, "accountNumber": "225487", "date": "2022-02-12", ... }
   ]
```

## Tests

### Tests Unitarios (44 en Dominio)

| Entidad | Tests | Cobertura |
|---------|-------|-----------|
| **Account** | ~25 | Constructor, deposit(), withdraw(), validaciones |
| **Movement** | ~20 | Factory methods, validaciones, edge cases |
| **Application Service** | ~30 | Casos de uso, orquestación |
| **Total** | ~74 | 80%+ dominio |

**Ejemplo de Test F3**:
```java
@Test
@DisplayName("F3: Should throw InsufficientBalanceException")
void shouldThrowWhenWithdrawingMoreThanBalance() {
    // Given: Cuenta con saldo 2000
    Account account = new Account("478758", "Ahorro", 
                                   new BigDecimal("2000"), true, 1L);
    
    // When & Then: Intentar retirar 2500
    InsufficientBalanceException exception = assertThrows(
        InsufficientBalanceException.class,
        () -> account.withdraw(new BigDecimal("2500"))
    );
    
    // Assert: Mensaje exacto del requisito F3
    assertEquals("Saldo no disponible", exception.getMessage());
    
    // Saldo no cambió
    assertEquals(new BigDecimal("2000"), account.getBalance());
}
```

### Tests Karate (60+ escenarios)

| Feature | Escenarios | Requisito |
|---------|------------|-----------|
| `account-crud.feature` | ~20 | F1 (Cuentas) |
| `movement-crud.feature` | ~20 | F2, F3, F4 |

**Escenario F3 en Karate**:
```gherkin
@f3 @withdrawal @negative
Scenario: F3: Retiro rechazado por saldo insuficiente
  * def testAccount = generateAccount({ initialBalance: 0.00 })
  * call read('account-create-helper.feature') testAccount
  * def accountNumber = testAccount.accountNumber
  
  Given path '/api/movements/' + accountNumber + '/withdraw'
  And request 100.00
  When method post
  Then status 400
  And match response.message == 'Saldo no disponible'
```

## Docker

```dockerfile
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
EXPOSE 8082
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

## Dependencias

| Servicio | Uso |
|----------|-----|
| **Eureka** | Registro y descubrimiento |
| **PostgreSQL** | Persistencia |
| **RabbitMQ** | Consumo de eventos (futuro) |
| **Gateway** | Enrutamiento |
| **ms-customer** | Validación de clientes |

## Puertos

| Puerto | Uso |
|--------|-----|
| `8082` | API REST, Swagger, Actuator |

## Accesos

| Recurso | URL |
|---------|-----|
| API Cuentas | http://localhost:8082/api/accounts |
| API Movimientos | http://localhost:8082/api/movements |
| Swagger UI | http://localhost:8082/swagger-ui.html |
| Health | http://localhost:8082/actuator/health |

## Valor Agregado

### Para el Negocio

- **F2**: Historial completo de transacciones para auditoría
- **F3**: Previene sobregiros, protege al banco
- **F4**: Reportes para compliance y análisis

### Para Desarrolladores

- **Dominio Puro**: Fácil testear, refactorizar
- **Flexibilidad**: Cambiar PostgreSQL por MongoDB sin tocar dominio
- **Escalabilidad**: Microservicio independiente

## Referencias

- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/)
- [MapStruct](https://mapstruct.org/)
