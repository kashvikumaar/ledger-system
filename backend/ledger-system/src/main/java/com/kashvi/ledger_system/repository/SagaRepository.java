package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.Saga;
import com.kashvi.ledger_system.enums.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SagaRepository extends JpaRepository<Saga, Long> {

    List<Saga> findByStatusIn(Collection<SagaStatus> statuses);
}
