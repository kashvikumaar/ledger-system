package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    @Query("""
        SELECT COALESCE(
            SUM(
                CASE
                    WHEN e.type = 'CREDIT'
                    THEN e.amount
                    ELSE -e.amount
                END
            ),
            0
        )
        FROM Entry e
        WHERE e.account.id = :accountId
    """)
    Long getBalance(Long accountId);

    List<Entry> findByTransactionId(Long transactionId);

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Entry e
        WHERE e.transaction.id = :transactionId
        AND e.type = 'CREDIT'
    """)
    Long getTransactionAmount(Long transactionId);
}
