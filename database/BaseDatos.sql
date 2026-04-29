-- ============================================================================
-- BaseDatos.sql — Prueba Técnica: Arquitectura Microservicio (2023)
-- ============================================================================
-- Microservicios: ms-customer (schema: customer) | ms-account (schema: account)
-- Base de datos: PostgreSQL
-- 
-- Ejecución:
--   psql -U postgres -d bankdb -f BaseDatos.sql
--
-- Tablas:
--   customer.clients  → Persona + Cliente (herencia tabla única)
--   account.accounts  → Cuenta
--   account.movements → Movimiento
-- ============================================================================


-- ============================================================================
-- CREAR BASE DE DATOS (si no existe)
-- ============================================================================
SELECT 'CREATE DATABASE bankdb'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'bankdb')\gexec


-- ============================================================================
-- SCHEMAS
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS customer;
CREATE SCHEMA IF NOT EXISTS account;


-- ============================================================================
-- SCHEMA: customer
-- ============================================================================

-- Sequence para PK de clientes (PersonJpaEntity usa SEQUENCE)
CREATE SEQUENCE IF NOT EXISTS customer.person_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ----------------------------------------------------------------------------
-- Tabla: customer.clients
-- Implementa herencia tabla única (SINGLE_TABLE):
--   Persona: id, name, gender, age, identification, address, phone
--   Cliente: password, active
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customer.clients (
    -- Clave primaria (PK)
    id              BIGINT          NOT NULL DEFAULT nextval('customer.person_sequence'),

    -- Campos de Persona
    name            VARCHAR(100)    NOT NULL,
    gender          VARCHAR(20),
    age             INTEGER         NOT NULL,
    identification  VARCHAR(20)     NOT NULL,
    address         VARCHAR(255),
    phone           VARCHAR(20),

    -- Campos de Cliente
    password        VARCHAR(50)     NOT NULL,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,

    -- Constraints
    CONSTRAINT pk_clients               PRIMARY KEY (id),
    CONSTRAINT uq_clients_identification UNIQUE (identification)
);

COMMENT ON TABLE  customer.clients                IS 'Entidad Cliente que hereda de Persona. Un cliente tiene clienteid, contraseña y estado.';
COMMENT ON COLUMN customer.clients.id             IS 'Clave primaria única del cliente (clienteid).';
COMMENT ON COLUMN customer.clients.identification IS 'Clave única de identificación de la persona (cédula/pasaporte).';
COMMENT ON COLUMN customer.clients.password       IS 'Contraseña de acceso del cliente.';
COMMENT ON COLUMN customer.clients.active         IS 'Estado del cliente: true=activo, false=inactivo.';


-- ============================================================================
-- SCHEMA: account
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Tabla: account.accounts
-- Entidad Cuenta: número cuenta, tipo, saldo inicial, estado
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS account.accounts (
    -- Clave primaria (PK)
    id              BIGINT          NOT NULL GENERATED ALWAYS AS IDENTITY,

    -- Campos de Cuenta
    account_number  VARCHAR(20)     NOT NULL,
    account_type    VARCHAR(20)     NOT NULL,
    balance         NUMERIC(15, 2)  NOT NULL DEFAULT 0.00,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,

    -- Referencia al cliente dueño de la cuenta (cross-schema, sin FK real)
    client_id       BIGINT          NOT NULL,

    -- Constraints
    CONSTRAINT pk_accounts              PRIMARY KEY (id),
    CONSTRAINT uq_accounts_number       UNIQUE (account_number),
    CONSTRAINT chk_accounts_balance     CHECK (balance >= 0),
    CONSTRAINT chk_accounts_type        CHECK (account_type IN ('Ahorro', 'Corriente'))
);

COMMENT ON TABLE  account.accounts                IS 'Entidad Cuenta bancaria. Contiene número, tipo, saldo disponible y estado.';
COMMENT ON COLUMN account.accounts.account_number IS 'Número de cuenta único (ej: 478758).';
COMMENT ON COLUMN account.accounts.account_type   IS 'Tipo de cuenta: Ahorro o Corriente.';
COMMENT ON COLUMN account.accounts.balance        IS 'Saldo disponible actual de la cuenta.';
COMMENT ON COLUMN account.accounts.active         IS 'Estado de la cuenta: true=activa, false=inactiva.';
COMMENT ON COLUMN account.accounts.client_id      IS 'ID del cliente propietario (referencia a customer.clients.id).';

-- Índice para búsquedas por cliente (reportes F4)
CREATE INDEX IF NOT EXISTS idx_accounts_client_id
    ON account.accounts (client_id);


-- ----------------------------------------------------------------------------
-- Tabla: account.movements
-- Entidad Movimiento: fecha, tipo, valor, saldo
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS account.movements (
    -- Clave primaria (PK)
    id              BIGINT          NOT NULL GENERATED ALWAYS AS IDENTITY,

    -- Referencia a la cuenta
    account_number  VARCHAR(20)     NOT NULL,

    -- Campos de Movimiento
    date            TIMESTAMP       NOT NULL DEFAULT NOW(),
    type            VARCHAR(20)     NOT NULL,
    amount          NUMERIC(15, 2)  NOT NULL,
    balance         NUMERIC(15, 2)  NOT NULL,

    -- Constraints
    CONSTRAINT pk_movements             PRIMARY KEY (id),
    CONSTRAINT chk_movements_type       CHECK (type IN ('Deposito', 'Retiro')),
    CONSTRAINT chk_movements_balance    CHECK (balance >= 0),
    CONSTRAINT chk_movements_amount     CHECK (amount != 0)
);

COMMENT ON TABLE  account.movements               IS 'Registro histórico de transacciones bancarias (F2). Depósitos y retiros.';
COMMENT ON COLUMN account.movements.account_number IS 'Número de cuenta asociada al movimiento.';
COMMENT ON COLUMN account.movements.date          IS 'Fecha y hora del movimiento (asignada automáticamente).';
COMMENT ON COLUMN account.movements.type          IS 'Tipo de movimiento: Deposito (monto positivo) o Retiro (monto negativo).';
COMMENT ON COLUMN account.movements.amount        IS 'Valor del movimiento. Positivo para depósitos, negativo para retiros.';
COMMENT ON COLUMN account.movements.balance       IS 'Saldo disponible de la cuenta después del movimiento.';

-- Índice para búsquedas por cuenta (F2 — historial de cuenta)
CREATE INDEX IF NOT EXISTS idx_movements_account_number
    ON account.movements (account_number);

-- Índice para búsquedas por fecha (F4 — reportes por rango)
CREATE INDEX IF NOT EXISTS idx_movements_date
    ON account.movements (date);

-- Índice compuesto para reportes por cuenta y fecha (F4)
CREATE INDEX IF NOT EXISTS idx_movements_account_date
    ON account.movements (account_number, date);


-- ============================================================================
-- DATOS DE PRUEBA — Casos de Uso del Enunciado
-- ============================================================================

-- Clientes (Caso de Uso 1)
INSERT INTO customer.clients (name, gender, age, identification, address, phone, password, active)
VALUES
    ('Jose Lema',          'Masculino', 35, '1234567890', 'Otavalo sn y principal',     '098254785', '1234', TRUE),
    ('Marianela Montalvo', 'Femenino',  28, '0987654321', 'Amazonas y NNUU',             '097548965', '5678', TRUE),
    ('Juan Osorio',        'Masculino', 42, '1122334455', '13 junio y Equinoccial',      '098874587', '1245', TRUE)
ON CONFLICT (identification) DO NOTHING;

-- Cuentas (Caso de Uso 2 + 3)
-- Nota: client_id corresponde a los IDs generados por la sequence (1, 2, 3)
INSERT INTO account.accounts (account_number, account_type, balance, active, client_id)
VALUES
    ('478758', 'Ahorro',    2000.00, TRUE, 1),  -- Jose Lema
    ('225487', 'Corriente',  100.00, TRUE, 2),  -- Marianela Montalvo
    ('495878', 'Ahorro',       0.00, TRUE, 3),  -- Juan Osorio
    ('496825', 'Ahorro',     540.00, TRUE, 2),  -- Marianela Montalvo
    ('585545', 'Corriente', 1000.00, TRUE, 1)   -- Jose Lema (Caso de Uso 3)
ON CONFLICT (account_number) DO NOTHING;

-- Movimientos iniciales (Caso de Uso 4 — retiro de 575 en cuenta 478758)
INSERT INTO account.movements (account_number, date, type, amount, balance)
VALUES
    ('478758', NOW(), 'Retiro', -575.00, 1425.00)  -- Retiro 575 → saldo 2000 - 575 = 1425
ON CONFLICT DO NOTHING;


-- ============================================================================
-- VERIFICACIÓN
-- ============================================================================
SELECT 'customer.clients'  AS tabla, COUNT(*) AS registros FROM customer.clients
UNION ALL
SELECT 'account.accounts'  AS tabla, COUNT(*) AS registros FROM account.accounts
UNION ALL
SELECT 'account.movements' AS tabla, COUNT(*) AS registros FROM account.movements;
