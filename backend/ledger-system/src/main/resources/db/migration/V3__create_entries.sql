CREATE TABLE entries (
                         id BIGSERIAL PRIMARY KEY,

                         transaction_id BIGINT NOT NULL,
                         account_id BIGINT NOT NULL,

                         type VARCHAR(10) NOT NULL
                             CHECK (type IN ('DEBIT', 'CREDIT')),

                         amount BIGINT NOT NULL
                             CHECK (amount > 0),

                         created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                         CONSTRAINT fk_transaction
                             FOREIGN KEY (transaction_id)
                                 REFERENCES transactions(id),

                         CONSTRAINT fk_account
                             FOREIGN KEY (account_id)
                                 REFERENCES accounts(id)
);