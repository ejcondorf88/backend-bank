CREATE SCHEMA IF NOT EXISTS customer;
CREATE SCHEMA IF NOT EXISTS account;

CREATE SEQUENCE IF NOT EXISTS customer.person_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS customer.persons (
    id BIGINT NOT NULL DEFAULT nextval('customer.person_sequence'),
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    age INTEGER NOT NULL,
    identification VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    CONSTRAINT pk_persons PRIMARY KEY (id),
    CONSTRAINT uq_persons_identification UNIQUE (identification),
    CONSTRAINT chk_persons_age CHECK (age >= 0 AND age <= 150)
);

CREATE TABLE IF NOT EXISTS customer.clients (
    id BIGINT NOT NULL,
    password VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_clients PRIMARY KEY (id),
    CONSTRAINT fk_clients_person FOREIGN KEY (id) REFERENCES customer.persons(id) ON DELETE CASCADE,
    CONSTRAINT chk_clients_password CHECK (char_length(password) >= 4)
);

CREATE TABLE IF NOT EXISTS account.accounts (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    account_number VARCHAR(20) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    client_id BIGINT NOT NULL,
    CONSTRAINT pk_accounts PRIMARY KEY (id),
    CONSTRAINT uq_accounts_number UNIQUE (account_number),
    CONSTRAINT chk_accounts_balance CHECK (balance >= 0),
    CONSTRAINT chk_accounts_type CHECK (account_type IN ('Ahorro', 'Corriente'))
);

CREATE INDEX IF NOT EXISTS idx_accounts_client_id
    ON account.accounts (client_id);

CREATE TABLE IF NOT EXISTS account.movements (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    account_number VARCHAR(20) NOT NULL,
    date TIMESTAMP NOT NULL DEFAULT NOW(),
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    balance NUMERIC(15, 2) NOT NULL,
    CONSTRAINT pk_movements PRIMARY KEY (id),
    CONSTRAINT chk_movements_type CHECK (type IN ('Deposito', 'Retiro')),
    CONSTRAINT chk_movements_balance CHECK (balance >= 0),
    CONSTRAINT chk_movements_amount CHECK (amount != 0)
);

CREATE INDEX IF NOT EXISTS idx_movements_account_number
    ON account.movements (account_number);

CREATE INDEX IF NOT EXISTS idx_movements_date
    ON account.movements (date);

CREATE INDEX IF NOT EXISTS idx_movements_account_date
    ON account.movements (account_number, date);

INSERT INTO customer.persons (name, gender, age, identification, address, phone)
VALUES
    ('Jose Lema', 'Masculino', 35, '1234567890', 'Otavalo sn y principal', '098254785'),
    ('Marianela Montalvo', 'Femenino', 28, '0987654321', 'Amazonas y NNUU', '097548965'),
    ('Juan Osorio', 'Masculino', 42, '1122334455', '13 junio y Equinoccial', '098874587')
ON CONFLICT (identification) DO NOTHING;

INSERT INTO customer.clients (id, password, active)
VALUES
    (1, '1234', TRUE),
    (2, '5678', TRUE),
    (3, '1245', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO account.accounts (account_number, account_type, balance, active, client_id)
VALUES
    ('478758', 'Ahorro', 2000.00, TRUE, 1),
    ('225487', 'Corriente', 100.00, TRUE, 2),
    ('495878', 'Ahorro', 0.00, TRUE, 3),
    ('496825', 'Ahorro', 540.00, TRUE, 2),
    ('585545', 'Corriente', 1000.00, TRUE, 1)
ON CONFLICT (account_number) DO NOTHING;

INSERT INTO account.movements (account_number, date, type, amount, balance)
VALUES
    ('478758', NOW(), 'Retiro', -575.00, 1425.00)
ON CONFLICT DO NOTHING;

SELECT 'customer.persons' AS tabla, COUNT(*) AS registros FROM customer.persons
UNION ALL
SELECT 'customer.clients' AS tabla, COUNT(*) AS registros FROM customer.clients
UNION ALL
SELECT 'account.accounts' AS tabla, COUNT(*) AS registros FROM account.accounts
UNION ALL
SELECT 'account.movements' AS tabla, COUNT(*) AS registros FROM account.movements
ORDER BY tabla;