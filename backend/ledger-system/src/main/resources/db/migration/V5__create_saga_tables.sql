CREATE TABLE sagas (
                       id BIGSERIAL PRIMARY KEY,
                       transaction_id BIGINT NOT NULL,
                       status VARCHAR(50) NOT NULL
                           CHECK (status IN (
                               'STARTED',
                               'IN_PROGRESS',
                               'COMPLETED',
                               'FAILED',
                               'COMPENSATING',
                               'COMPENSATED'
                           )),
                       failure_reason TEXT,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                       CONSTRAINT fk_saga_transaction
                           FOREIGN KEY (transaction_id)
                               REFERENCES transactions(id)
);

CREATE TABLE saga_steps (
                            id BIGSERIAL PRIMARY KEY,

                            saga_id BIGINT NOT NULL,

                            step_type VARCHAR(50) NOT NULL
                                CHECK (step_type IN (
                                    'CREATE_LEDGER_TRANSACTION'
                                )),

                            status VARCHAR(50) NOT NULL
                                CHECK (status IN (
                                    'PENDING',
                                    'IN_PROGRESS',
                                    'COMPLETED',
                                    'FAILED',
                                    'COMPENSATED'
                                )),

                            failure_reason TEXT,
                            created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                            updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                            CONSTRAINT fk_saga
                                FOREIGN KEY (saga_id)
                                    REFERENCES sagas(id)
);
