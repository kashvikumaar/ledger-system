package com.kashvi.ledger_system.service;

import com.kashvi.ledger_system.dto.TransactionRequest;
import com.kashvi.ledger_system.entity.LedgerTransaction;
import com.kashvi.ledger_system.entity.Saga;
import com.kashvi.ledger_system.entity.SagaStep;
import com.kashvi.ledger_system.enums.SagaStatus;
import com.kashvi.ledger_system.enums.SagaStepStatus;
import com.kashvi.ledger_system.enums.StepType;
import com.kashvi.ledger_system.repository.SagaRepository;
import com.kashvi.ledger_system.repository.SagaStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerStepService {

    private final TransactionService transactionService;
    private final SagaRepository sagaRepository;
    private final SagaStepRepository sagaStepRepository;

    public LedgerStepService(
            TransactionService transactionService,
            SagaRepository sagaRepository,
            SagaStepRepository sagaStepRepository
    ) {
        this.transactionService = transactionService;
        this.sagaRepository = sagaRepository;
        this.sagaStepRepository = sagaStepRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeLedgerTransactionStep(
            Long sagaId,
            Long sagaStepId,
            TransactionRequest request
    ) {
        Saga saga =
                getSaga(sagaId);

        SagaStep sagaStep =
                getSagaStep(sagaStepId);

        LedgerTransaction transaction =
                transactionService.createTransaction(request);

        saga.setTransaction(transaction);
        sagaStep.setStatus(SagaStepStatus.COMPLETED);
        sagaStep.setFailureReason(null);

        sagaRepository.save(saga);
        sagaStepRepository.save(sagaStep);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long createSaga() {
        Saga saga = new Saga();
        saga.setStatus(SagaStatus.STARTED);

        return sagaRepository.save(saga).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long createSagaStep(Long sagaId, StepType stepType) {
        Saga saga =
                getSaga(sagaId);

        SagaStep sagaStep = new SagaStep();
        sagaStep.setSaga(saga);
        sagaStep.setStepType(stepType);
        sagaStep.setStatus(SagaStepStatus.PENDING);

        return sagaStepRepository.save(sagaStep).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSagaInProgress(Long sagaId) {
        Saga saga =
                getSaga(sagaId);

        saga.setStatus(SagaStatus.IN_PROGRESS);
        saga.setFailureReason(null);

        sagaRepository.save(saga);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSagaCompleted(Long sagaId) {
        Saga saga =
                getSaga(sagaId);

        saga.setStatus(SagaStatus.COMPLETED);
        saga.setFailureReason(null);

        sagaRepository.save(saga);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSagaFailed(Long sagaId, String failureReason) {
        Saga saga =
                getSaga(sagaId);

        saga.setStatus(SagaStatus.FAILED);
        saga.setFailureReason(failureReason);

        sagaRepository.save(saga);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSagaCompensating(Long sagaId, String failureReason) {
        Saga saga =
                getSaga(sagaId);

        saga.setStatus(SagaStatus.COMPENSATING);
        saga.setFailureReason(failureReason);

        sagaRepository.save(saga);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSagaCompensated(Long sagaId) {
        Saga saga =
                getSaga(sagaId);

        saga.setStatus(SagaStatus.COMPENSATED);

        sagaRepository.save(saga);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSagaCompensationFailed(
            Long sagaId,
            String failureReason
    ) {
        Saga saga =
                getSaga(sagaId);

        saga.setStatus(SagaStatus.COMPENSATION_FAILED);
        saga.setFailureReason(failureReason);

        sagaRepository.save(saga);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markStepInProgress(Long sagaStepId) {
        SagaStep sagaStep =
                getSagaStep(sagaStepId);

        sagaStep.setStatus(SagaStepStatus.IN_PROGRESS);
        sagaStep.setFailureReason(null);

        sagaStepRepository.save(sagaStep);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markStepCompleted(Long sagaStepId) {
        SagaStep sagaStep =
                getSagaStep(sagaStepId);

        sagaStep.setStatus(SagaStepStatus.COMPLETED);
        sagaStep.setFailureReason(null);

        sagaStepRepository.save(sagaStep);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markStepFailedIfNotCompleted(
            Long sagaStepId,
            String failureReason
    ) {
        SagaStep sagaStep =
                getSagaStep(sagaStepId);

        if (sagaStep.getStatus() == SagaStepStatus.COMPLETED) {
            return;
        }

        sagaStep.setStatus(SagaStepStatus.FAILED);
        sagaStep.setFailureReason(failureReason);

        sagaStepRepository.save(sagaStep);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markStepFailed(Long sagaStepId, String failureReason) {
        SagaStep sagaStep =
                getSagaStep(sagaStepId);

        sagaStep.setStatus(SagaStepStatus.FAILED);
        sagaStep.setFailureReason(failureReason);

        sagaStepRepository.save(sagaStep);
    }

    private Saga getSaga(Long sagaId) {
        return sagaRepository.findById(sagaId)
                .orElseThrow(() ->
                        new RuntimeException("Saga not found")
                );
    }

    private SagaStep getSagaStep(Long sagaStepId) {
        return sagaStepRepository.findById(sagaStepId)
                .orElseThrow(() ->
                        new RuntimeException("Saga step not found")
                );
    }

}
