CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              idempotency_key UUID UNIQUE,
                              status VARCHAR(50) NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT NOW()
);