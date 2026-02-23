# 🏦 Definición del Proyecto

## Sistema de Gestión de Cuentas Bancarias

---

## 1. 🏛️ Descripción del Sistema

`Descripción`: Microservicio bancario que gestiona clientes y sus cuentas bancarias, permitiendo operaciones como
`apertura de cuentas`, `depósitos`, `retiros` y `consulta de movimientos`. El sistema integra una API externa para
obtener tasas de cambio de moneda en tiempo real.

`Contexto empresarial`: Este servicio forma parte del core bancario de una entidad financiera. Es el responsable de
mantener la integridad del saldo de las cuentas y garantizar que todas las operaciones cumplan las reglas de negocio
definidas por el banco.

## 2. ⚙️ Tecnologías y Dependencias

| Tecnología        | Versión                  | Uso                           |
|-------------------|--------------------------|-------------------------------|
| Java              | 25                       | Lenguaje principal            |
| Spring Boot       | 4.0.3                    | Framework principal           |
| Spring Web        | 4.0.3                    | Controladores REST            |
| Spring Data JPA   | 4.0.3                    | Persistencia                  |
| PostgreSQL        | 17-alpine                | Base de datos                 |
| Flyway            | Latest compatible        | Migraciones de BD             |
| RestClient        | Spring Boot 4 (built-in) | Cliente HTTP para API externa |
| MapStruct         | 1.6.x                    | Mappers entre capas           |
| Lombok            | 1.18.x                   | Reducción de boilerplate      |
| Bean Validation   | Jakarta 3.x              | Validaciones en requests      |
| JUnit 5 + Mockito | Latest                   | Testing                       |

## 3. 🗄️ Modelo de Datos

Trabajaremos con **3 tablas** relacionadas entre sí:

### Diagrama de Relaciones

```
┌─────────────────┐         ┌──────────────────────┐         ┌──────────────────────────┐
│    customers    │         │       accounts       │         │       transactions       │
├─────────────────┤         ├──────────────────────┤         ├──────────────────────────┤
│ id (PK)  UUID   │◄──┐     │ id (PK)  UUID        │◄──┐     │ id (PK)  UUID            │
│ document_number │   └─────│ customer_id (FK)     │   └─────│ account_id (FK)          │
│ document_type   │         │ account_number       │         │ transaction_type         │
│ first_name      │         │ account_type         │         │ amount                   │
│ last_name       │         │ balance              │         │ currency                 │
│ email           │         │ currency             │         │ description              │
│ phone           │         │ status               │         │ status                   │
│ status          │         │ created_at           │         │ reference_number         │
│ created_at      │         │ updated_at           │         │ created_at               │
│ updated_at      │         └──────────────────────┘         └──────────────────────────┘
└─────────────────┘
```

Relaciones:

- Un `CUSTOMER` puede tener muchas `ACCOUNTS (1:N)`
- Una `ACCOUNT` puede tener muchos `TRANSACTIONS(1:N)`

### Tabla `customers`

| Columna           | Tipo           | Restricciones              | Descripción                   |
|-------------------|----------------|----------------------------|-------------------------------|
| `id`              | `UUID`         | PK, NOT NULL               | Identificador único           |
| `document_number` | `VARCHAR(20)`  | UNIQUE, NOT NULL           | Número de documento           |
| `document_type`   | `VARCHAR(10)`  | NOT NULL                   | DNI, RUC, PASAPORTE           |
| `first_name`      | `VARCHAR(100)` | NOT NULL                   | Nombres                       |
| `last_name`       | `VARCHAR(100)` | NOT NULL                   | Apellidos                     |
| `email`           | `VARCHAR(150)` | UNIQUE, NOT NULL           | Correo electrónico            |
| `phone`           | `VARCHAR(20)`  | NOT NULL                   | Teléfono                      |
| `status`          | `VARCHAR(20)`  | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE, INACTIVE, BLOCKED     |
| `created_at`      | `TIMESTAMP`    | NOT NULL                   | Fecha de creación             |
| `updated_at`      | `TIMESTAMP`    | NOT NULL                   | Fecha de última actualización |

---

### Tabla `accounts`

| Columna          | Tipo            | Restricciones               | Descripción                       |
|------------------|-----------------|-----------------------------|-----------------------------------|
| `id`             | `UUID`          | PK, NOT NULL                | Identificador único               |
| `customer_id`    | `UUID`          | FK → customers.id, NOT NULL | Cliente propietario               |
| `account_number` | `VARCHAR(20)`   | UNIQUE, NOT NULL            | Número de cuenta generado         |
| `account_type`   | `VARCHAR(20)`   | NOT NULL                    | SAVINGS, CHECKING                 |
| `balance`        | `DECIMAL(19,4)` | NOT NULL, DEFAULT 0         | Saldo actual                      |
| `currency`       | `VARCHAR(3)`    | NOT NULL                    | PEN, USD, EUR                     |
| `status`         | `VARCHAR(20)`   | NOT NULL, DEFAULT 'ACTIVE'  | ACTIVE, INACTIVE, BLOCKED, CLOSED |
| `created_at`     | `TIMESTAMP`     | NOT NULL                    | Fecha de creación                 |
| `updated_at`     | `TIMESTAMP`     | NOT NULL                    | Fecha de última actualización     |

---

### Tabla `transactions`

| Columna            | Tipo            | Restricciones              | Descripción                                    |
|--------------------|-----------------|----------------------------|------------------------------------------------|
| `id`               | `UUID`          | PK, NOT NULL               | Identificador único                            |
| `account_id`       | `UUID`          | FK → accounts.id, NOT NULL | Cuenta asociada                                |
| `transaction_type` | `VARCHAR(20)`   | NOT NULL                   | DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT |
| `amount`           | `DECIMAL(19,4)` | NOT NULL                   | Monto de la operación                          |
| `currency`         | `VARCHAR(3)`    | NOT NULL                   | Moneda de la transacción                       |
| `description`      | `VARCHAR(255)`  | NULLABLE                   | Descripción del movimiento                     |
| `status`           | `VARCHAR(20)`   | NOT NULL                   | COMPLETED, FAILED, PENDING                     |
| `reference_number` | `VARCHAR(50)`   | UNIQUE, NOT NULL           | Número de referencia único                     |
| `created_at`       | `TIMESTAMP`     | NOT NULL                   | Fecha de la transacción                        |

## 4. 🎯 Casos de Uso

### 👤 Customer

| # | Caso de Uso              | Método | Endpoint                 | Descripción                      |
|---|--------------------------|--------|--------------------------|----------------------------------|
| 1 | `CreateCustomerUseCase`  | POST   | `/api/v1/customers`      | Registrar nuevo cliente          |
| 2 | `GetCustomerByIdUseCase` | GET    | `/api/v1/customers/{id}` | Obtener cliente por ID           |
| 3 | `GetAllCustomersUseCase` | GET    | `/api/v1/customers`      | Listar todos los clientes        |
| 4 | `UpdateCustomerUseCase`  | PUT    | `/api/v1/customers/{id}` | Actualizar datos del cliente     |
| 5 | `DeleteCustomerUseCase`  | DELETE | `/api/v1/customers/{id}` | Desactivar cliente (baja lógica) |

### 🏦 Account

| #  | Caso de Uso                    | Método | Endpoint                          | Descripción                                |
|----|--------------------------------|--------|-----------------------------------|--------------------------------------------|
| 6  | `OpenAccountUseCase`           | POST   | `/api/v1/accounts`                | Abrir nueva cuenta bancaria                |
| 7  | `GetAccountByIdUseCase`        | GET    | `/api/v1/accounts/{id}`           | Consultar cuenta por ID                    |
| 8  | `GetAccountsByCustomerUseCase` | GET    | `/api/v1/customers/{id}/accounts` | Cuentas de un cliente                      |
| 9  | `DepositUseCase`               | POST   | `/api/v1/accounts/{id}/deposit`   | Realizar un depósito                       |
| 10 | `WithdrawUseCase`              | POST   | `/api/v1/accounts/{id}/withdraw`  | Realizar un retiro                         |
| 11 | `GetAccountBalanceUseCase`     | GET    | `/api/v1/accounts/{id}/balance`   | Saldo + equivalente en USD vía API externa |
| 12 | `BlockAccountUseCase`          | PATCH  | `/api/v1/accounts/{id}/block`     | Bloquear una cuenta                        |

### 📋 Transaction

| #  | Caso de Uso                       | Método | Endpoint                             | Descripción              |
|----|-----------------------------------|--------|--------------------------------------|--------------------------|
| 13 | `GetTransactionsByAccountUseCase` | GET    | `/api/v1/accounts/{id}/transactions` | Historial de movimientos |
| 14 | `GetTransactionByIdUseCase`       | GET    | `/api/v1/transactions/{id}`          | Detalle de un movimiento |

## 5. 📏 Reglas de Negocio

> Estas reglas viven en la **capa de Dominio** y son invariantes del negocio. Ninguna capa externa puede saltárselas.

### Reglas de Cliente

- El `document_number` debe ser único en el sistema.
- El `email` debe ser único en el sistema.
- Un cliente con estado `BLOCKED` no puede abrir nuevas cuentas.
- Un cliente con estado `INACTIVE` no puede realizar operaciones.
- La eliminación es **lógica** (cambia estado a `INACTIVE`), nunca física.

### Reglas de Cuenta

- Un cliente puede tener **máximo 3 cuentas activas** simultáneamente.
- El saldo inicial al abrir una cuenta debe ser **mayor a 0**.
- Una cuenta `BLOCKED` o `CLOSED` **no puede recibir ni enviar** dinero.
- Una cuenta `CLOSED` **no puede ser reactivada** bajo ningún motivo.
- El número de cuenta se genera automáticamente: formato `BNK` + timestamp + random.
- **No se permite saldo negativo** en ninguna circunstancia.
- El monto de cualquier operación debe ser **estrictamente mayor a 0**.

### Reglas de Transacción

- Toda operación exitosa genera un registro con estado `COMPLETED`.
- Si una operación falla por regla de negocio, se registra con estado `FAILED`.
- El `reference_number` es único y generado automáticamente.
- Las transacciones son **inmutables**: no se editan ni eliminan jamás.

## 6. 🌐 Cliente HTTP — ExchangeRate API

Usaremos `RestClient` (Spring Boot 4 built-in) para consultar tasas de cambio en tiempo real.

**¿Para qué?** En `GetAccountBalanceUseCase`, al consultar el saldo de una cuenta en PEN o EUR, mostraremos también el
**equivalente en USD** usando la tasa de cambio actual.

### Endpoint consumido

```
GET https://v6.exchangerate-api.com/v6/{api_key}/pair/{base}/{target}

Ejemplo:
GET https://v6.exchangerate-api.com/v6/YOUR_KEY/pair/PEN/USD

Respuesta:
{
  "result": "success",
  "base_code": "PEN",
  "target_code": "USD",
  "conversion_rate": 0.2671
}
```

### Puerto de Salida (capa Aplicación — solo interfaz)

```java
public interface ExchangeRatePort {
    BigDecimal getExchangeRate(String baseCurrency, String targetCurrency);
}
```

### Adaptador de Salida (capa Infraestructura — implementación con RestClient)

```java

@Component
public class ExchangeRateRestClientAdapter implements ExchangeRatePort {
    private final RestClient restClient;

    @Override
    public BigDecimal getExchangeRate(String baseCurrency, String targetCurrency) {
        // Llama a la API externa con RestClient y retorna la tasa
    }
}
```

## 7. 🚨 Manejo de Errores

### Excepciones de Dominio

| Excepción                          | HTTP | Cuándo se lanza                    |
|------------------------------------|------|------------------------------------|
| `CustomerNotFoundException`        | 404  | Cliente no encontrado              |
| `CustomerAlreadyExistsException`   | 409  | Documento o email duplicado        |
| `CustomerBlockedException`         | 422  | Cliente bloqueado intenta operar   |
| `AccountNotFoundException`         | 404  | Cuenta no encontrada               |
| `AccountBlockedException`          | 422  | Cuenta bloqueada intenta operar    |
| `AccountClosedException`           | 422  | Cuenta cerrada intenta operar      |
| `InsufficientFundsException`       | 422  | Saldo insuficiente para retiro     |
| `MaxAccountsReachedException`      | 422  | Cliente ya tiene 3 cuentas activas |
| `InvalidAmountException`           | 400  | Monto igual o menor a cero         |
| `ExchangeRateUnavailableException` | 502  | Fallo al consultar API externa     |
| `MethodArgumentNotValidException`  | 400  | Validación Bean Validation fallida |
| `Exception` (genérica)             | 500  | Error inesperado del servidor      |

### Formato estándar de error

```json
{
  "timestamp": "2025-10-15T10:30:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Saldo insuficiente para realizar el retiro",
  "path": "/api/v1/accounts/abc-123/withdraw"
}
```

## 8. 📁 Estructura de Directorios

> Aplicamos **Vertical Slicing** — la feature es el eje de organización. Cada módulo de negocio (`customer`, `account`,
`transaction`) contiene sus propias capas completas.

```
banking-account-service/
│
├── pom.xml
│
├── src/
│   │
│   ├── main/
│   │   ├── java/com/bank/accountservice/
│   │   │   │
│   │   │   ├── BankingAccountServiceApplication.java
│   │   │   │
│   │   │   ├── customer/                                      👤 FEATURE: CUSTOMER
│   │   │   │   │
│   │   │   │   ├── domain/                                    🟤 Dominio
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Customer.java                      # Entidad de dominio
│   │   │   │   │   │   ├── vo/
│   │   │   │   │   │   │   ├── CustomerId.java                # Value Object
│   │   │   │   │   │   │   ├── DocumentNumber.java            # Value Object
│   │   │   │   │   │   │   └── Email.java                     # Value Object
│   │   │   │   │   │   └── enums/
│   │   │   │   │   │       ├── CustomerStatus.java
│   │   │   │   │   │       └── DocumentType.java
│   │   │   │   │   └── exception/
│   │   │   │   │       ├── CustomerNotFoundException.java
│   │   │   │   │       ├── CustomerAlreadyExistsException.java
│   │   │   │   │       └── CustomerBlockedException.java
│   │   │   │   │
│   │   │   │   ├── application/                               🔵 Aplicación
│   │   │   │   │   ├── port/
│   │   │   │   │   │   ├── input/
│   │   │   │   │   │   │   ├── CreateCustomerUseCase.java
│   │   │   │   │   │   │   ├── GetCustomerByIdUseCase.java
│   │   │   │   │   │   │   ├── GetAllCustomersUseCase.java
│   │   │   │   │   │   │   ├── UpdateCustomerUseCase.java
│   │   │   │   │   │   │   └── DeleteCustomerUseCase.java
│   │   │   │   │   │   └── output/
│   │   │   │   │   │       └── CustomerRepositoryPort.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── CreateCustomerService.java
│   │   │   │   │   │   ├── GetCustomerByIdService.java
│   │   │   │   │   │   ├── GetAllCustomersService.java
│   │   │   │   │   │   ├── UpdateCustomerService.java
│   │   │   │   │   │   └── DeleteCustomerService.java
│   │   │   │   │   └── dto/
│   │   │   │   │       ├── command/
│   │   │   │   │       │   ├── CreateCustomerCommand.java
│   │   │   │   │       │   └── UpdateCustomerCommand.java
│   │   │   │   │       └── response/
│   │   │   │   │           └── CustomerResponse.java
│   │   │   │   │
│   │   │   │   └── infrastructure/                            🔴 Infraestructura
│   │   │   │       ├── adapter/
│   │   │   │       │   ├── input/
│   │   │   │       │   │   └── rest/
│   │   │   │       │   │       ├── CustomerController.java
│   │   │   │       │   │       └── dto/
│   │   │   │       │   │           ├── CreateCustomerRequest.java
│   │   │   │       │   │           └── UpdateCustomerRequest.java
│   │   │   │       │   └── output/
│   │   │   │       │       └── persistence/
│   │   │   │       │           ├── CustomerJpaAdapter.java    # impl CustomerRepositoryPort
│   │   │   │       │           ├── entity/
│   │   │   │       │           │   └── CustomerEntity.java    # Entidad JPA
│   │   │   │       │           ├── repository/
│   │   │   │       │           │   └── CustomerJpaRepository.java
│   │   │   │       │           └── mapper/
│   │   │   │       │               └── CustomerInfraMapper.java  # MapStruct
│   │   │   │       └── config/                                # (si necesita config propia)
│   │   │   │
│   │   │   ├── account/                                       🏦 FEATURE: ACCOUNT
│   │   │   │   │
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Account.java
│   │   │   │   │   │   ├── vo/
│   │   │   │   │   │   │   ├── AccountId.java
│   │   │   │   │   │   │   └── Money.java
│   │   │   │   │   │   └── enums/
│   │   │   │   │   │       ├── AccountStatus.java
│   │   │   │   │   │       ├── AccountType.java
│   │   │   │   │   │       └── Currency.java
│   │   │   │   │   └── exception/
│   │   │   │   │       ├── AccountNotFoundException.java
│   │   │   │   │       ├── AccountBlockedException.java
│   │   │   │   │       ├── AccountClosedException.java
│   │   │   │   │       ├── InsufficientFundsException.java
│   │   │   │   │       ├── InvalidAmountException.java
│   │   │   │   │       └── MaxAccountsReachedException.java
│   │   │   │   │
│   │   │   │   ├── application/
│   │   │   │   │   ├── port/
│   │   │   │   │   │   ├── input/
│   │   │   │   │   │   │   ├── OpenAccountUseCase.java
│   │   │   │   │   │   │   ├── GetAccountByIdUseCase.java
│   │   │   │   │   │   │   ├── GetAccountsByCustomerUseCase.java
│   │   │   │   │   │   │   ├── DepositUseCase.java
│   │   │   │   │   │   │   ├── WithdrawUseCase.java
│   │   │   │   │   │   │   ├── GetAccountBalanceUseCase.java
│   │   │   │   │   │   │   └── BlockAccountUseCase.java
│   │   │   │   │   │   └── output/
│   │   │   │   │   │       ├── AccountRepositoryPort.java
│   │   │   │   │   │       └── ExchangeRatePort.java          # Puerto hacia API externa
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── OpenAccountService.java
│   │   │   │   │   │   ├── GetAccountByIdService.java
│   │   │   │   │   │   ├── GetAccountsByCustomerService.java
│   │   │   │   │   │   ├── DepositService.java
│   │   │   │   │   │   ├── WithdrawService.java
│   │   │   │   │   │   ├── GetAccountBalanceService.java
│   │   │   │   │   │   └── BlockAccountService.java
│   │   │   │   │   └── dto/
│   │   │   │   │       ├── command/
│   │   │   │   │       │   ├── OpenAccountCommand.java
│   │   │   │   │       │   ├── DepositCommand.java
│   │   │   │   │       │   └── WithdrawCommand.java
│   │   │   │   │       └── response/
│   │   │   │   │           ├── AccountResponse.java
│   │   │   │   │           └── AccountBalanceResponse.java
│   │   │   │   │
│   │   │   │   └── infrastructure/
│   │   │   │       ├── adapter/
│   │   │   │       │   ├── input/
│   │   │   │       │   │   └── rest/
│   │   │   │       │   │       ├── AccountController.java
│   │   │   │       │   │       └── dto/
│   │   │   │       │   │           ├── OpenAccountRequest.java
│   │   │   │       │   │           ├── DepositRequest.java
│   │   │   │       │   │           └── WithdrawRequest.java
│   │   │   │       │   └── output/
│   │   │   │       │       ├── persistence/
│   │   │   │       │       │   ├── AccountJpaAdapter.java     # impl AccountRepositoryPort
│   │   │   │       │       │   ├── entity/
│   │   │   │       │       │   │   └── AccountEntity.java
│   │   │   │       │       │   ├── repository/
│   │   │   │       │       │   │   └── AccountJpaRepository.java
│   │   │   │       │       │   └── mapper/
│   │   │   │       │       │       └── AccountInfraMapper.java
│   │   │   │       │       └── rest/
│   │   │   │       │           ├── ExchangeRateRestClientAdapter.java  # impl ExchangeRatePort
│   │   │   │       │           └── dto/
│   │   │   │       │               └── ExchangeRateApiResponse.java
│   │   │   │       └── config/
│   │   │   │           └── RestClientConfig.java              # Config del RestClient
│   │   │   │
│   │   │   ├── transaction/                                   📋 FEATURE: TRANSACTION
│   │   │   │   │
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Transaction.java
│   │   │   │   │   │   ├── vo/
│   │   │   │   │   │   │   ├── TransactionId.java
│   │   │   │   │   │   │   └── ReferenceNumber.java
│   │   │   │   │   │   └── enums/
│   │   │   │   │   │       ├── TransactionType.java
│   │   │   │   │   │       └── TransactionStatus.java
│   │   │   │   │   └── exception/
│   │   │   │   │       └── TransactionNotFoundException.java
│   │   │   │   │
│   │   │   │   ├── application/
│   │   │   │   │   ├── port/
│   │   │   │   │   │   ├── input/
│   │   │   │   │   │   │   ├── GetTransactionsByAccountUseCase.java
│   │   │   │   │   │   │   └── GetTransactionByIdUseCase.java
│   │   │   │   │   │   └── output/
│   │   │   │   │   │       └── TransactionRepositoryPort.java
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── GetTransactionsByAccountService.java
│   │   │   │   │   │   └── GetTransactionByIdService.java
│   │   │   │   │   └── dto/
│   │   │   │   │       └── response/
│   │   │   │   │           └── TransactionResponse.java
│   │   │   │   │
│   │   │   │   └── infrastructure/
│   │   │   │       └── adapter/
│   │   │   │           ├── input/
│   │   │   │           │   └── rest/
│   │   │   │           │       └── TransactionController.java
│   │   │   │           └── output/
│   │   │   │               └── persistence/
│   │   │   │                   ├── TransactionJpaAdapter.java # impl TransactionRepositoryPort
│   │   │   │                   ├── entity/
│   │   │   │                   │   └── TransactionEntity.java
│   │   │   │                   ├── repository/
│   │   │   │                   │   └── TransactionJpaRepository.java
│   │   │   │                   └── mapper/
│   │   │   │                       └── TransactionInfraMapper.java
│   │   │   │
│   │   │   └── shared/                                        🔧 COMPARTIDO (transversal)
│   │   │       ├── exception/
│   │   │       │   └── GlobalExceptionHandler.java            # Manejo global de errores
│   │   │       └── infrastructure/
│   │   │           └── config/
│   │   │               └── JacksonConfig.java                 # Config serialización JSON
│   │   │
│   │   └── resources/
│   │       ├── application.yml                                # Config principal
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__create_customers_table.sql
│   │               ├── V2__create_accounts_table.sql
│   │               └── V3__create_transactions_table.sql
│   │
│   └── test/
│       └── java/com/bank/accountservice/
│           │
│           ├── customer/
│           │   ├── application/
│           │   │   └── service/
│           │   │       ├── CreateCustomerServiceTest.java     # Test unitario (sin Spring)
│           │   │       └── UpdateCustomerServiceTest.java
│           │   └── infrastructure/
│           │       └── adapter/
│           │           └── rest/
│           │               └── CustomerControllerTest.java    # Test de integración
│           │
│           ├── account/
│           │   └── application/
│           │       └── service/
│           │           ├── DepositServiceTest.java
│           │           ├── WithdrawServiceTest.java
│           │           └── OpenAccountServiceTest.java
│           │
│           └── transaction/
│               └── application/
│                   └── service/
│                       └── GetTransactionsByAccountServiceTest.java
```

> 💡 **Nota sobre el paquete `shared`:**
> Elementos que son verdaderamente transversales a todas las features (como el `GlobalExceptionHandler`) no pertenecen
> a ninguna feature en particular. Los colocamos en `shared` para no forzarlos en una feature arbitraria. Esto es una
> práctica común en proyectos con Vertical Slicing.
