package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LedgerTransactionRepository
        extends JpaRepository<LedgerTransaction, Long> {

    @Query("""
        SELECT t.id
        FROM LedgerTransaction t
        WHERE t.status = 'SUCCESS'
    """)
    List<Long> findSuccessfulTransactionIds();

    Optional<LedgerTransaction> findByExternalTransactionReference(
            String externalTransactionReference
    );

    @Query("""
        SELECT t
        FROM LedgerTransaction t
        WHERE t.status = 'SUCCESS'
        AND t.externalTransactionReference IS NOT NULL
    """)
    List<LedgerTransaction> findSuccessfulTransactionsWithExternalReference();
}
