# LedgerCore

> **A production-inspired payment and accounting engine that demonstrates the core backend infrastructure behind modern fintech platforms.**

LedgerCore is a backend financial transaction engine built with **Java**, **Spring Boot**, **PostgreSQL**, and **Redis**. It implements **double-entry accounting**, **Saga-based transaction orchestration**, **Redis-backed idempotency**, **automatic workflow recovery**, and a **financial reconciliation engine** to model the reliability, consistency, and auditability expected in modern payment systems.

---

## Why LedgerCore?

Traditional CRUD applications rarely demonstrate the engineering challenges involved in financial systems.

LedgerCore explores how payment platforms maintain **financial correctness**, **fault tolerance**, and **operational reliability** using immutable ledger design, distributed transaction orchestration, compensating transactions, and reconciliation workflows.

---

## Table of Contents

- [Features](#features)
- [Design Principles](#design-principles)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Database Design](#database-design)
- [Payment Flow](#payment-flow)
- [Failure Handling](#failure-handling)
- [Saga Recovery](#saga-recovery)
- [Financial Reconciliation](#financial-reconciliation)
- [REST APIs](#rest-apis)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Engineering Concepts](#engineering-concepts)
- [Future Enhancements](#future-enhancements)

---

# Features

### Double-Entry Ledger

- Immutable append-only ledger
- Double-entry accounting
- Dynamic balance computation
- Money stored as integer paise to eliminate floating-point precision errors

### Reliable Transaction Processing

- ACID-compliant transaction processing
- Atomic ledger writes using Spring Transactions
- PostgreSQL-enforced append-only ledger
- Version-controlled schema with Flyway

### Redis-backed Idempotency

- Safe request retries
- Duplicate payment prevention
- In-flight request detection

### Saga Orchestration

- Production-inspired Saga implementation
- Independent transaction boundaries using `REQUIRES_NEW`
- Durable Saga state persistence
- Optimistic locking
- Crash-safe workflow execution

### Compensating Transactions

- Financial rollback through reversal transactions
- Immutable audit trail
- Guaranteed double-entry correctness
- No updates or deletes on ledger entries

### Automatic Recovery

- Scheduled Saga recovery worker
- Resume interrupted workflows
- Resume failed compensations
- Recovery after unexpected application failures

### Financial Reconciliation

- CSV upload of external transaction records
- Matching using external transaction references
- Detection of:
  - Missing transactions
  - Duplicate transactions
  - Amount mismatches
  - Successful matches
- Durable reconciliation reports

---

# Design Principles

LedgerCore is built around a few core engineering principles:

- **Financial Correctness** – Every transaction follows double-entry accounting.
- **Immutability** – Ledger entries are never updated or deleted.
- **Reliability** – Idempotency, Saga orchestration, and recovery protect against failures.
- **Auditability** – Every financial event remains traceable.
- **Operational Visibility** – Reconciliation identifies inconsistencies without modifying financial data.

---

# Architecture

```text
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

# Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Programming language |
| Spring Boot | REST API & application framework |
| Spring Data JPA / Hibernate | ORM & persistence |
| PostgreSQL | Durable ledger storage |
| Redis | Idempotency cache |
| Flyway | Database versioning & migrations |
| Maven | Dependency management |
| Docker | Local Redis deployment |
| Postman | API testing |
| Git & GitHub | Version control |

---

# Database Design

### Core Ledger

- `accounts`
- `transactions`
- `entries`

### Saga

- `sagas`
- `saga_steps`

### Reconciliation

- `reconciliation_reports`
- `reconciliation_records`

---

# Payment Flow

```text
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

# Failure Handling

If a Saga step fails:

```text
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

Instead of rolling back database changes, LedgerCore preserves financial correctness by appending **reversal transactions**, ensuring the ledger remains immutable and fully auditable.

---

# Saga Recovery

LedgerCore automatically recovers interrupted workflows.

A scheduled recovery worker periodically scans for Sagas in recoverable states:

- `STARTED`
- `IN_PROGRESS`
- `COMPENSATING`
- `COMPENSATION_FAILED`

It then:

- Resumes unfinished workflows
- Continues pending compensation
- Prevents duplicate execution using persisted Saga state
- Preserves financial consistency

---

# Financial Reconciliation

External transaction records can be uploaded as CSV.

Example:

```csv
transaction_reference,amount,currency,timestamp
pay_7AF93KD,500,INR,2026-07-04T10:30:00
upi_987654321,750,INR,2026-07-04T11:00:00
```

Transactions are matched using **external transaction references**, similar to modern payment processors.

Possible reconciliation outcomes:

| Status | Description |
|---------|-------------|
| MATCHED | Internal and external records agree |
| MISSING_IN_LEDGER | Present externally but missing internally |
| MISSING_EXTERNALLY | Present internally but missing externally |
| DUPLICATE_TRANSACTION | Duplicate external transaction |
| AMOUNT_MISMATCH | Transaction found but amount differs |

Reconciliation is **read-only** and never modifies ledger data.

---

# REST APIs

### Transactions

#### Create Transaction

```http
POST /transactions
```

Requires an `Idempotency-Key` request header.

#### Get Account Balance

```http
GET /transactions/accounts/{id}/balance
```

Returns the current balance computed dynamically from ledger entries.

---

### Reconciliation

#### Upload CSV

```http
POST /reconciliation/upload
```

#### Get Report

```http
GET /reconciliation/{id}
```

---

# Project Structure

```text
src
├── controller
├── dto
├── entity
├── enums
├── repository
├── service
└── resources
    └── db
        └── migration
```

---

# Getting Started

### Clone the repository

```bash
git clone https://github.com/kashvikumaar/ledgercore.git
cd ledgercore
```

### Create PostgreSQL database

```text
ledger
```

### Start Redis

```bash
docker run -d -p 6379:6379 redis
```

### Configure application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ledger
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### Run the application

```bash
mvn spring-boot:run
```

Flyway automatically executes all database migrations during startup.

---

# Engineering Concepts

- Double-entry accounting
- Immutable ledger architecture
- ACID transactions
- Redis-backed idempotency
- Saga Pattern
- Compensating transactions
- Optimistic locking
- Automatic workflow recovery
- Financial reconciliation
- Version-controlled database migrations
- Layered Spring Boot architecture
- Auditability and financial consistency

---

# Future Enhancements

- Settlement engine
- Event-driven architecture (Kafka)
- Outbox pattern
- Multi-currency support
- Fraud detection integration
- Account statements
- Metrics & monitoring
- OpenAPI / Swagger
- CI/CD pipeline
- Cloud deployment

---

# Author

**Kashvi Kumar**

LedgerCore was built as a backend engineering project to explore the reliability, consistency, and operational workflows behind modern fintech payment systems.
