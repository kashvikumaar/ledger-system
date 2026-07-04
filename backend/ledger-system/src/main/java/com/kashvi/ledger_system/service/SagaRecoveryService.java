package com.kashvi.ledger_system.service;

import com.kashvi.ledger_system.entity.Saga;
import com.kashvi.ledger_system.entity.SagaStep;
import com.kashvi.ledger_system.enums.SagaStatus;
import com.kashvi.ledger_system.enums.SagaStepStatus;
import com.kashvi.ledger_system.repository.SagaRepository;
import com.kashvi.ledger_system.repository.SagaStepRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SagaRecoveryService {

    private final SagaRepository sagaRepository;
    private final SagaStepRepository sagaStepRepository;
    private final SagaCompensationService sagaCompensationService;
    private final LedgerStepService ledgerStepService;

    public SagaRecoveryService(
            SagaRepository sagaRepository,
            SagaStepRepository sagaStepRepository,
            SagaCompensationService sagaCompensationService,
            LedgerStepService ledgerStepService
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaStepRepository = sagaStepRepository;
        this.sagaCompensationService = sagaCompensationService;
        this.ledgerStepService = ledgerStepService;
    }

    @Scheduled(fixedDelay = 30000)
    public void recoverSagas() {
        List<Saga> recoverableSagas =
                sagaRepository.findByStatusIn(List.of(
                        SagaStatus.STARTED,
                        SagaStatus.IN_PROGRESS,
                        SagaStatus.COMPENSATING,
                        SagaStatus.COMPENSATION_FAILED
                ));

        for (Saga saga : recoverableSagas) {
            recoverSaga(saga);
        }
    }

    private void recoverSaga(Saga saga) {
        List<SagaStep> sagaSteps =
                sagaStepRepository.findBySagaIdOrderByIdAsc(
                        saga.getId()
                );

        if (shouldResumeCompensation(saga)) {
            resumeCompensation(saga);
            return;
        }

        if (shouldMarkCompleted(saga, sagaSteps)) {
            ledgerStepService.markSagaCompleted(saga.getId());
        }
    }

    private boolean shouldResumeCompensation(Saga saga) {
        return saga.getStatus() == SagaStatus.COMPENSATING
                || saga.getStatus() == SagaStatus.COMPENSATION_FAILED;
    }

    private boolean shouldMarkCompleted(
            Saga saga,
            List<SagaStep> sagaSteps
    ) {
        return isForwardRecoverable(saga)
                && hasCompletedStep(sagaSteps)
                && !hasUnfinishedStep(sagaSteps);
    }

    private boolean isForwardRecoverable(Saga saga) {
        return saga.getStatus() == SagaStatus.STARTED
                || saga.getStatus() == SagaStatus.IN_PROGRESS;
    }

    private boolean hasCompletedStep(List<SagaStep> sagaSteps) {
        return sagaSteps.stream()
                .anyMatch(step ->
                        step.getStatus() == SagaStepStatus.COMPLETED
                );
    }

    private boolean hasUnfinishedStep(List<SagaStep> sagaSteps) {
        return sagaSteps.stream()
                .anyMatch(step ->
                        step.getStatus() == SagaStepStatus.PENDING
                                || step.getStatus() == SagaStepStatus.IN_PROGRESS
                );
    }

    private void resumeCompensation(Saga saga) {
        try {
            ledgerStepService.markSagaCompensating(
                    saga.getId(),
                    saga.getFailureReason()
            );

            sagaCompensationService.compensateCompletedSteps(
                    saga.getId()
            );

            ledgerStepService.markSagaCompensated(
                    saga.getId()
            );

        } catch (Exception e) {
            ledgerStepService.markSagaCompensationFailed(
                    saga.getId(),
                    e.getMessage()
            );
        }
    }
}
