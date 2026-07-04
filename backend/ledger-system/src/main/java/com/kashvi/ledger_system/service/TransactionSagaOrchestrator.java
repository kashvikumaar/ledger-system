package com.kashvi.ledger_system.service;

import com.kashvi.ledger_system.dto.TransactionRequest;
import com.kashvi.ledger_system.enums.StepType;
import org.springframework.stereotype.Service;

@Service
public class TransactionSagaOrchestrator {

    private final LedgerStepService ledgerStepService;
    private final SagaCompensationService sagaCompensationService;

    public TransactionSagaOrchestrator(
            LedgerStepService ledgerStepService,
            SagaCompensationService sagaCompensationService
    ) {
        this.ledgerStepService = ledgerStepService;
        this.sagaCompensationService = sagaCompensationService;
    }

    public Long execute(TransactionRequest request) {
        Long sagaId = null;
        Long ledgerStepId = null;

        try {

            sagaId =
                    ledgerStepService.createSaga();

            ledgerStepId =
                    ledgerStepService.createSagaStep(
                            sagaId,
                            StepType.CREATE_LEDGER_TRANSACTION
                    );

            ledgerStepService.markSagaInProgress(sagaId);

            ledgerStepService.markStepInProgress(ledgerStepId);

            ledgerStepService.executeLedgerTransactionStep(
                    sagaId,
                    ledgerStepId,
                    request
            );

            ledgerStepService.markSagaCompleted(sagaId);

            return sagaId;

        } catch (Exception e) {

            compensateAfterFailure(
                    sagaId,
                    ledgerStepId,
                    e
            );

            throw e;
        }
    }

    private void compensateAfterFailure(
            Long sagaId,
            Long sagaStepId,
            Exception originalException
    ) {
        String failureReason =
                originalException.getMessage();

        if (sagaId == null) {
            return;
        }

        try {
            ledgerStepService.markSagaCompensating(
                    sagaId,
                    failureReason
            );
        } catch (Exception compensationUpdateException) {
            originalException.addSuppressed(compensationUpdateException);
        }

        if (sagaStepId != null) {
            try {
                ledgerStepService.markStepFailedIfNotCompleted(
                        sagaStepId,
                        failureReason
                );
            } catch (Exception failureUpdateException) {
                originalException.addSuppressed(failureUpdateException);
            }
        }

        try {
            sagaCompensationService.compensateCompletedSteps(sagaId);
            ledgerStepService.markSagaCompensated(sagaId);
        } catch (Exception compensationException) {
            originalException.addSuppressed(compensationException);
            markCompensationFailed(
                    sagaId,
                    compensationException
            );
        }
    }

    private void markCompensationFailed(
            Long sagaId,
            Exception compensationException
    ) {
        try {
            ledgerStepService.markSagaCompensationFailed(
                    sagaId,
                    compensationException.getMessage()
            );
        } catch (Exception failureUpdateException) {
            compensationException.addSuppressed(failureUpdateException);
        }
    }
}
