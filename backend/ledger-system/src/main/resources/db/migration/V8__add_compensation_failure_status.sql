ALTER TABLE sagas
    DROP CONSTRAINT sagas_status_check;

ALTER TABLE sagas
    ADD CONSTRAINT sagas_status_check
        CHECK (status IN (
            'STARTED',
            'IN_PROGRESS',
            'COMPLETED',
            'FAILED',
            'COMPENSATING',
            'COMPENSATED',
            'COMPENSATION_FAILED'
        ));
