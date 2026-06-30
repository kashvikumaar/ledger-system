package com.kashvi.ledger_system.controller;

import com.kashvi.ledger_system.dto.TransactionRequest;
import com.kashvi.ledger_system.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.kashvi.ledger_system.service.IdempotencyService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final IdempotencyService idempotencyService;

    public TransactionController(
            TransactionService transactionService,
            IdempotencyService idempotencyService
    ) {
        this.transactionService = transactionService;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(

            @RequestHeader("Idempotency-Key")
            String idempotencyKey,

            @RequestBody
            TransactionRequest request
    ) {

        String stored =
                idempotencyService.getStoredResult(idempotencyKey);

        if (stored != null) {

            if (stored.equals("IN_FLIGHT")) {

                return ResponseEntity
                        .status(409)
                        .body("Request already processing");
            }

            return ResponseEntity.ok(stored);
        }

        // claim new request
        boolean claimed =
                idempotencyService.claim(idempotencyKey);

        if (!claimed) {

            return ResponseEntity
                    .status(409)
                    .body("Request already processing");
        }

        try {

            transactionService.createTransaction(request);

            String response =
                    "Transaction created successfully";

            idempotencyService.store(
                    idempotencyKey,
                    response
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            idempotencyService.delete(idempotencyKey);

            throw e;
        }
    }

    @GetMapping("/accounts/{id}/balance")
    public Long getBalance(@PathVariable Long id) {
        return transactionService.getBalance(id);
    }
}