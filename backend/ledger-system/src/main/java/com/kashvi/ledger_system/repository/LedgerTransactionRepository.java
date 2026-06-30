package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerTransactionRepository
        extends JpaRepository<LedgerTransaction, Long> {
}