CREATE TABLE reconciliation_reports (
                                       id BIGSERIAL PRIMARY KEY,
                                       total_records BIGINT NOT NULL,
                                       matched BIGINT NOT NULL,
                                       unmatched BIGINT NOT NULL,
                                       duplicates BIGINT NOT NULL,
                                       amount_mismatches BIGINT NOT NULL,
                                       reconciliation_timestamp TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE reconciliation_records (
                                       id BIGSERIAL PRIMARY KEY,
                                       report_id BIGINT NOT NULL,
                                       transaction_reference VARCHAR(255) NOT NULL,
                                       external_amount BIGINT,
                                       internal_amount BIGINT,
                                       currency VARCHAR(10),
                                       external_timestamp TIMESTAMP,
                                       status VARCHAR(50) NOT NULL
                                           CHECK (status IN (
                                               'MATCHED',
                                               'MISSING_IN_LEDGER',
                                               'MISSING_EXTERNALLY',
                                               'AMOUNT_MISMATCH',
                                               'DUPLICATE_TRANSACTION'
                                           )),
                                       details VARCHAR(500),
                                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                       CONSTRAINT fk_reconciliation_report
                                           FOREIGN KEY (report_id)
                                               REFERENCES reconciliation_reports(id)
);
