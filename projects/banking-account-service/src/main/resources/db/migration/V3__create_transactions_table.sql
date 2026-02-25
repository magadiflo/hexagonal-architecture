-- V3: Creación de la tabla transactions
CREATE TABLE transactions(
    id               BIGSERIAL      NOT NULL, --Clave interna(PK): usado para joins, FK en otras tablas, índices, etc.
    reference_number VARCHAR(50)    NOT NULL, --Referencia de negocio, la que viaja en la API, URLs, respuestas JSON
    account_id       BIGINT         NOT NULL, --FK hacia accounts.id
    transaction_type VARCHAR(20)    NOT NULL,
    amount           DECIMAL(19, 2) NOT NULL,
    currency         VARCHAR(3)     NOT NULL,
    description      VARCHAR(255),
    status           VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMP      NOT NULL DEFAULT now(),

    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT uq_transactions_reference_number UNIQUE (reference_number),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id)
        REFERENCES accounts(id) ON DELETE RESTRICT,
    CONSTRAINT chk_transactions_amount CHECK (amount > 0),
    CONSTRAINT chk_transactions_type CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT')),
    CONSTRAINT chk_transactions_currency CHECK (currency IN ('PEN', 'USD', 'EUR')),
    CONSTRAINT chk_transactions_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED'))
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_transactions_account_id ON transactions(account_id); -- Creamos índice de account_id, porque el FK no crea índice automático
CREATE INDEX idx_transactions_type ON transactions (transaction_type);
CREATE INDEX idx_transactions_status ON transactions (status);
CREATE INDEX idx_transactions_created_at ON transactions (created_at DESC);

COMMENT ON TABLE transactions IS 'Tabla de movimientos/transacciones bancarias';
COMMENT ON COLUMN transactions.id IS 'Primary Key: usado para joins, FK, índices, etc.';
COMMENT ON COLUMN transactions.reference_number IS 'Referencia de negocio, la que viaja en la API, URLs, respuestas JSON';
COMMENT ON COLUMN transactions.account_id IS 'Cuenta bancaria asociada a la transacción (FK hacia accounts.id)';
COMMENT ON COLUMN transactions.transaction_type IS 'Tipo: DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT';
COMMENT ON COLUMN transactions.amount IS 'Monto de la operación, siempre positivo';
COMMENT ON COLUMN transactions.status IS 'Estado: PENDING, COMPLETED, FAILED';
