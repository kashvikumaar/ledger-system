package com.kashvi.ledger_system.dto;

import com.kashvi.ledger_system.enums.ReconciliationStatus;

import java.time.LocalDateTime;

public class ReconciliationRecordResponse {

    private Long id;
    private String transactionReference;
    private Long externalAmount;
    private Long internalAmount;
    private String currency;
    private LocalDateTime externalTimestamp;
    private ReconciliationStatus status;
    private String details;

    public ReconciliationRecordResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public Long getExternalAmount() {
        return externalAmount;
    }

    public Long getInternalAmount() {
        return internalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getExternalTimestamp() {
        return externalTimestamp;
    }

    public ReconciliationStatus getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public void setExternalAmount(Long externalAmount) {
        this.externalAmount = externalAmount;
    }

    public void setInternalAmount(Long internalAmount) {
        this.internalAmount = internalAmount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setExternalTimestamp(LocalDateTime externalTimestamp) {
        this.externalTimestamp = externalTimestamp;
    }

    public void setStatus(ReconciliationStatus status) {
        this.status = status;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
