# LedgerCore

**A production-inspired, full-stack payment and accounting platform that demonstrates the engineering behind modern fintech systems.**

LedgerCore pairs a backend engineering project — a distributed transaction engine built with **Java, Spring Boot, PostgreSQL, and Redis** — with a **React + TypeScript operations console** on top of it. The backend implements double-entry accounting, Saga-based transaction orchestration, Redis-backed idempotency, automatic workflow recovery, and a financial reconciliation engine; the frontend surfaces all of it through a live dashboard — modeling the reliability, consistency, auditability, and operability expected of modern payment platforms.

---

## Highlights

- Immutable, append-only double-entry ledger
- Redis-backed idempotency
- Saga orchestration with compensating transactions
- Automatic workflow recovery
- Financial reconciliation engine
- Internal React operations dashboard

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Why LedgerCore?](#why-ledgercore)
- [Design Principles](#design-principles)
- [Database Design](#database-design)
- [Payment Flow](#payment-flow)
- [Failure Handling](#failure-handling)
- [Saga Recovery](#saga-recovery)
- [Financial Reconciliation](#financial-reconciliation)
- [Operations Dashboard](#operations-dashboard)
- [REST APIs](#rest-apis)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Engineering Concepts Demonstrated](#engineering-concepts-demonstrated)
- [Future Enhancements](#future-enhancements)
- [Author](#author)

---

## Features

### 🧾 Double-Entry Ledger
- Immutable, append-only ledger
- Accounts, transactions, and entries modeled explicitly
- Dynamic account balance computation
- Money stored as **integer paise** to eliminate floating-point precision errors
- Validation ensuring every transaction balances (total debits = total credits)
- PostgreSQL trigger preventing updates/deletes on ledger entries

### ⚙️ ACID Transaction Processing
- Atomic ledger writes via `@Transactional`
- Rollback on validation failure
- Layered architecture (Repository → Service → Controller)

### 🔁 Redis-Backed Idempotency
- `Idempotency-Key` request header
- Safe retry handling and duplicate payment prevention
- In-flight request detection (`409 Conflict`)
- 24-hour TTL, with automatic removal of failed requests to allow retry

### 🧩 Saga Orchestration
- Orchestration-based Saga pattern via `TransactionSagaOrchestrator` and `LedgerStepService`
- Durable Saga persistence and state machine
- Independent transaction boundaries using `Propagation.REQUIRES_NEW`
- Saga lifecycle kept fully separate from core ledger logic

### ↩️ Compensating Transactions
Instead of `DELETE` / `UPDATE` / `ROLLBACK`, LedgerCore performs:

```
DEBIT → Compensation → CREDIT
```

- Reversal transactions instead of destructive rollbacks
- Immutable audit trail preserved at all times
- Append-only compensation guarantees financial correctness

### 🛠️ Automatic Saga Recovery
- Scheduled recovery worker detects unfinished Sagas and failed compensations
- Resumes interrupted workflows after a crash
- Protected by optimistic locking to prevent duplicate execution

### 🔒 Optimistic Locking
- `@Version`-based concurrency control
- Prevents lost updates during concurrent recovery and compensation

### 📊 Financial Reconciliation Engine
- CSV upload of external transaction records
- Matching against **external transaction references** (e.g. `pay_7AF93KD`, `upi_987654321`) rather than internal IDs
- Durable, read-only reconciliation reports
- Detects: missing transactions, duplicate transactions, amount mismatches, and successful matches

### 🖥️ Internal Operations Dashboard
A React-based operations console featuring:
- Dashboard overview and system health
- Create Transaction page with automatic idempotency key generation
- Transaction explorer, account monitoring, saga monitor, and saga recovery monitoring
- Reconciliation upload
- Dark, fintech-inspired, responsive UI with a typed Axios API layer

---

## Architecture

The React operations console consumes the REST APIs below through a typed Axios layer; the backend request flow looks like this:

```
                        Client
                           │
                           ▼
                TransactionController
                           │
                           ▼
                 IdempotencyService
                     (Redis Cache)
                           │
                           ▼
             TransactionSagaOrchestrator
                │                    │
                ▼                    ▼
        LedgerStepService   SagaCompensationService
                │
                ▼
          TransactionService
                │
      ┌─────────┴──────────┐
      ▼                    ▼
 Spring Data JPA       PostgreSQL
                │
                ▼
     Double-Entry Ledger Engine
```

---

## Tech Stack

**Backend**

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot | REST API & application framework |
| Spring Data JPA / Hibernate | ORM & persistence |
| PostgreSQL | Durable ledger storage |
| Redis | Idempotency cache |
| Flyway | Database versioning & migrations |
| Maven | Dependency management |

**Frontend**

| Technology | Purpose |
|---|---|
| React | UI framework |
| TypeScript | Type-safe application logic |
| Tailwind CSS | Styling |
| Axios | Typed API client |
| Vite | Build tooling |

**Tools**

Docker · Git · GitHub · Postman

---

## Why LedgerCore?

Traditional CRUD applications rarely demonstrate the engineering challenges involved in financial systems. LedgerCore explores how payment infrastructure maintains **financial correctness, fault tolerance, and operational reliability** using immutable ledger design, distributed transaction orchestration, compensating transactions, and reconciliation workflows — and pairs that engine with a full **operations dashboard** so those internals are actually observable, not just implemented.

---

## Design Principles

LedgerCore is built around a few core engineering principles:

| Principle | Description |
|---|---|
| **Financial Correctness** | Every transaction follows double-entry accounting. |
| **Immutability** | Ledger entries are never updated or deleted. |
| **Reliability** | Idempotency, Saga orchestration, and recovery protect against failures. |
| **Auditability** | Every financial event remains traceable. |
| **Operational Visibility** | Reconciliation identifies inconsistencies without modifying financial data. |

---

## Database Design

Schema is managed with **Flyway** migrations for fully version-controlled schema evolution.

**Core Ledger**
- `accounts`
- `transactions`
- `entries`

**Saga**
- `sagas`
- `saga_steps`

**Reconciliation**
- `reconciliation_reports`
- `reconciliation_records`

Migrations cover the ledger tables and append-only trigger, saga tables and improvements, optimistic locking, compensation updates, reconciliation tables, and external transaction references.

---

## Payment Flow

```
Client
  │
  POST /transactions
  │
  ▼
Redis Idempotency Check
  │
  ▼
Create Saga
  │
  ▼
Execute Ledger Transaction
  │
  ▼
Persist Debit & Credit Entries
  │
  ▼
Complete Saga
  │
  ▼
Return Response
```

---

## Failure Handling

If a Saga step fails:

```
Failure
  │
  ▼
Saga marked COMPENSATING
  │
  ▼
Execute Reversal Transactions
  │
  ▼
Ledger remains balanced
  │
  ▼
Saga marked COMPENSATED
```

Instead of rolling back database changes, LedgerCore preserves financial correctness by **appending reversal transactions**, ensuring the ledger stays immutable and fully auditable.

---

## Saga Recovery

LedgerCore automatically recovers interrupted workflows. A scheduled recovery worker periodically scans for Sagas in recoverable states:

- `STARTED`
- `IN_PROGRESS`
- `COMPENSATING`
- `COMPENSATION_FAILED`

It then:
- Resumes unfinished workflows
- Continues pending compensation
- Prevents duplicate execution using persisted Saga state (protected by optimistic locking)
- Preserves financial consistency throughout

---

## Financial Reconciliation

External transaction records can be uploaded as CSV:

```csv
transaction_reference,amount,currency,timestamp
pay_7AF93KD,500,INR,2026-07-04T10:30:00
upi_987654321,750,INR,2026-07-04T11:00:00
```

Transactions are matched using **external transaction references**, the same approach used across modern payment infrastructure.

**Possible reconciliation outcomes:**

| Status | Description |
|---|---|
| `MATCHED` | Internal and external records agree |
| `MISSING_IN_LEDGER` | Present externally but missing internally |
| `MISSING_EXTERNALLY` | Present internally but missing externally |
| `DUPLICATE_TRANSACTION` | Duplicate external transaction |
| `AMOUNT_MISMATCH` | Transaction found but amount differs |

Reconciliation is **read-only** and never modifies ledger data.

---

## Operations Dashboard

The internal React console provides read-only operational visibility:

- Dashboard summary
- Transaction and account explorers
- Account entry views
- Saga list and saga detail views
- Saga recovery monitoring
- Reconciliation upload
- System health endpoint

---

## REST APIs

### Transactions

**Create Transaction**
```
POST /transactions
```
Requires an `Idempotency-Key` request header.

**Get Account Balance**
```
GET /transactions/accounts/{id}/balance
```
Returns the current balance, computed dynamically from ledger entries.

### Reconciliation

**Upload CSV**
```
POST /reconciliation/upload
```

**Get Report**
```
GET /reconciliation/{id}
```

### Operations (internal, read-only)

- Dashboard summary
- Transactions / Accounts / Account entries
- Sagas / Saga details
- Health endpoint

---

## Project Structure

```
backend/
    ledger-system/
frontend/
    ledger-ops-dashboard/
docs/
README.md
```

---

## Getting Started

**1. Clone the repository**
```bash
git clone https://github.com/kashvikumaar/ledger-system.git
cd ledger-system
```

**2. Create a PostgreSQL database**
```
ledger
```

**3. Start Redis**
```bash
docker run -d -p 6379:6379 redis
```

**4. Configure `application.properties`**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ledger
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.data.redis.host=localhost
spring.data.redis.port=6379
```

> Make sure PostgreSQL and Redis are both running before starting the backend.

**5. Run the backend**
```bash
mvn spring-boot:run
```

Flyway automatically executes all database migrations on startup.

**6. Run the frontend**
```bash
cd frontend
npm install
npm run dev
```

The operations console will be available in your browser, talking to the backend via the typed Axios API layer.

---

## Engineering Concepts Demonstrated

- Double-entry accounting & ledger systems
- Immutable, append-only ledger architecture
- ACID transaction processing
- Redis-backed idempotency
- Distributed transactions via the Saga pattern
- Compensating transactions
- Optimistic locking & crash-safe workflows
- Automatic workflow recovery
- Financial reconciliation
- Version-controlled database migrations (Flyway)
- Layered Spring Boot architecture (Repository–Service–Controller, DTOs, domain-driven design)
- Auditability, fault tolerance, and operational dashboards

---

## Future Enhancements

- [ ] Kafka event streaming
- [ ] Outbox pattern
- [ ] Settlement engine
- [ ] Multi-currency support
- [ ] Fraud detection integration
- [ ] Account statements
- [ ] Metrics & monitoring
- [ ] OpenAPI / Swagger documentation
- [ ] CI/CD pipeline
- [ ] Cloud deployment

---

## Author

**Kashvi Kumar**

LedgerCore was built as a full-stack engineering project to explore the reliability, consistency, and operational workflows behind modern fintech payment systems — from the ledger internals up to the dashboard used to observe them.
