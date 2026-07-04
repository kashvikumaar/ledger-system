ALTER TABLE sagas
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE saga_steps
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE saga_steps
    ADD CONSTRAINT uq_saga_step_type UNIQUE (saga_id, step_type);
