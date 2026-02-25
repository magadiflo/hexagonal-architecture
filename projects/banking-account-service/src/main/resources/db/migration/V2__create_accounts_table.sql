-- V2: Creación de la tabla accounts
CREATE TABLE accounts(
    id             BIGSERIAL      NOT NULL, --Clave interna(PK): usado para joins, FK en otras tablas, índices, etc.
    account_number VARCHAR(20)    NOT NULL, --Referencia de negocio, la que viaja en la API, URLs, respuestas JSON
    customer_id    BIGINT         NOT NULL, --FK hacia customers.id
    account_type   VARCHAR(20)    NOT NULL,
    balance        DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    currency       VARCHAR(3)     NOT NULL DEFAULT 'PEN',
    status         VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP      NOT NULL DEFAULT now(),

    CONSTRAINT pk_accounts PRIMARY KEY (id),                        -- PK crea índice único automáticamente
    CONSTRAINT uq_accounts_account_number UNIQUE (account_number),  -- UNIQUE crea índice único automáticamente

    -- ON DELETE RESTRICT: Impide borrar un cliente si existen cuentas asociadas que lo referencian.
    -- Garantiza la integridad referencial.
    -- Nota: es muy parecido a NO ACTION (que es el valor por defecto), pero la diferencia es que RESTRICT
    -- valida inmediatamente la restricción en el momento de ejecutar la sentencia, mientras que NO ACTION
    -- espera hasta el final de la transacción para comprobarla.
    CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id)
        REFERENCES customers(id) ON DELETE RESTRICT,

    CONSTRAINT chk_accounts_balance CHECK (balance >= 0),
    CONSTRAINT chk_accounts_account_type CHECK (account_type IN ('SAVINGS', 'CHECKING')),
    CONSTRAINT chk_accounts_currency CHECK (currency IN ('PEN', 'USD', 'EUR')),
    CONSTRAINT chk_accounts_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'CLOSED'))
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_accounts_customer_id ON accounts(customer_id); -- FK no crea índice automático
CREATE INDEX idx_accounts_status ON accounts(status);           -- útil para filtrar cuentas por estado

COMMENT ON TABLE accounts IS 'Tabla de cuentas bancarias';
COMMENT ON COLUMN accounts.id IS 'Primary Key: usado para joins, FK, índices, etc.';
COMMENT ON COLUMN accounts.account_number IS 'Referencia de negocio, la que viaja en la API, URLs, respuestas JSON';
COMMENT ON COLUMN accounts.customer_id IS 'Cliente propietario de la cuenta (FK hacia customers.id)';
COMMENT ON COLUMN accounts.account_type IS 'Tipo de cuenta: SAVINGS (ahorros), CHECKING (corriente)';
COMMENT ON COLUMN accounts.balance IS 'Saldo actual de la cuenta con 2 decimales';
COMMENT ON COLUMN accounts.currency IS 'Moneda de la cuenta: PEN, USD, EUR';
COMMENT ON COLUMN accounts.status IS 'Estado: ACTIVE, INACTIVE, BLOCKED, CLOSED';
