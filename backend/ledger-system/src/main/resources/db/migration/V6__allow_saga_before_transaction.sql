ALTER TABLE sagas
    ALTER COLUMN transaction_id DROP NOT NULL;

ALTER TABLE sagas
    ADD CONSTRAINT uq_saga_transaction UNIQUE (transaction_id);
