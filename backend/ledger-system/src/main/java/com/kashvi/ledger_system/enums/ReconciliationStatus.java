package com.kashvi.ledger_system.enums;

public enum ReconciliationStatus {
    MATCHED,
    MISSING_IN_LEDGER,
    MISSING_EXTERNALLY,
    AMOUNT_MISMATCH,
    DUPLICATE_TRANSACTION
}
