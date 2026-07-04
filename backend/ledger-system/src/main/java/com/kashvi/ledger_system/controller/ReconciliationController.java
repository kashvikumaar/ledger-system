package com.kashvi.ledger_system.controller;

import com.kashvi.ledger_system.dto.ReconciliationReportResponse;
import com.kashvi.ledger_system.service.ReconciliationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/reconciliation")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(
            ReconciliationService reconciliationService
    ) {
        this.reconciliationService = reconciliationService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ReconciliationReportResponse> upload(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                reconciliationService.reconcile(file)
        );
    }

    @GetMapping("/{id}")
    public ReconciliationReportResponse getReport(
            @PathVariable Long id
    ) {
        return reconciliationService.getReport(id);
    }
}
