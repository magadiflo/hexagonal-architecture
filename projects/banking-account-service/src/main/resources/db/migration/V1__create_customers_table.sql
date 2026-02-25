-- V1: Creación de la tabla customers
CREATE TABLE customers(
    id              BIGSERIAL    NOT NULL,  --Clave interna(PK): usado para joins, FK en otras tablas, índices, etc.
    customer_code   VARCHAR(20)  NOT NULL,  --Referencia de negocio, la que viaja en la API, URLs, respuestas JSON
    document_number VARCHAR(20)  NOT NULL,
    document_type   VARCHAR(10)  NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now(),

    CONSTRAINT pk_customers PRIMARY KEY (id),
    CONSTRAINT uq_customers_customer_code UNIQUE (customer_code),
    CONSTRAINT uq_customers_document_number UNIQUE (document_number),
    CONSTRAINT uq_customers_email UNIQUE (email),
    CONSTRAINT chk_customers_document_type CHECK (document_type IN ('DNI', 'RUC', 'PASAPORTE')),
    CONSTRAINT chk_customers_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED'))
);

-- Índices para búsquedas frecuentes
-- Nota: document_number y email ya tienen índices por sus restricciones UNIQUE
CREATE INDEX idx_customers_status ON customers(status);

COMMENT ON TABLE customers IS 'Tabla de clientes del banco';
COMMENT ON COLUMN customers.id IS 'Primary Key: usado para joins, FK, índices, etc.';
COMMENT ON COLUMN customers.customer_code IS 'Referencia de negocio, la que viaja en la API, URLs, respuestas JSON';
COMMENT ON COLUMN customers.document_number IS 'Número de documento de identidad';
COMMENT ON COLUMN customers.document_type IS 'Tipo de documento: DNI, RUC, PASAPORTE';
COMMENT ON COLUMN customers.status IS 'Estado del cliente: ACTIVE, INACTIVE, BLOCKED';
