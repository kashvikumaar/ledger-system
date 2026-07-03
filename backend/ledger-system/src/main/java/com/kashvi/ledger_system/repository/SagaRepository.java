package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.Saga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaRepository extends JpaRepository<Saga, Long> {
}
