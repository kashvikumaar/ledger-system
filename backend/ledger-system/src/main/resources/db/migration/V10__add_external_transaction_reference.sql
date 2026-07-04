ALTER TABLE transactions
    ADD COLUMN external_transaction_reference VARCHAR(255);

CREATE UNIQUE INDEX uq_transactions_external_reference
    ON transactions(external_transaction_reference)
    WHERE external_transaction_reference IS NOT NULL;
