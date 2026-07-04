package com.kashvi.ledger_system.service;

import com.kashvi.ledger_system.dto.EntryRequest;
import com.kashvi.ledger_system.dto.TransactionRequest;
import com.kashvi.ledger_system.entity.Entry;
import com.kashvi.ledger_system.entity.LedgerTransaction;
import com.kashvi.ledger_system.entity.Saga;
import com.kashvi.ledger_system.entity.SagaStep;
import com.kashvi.ledger_system.enums.SagaStepStatus;
import com.kashvi.ledger_system.enums.StepType;
import com.kashvi.ledger_system.repository.EntryRepository;
import com.kashvi.ledger_system.repository.SagaRepository;
import com.kashvi.ledger_system.repository.SagaStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SagaCompensationService {

    private static final String CREDIT = "CREDIT";
    private static final String DEBIT = "DEBIT";

    private final TransactionService transactionService;
    private final SagaRepository sagaRepository;
    private final SagaStepRepository sagaStepRepository;
    private final EntryRepository entryRepository;

    public SagaCompensationService(
            TransactionService transactionService,
            SagaRepository sagaRepository,
            SagaStepRepository sagaStepRepository,
            EntryRepository entryRepository
    ) {
        this.transactionService = transactionService;
        this.sagaRepository = sagaRepository;
        this.sagaStepRepository = sagaStepRepository;
        this.entryRepository = entryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void compensateCompletedSteps(Long sagaId) {
        List<SagaStep> completedSteps =
                sagaStepRepository.findBySagaIdAndStatusOrderByIdDesc(
                        sagaId,
                        SagaStepStatus.COMPLETED
                );

        for (SagaStep sagaStep : completedSteps) {
            compensateStep(sagaId, sagaStep);
        }
    }

    private void compensateStep(
            Long sagaId,
            SagaStep sagaStep
    ) {
        if (sagaStep.getStepType() == StepType.CREATE_LEDGER_TRANSACTION) {
            compensateLedgerTransaction(sagaId);
            sagaStep.setStatus(SagaStepStatus.COMPENSATED);
            sagaStep.setFailureReason(null);
            sagaStepRepository.save(sagaStep);
        }
    }

    private void compensateLedgerTransaction(Long sagaId) {
        Saga saga =
                getSaga(sagaId);

        LedgerTransaction transaction =
                saga.getTransaction();

        if (transaction == null) {
            throw new RuntimeException("Saga has no ledger transaction to compensate");
        }

        List<Entry> entries =
                entryRepository.findByTransactionId(transaction.getId());

        TransactionRequest reversalRequest =
                buildReversalRequest(entries);

        transactionService.createTransaction(reversalRequest);
    }

    private TransactionRequest buildReversalRequest(List<Entry> entries) {
        TransactionRequest request =
                new TransactionRequest();

        List<EntryRequest> reversalEntries =
                new ArrayList<>();

        for (Entry entry : entries) {
            EntryRequest reversalEntry =
                    new EntryRequest();

            reversalEntry.setAccountId(entry.getAccount().getId());
            reversalEntry.setAmount(entry.getAmount());
            reversalEntry.setType(reverseType(entry.getType()));

            reversalEntries.add(reversalEntry);
        }

        request.setEntries(reversalEntries);

        return request;
    }

    private String reverseType(String type) {
        if (CREDIT.equals(type)) {
            return DEBIT;
        }

        if (DEBIT.equals(type)) {
            return CREDIT;
        }

        throw new RuntimeException("Unsupported ledger entry type");
    }

    private Saga getSaga(Long sagaId) {
        return sagaRepository.findById(sagaId)
                .orElseThrow(() ->
                        new RuntimeException("Saga not found")
                );
    }
}
