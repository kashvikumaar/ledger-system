package com.kashvi.ledger_system.repository;

import com.kashvi.ledger_system.entity.SagaStep;
import com.kashvi.ledger_system.enums.SagaStepStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

    List<SagaStep> findBySagaIdOrderByIdAsc(Long sagaId);

    List<SagaStep> findBySagaIdAndStatusOrderByIdDesc(
            Long sagaId,
            SagaStepStatus status
    );
}
