CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          type VARCHAR(50) NOT NULL,
                          currency VARCHAR(10) NOT NULL DEFAULT 'INR',
                          created_at TIMESTAMP NOT NULL DEFAULT NOW()
);