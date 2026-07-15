package com.kashvi.ledger_system.controller;

import com.kashvi.ledger_system.dto.*;
import com.kashvi.ledger_system.service.OperationsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operations")
public class OperationsController {

    private final OperationsService operationsService;

    public OperationsController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @GetMapping("/dashboard")
    public DashboardSummaryResponse getDashboard() {
        return operationsService.getDashboard();
    }

    @GetMapping("/accounts")
    public List<AccountSummaryResponse> getAccounts(
            @RequestParam(required = false) String search
    ) {
        return operationsService.getAccounts(search);
    }

    @GetMapping("/accounts/{id}/entries")
    public List<LedgerEntryResponse> getAccountEntries(
            @PathVariable Long id
    ) {
        return operationsService.getAccountEntries(id);
    }

    @GetMapping("/transactions")
    public List<TransactionSummaryResponse> getTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return operationsService.getTransactions(
                status,
                search,
                page,
                size
        );
    }

    @GetMapping("/transactions/{id}")
    public TransactionDetailResponse getTransaction(@PathVariable Long id) {
        return operationsService.getTransaction(id);
    }

    @GetMapping("/sagas")
    public List<SagaSummaryResponse> getSagas(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "false") boolean recoverable
    ) {
        return operationsService.getSagas(status, recoverable);
    }

    @GetMapping("/sagas/{id}")
    public SagaDetailResponse getSaga(@PathVariable Long id) {
        return operationsService.getSaga(id);
    }

    @GetMapping("/health")
    public HealthStatusResponse getHealth() {
        return operationsService.getHealth();
    }
}
