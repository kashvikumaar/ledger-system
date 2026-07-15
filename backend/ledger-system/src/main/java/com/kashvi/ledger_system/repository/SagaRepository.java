package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.Saga;
import com.kashvi.ledger_system.enums.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SagaRepository extends JpaRepository<Saga, Long> {

    List<Saga> findByStatusIn(Collection<SagaStatus> statuses);

    Long countByStatusIn(Collection<SagaStatus> statuses);

    Optional<Saga> findByTransactionId(Long transactionId);

    List<Saga> findTop10ByOrderByUpdatedAtDesc();

    List<Saga> findAllByOrderByUpdatedAtDesc();
}
