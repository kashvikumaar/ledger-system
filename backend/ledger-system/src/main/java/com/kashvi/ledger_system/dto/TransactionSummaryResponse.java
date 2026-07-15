package com.kashvi.ledger_system.dto;

import java.time.LocalDateTime;

public class TransactionSummaryResponse {

    private Long id;
    private String externalTransactionReference;
    private String status;
    private Long amount;
    private String currency;
    private LocalDateTime createdAt;

    public TransactionSummaryResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getExternalTransactionReference() {
        return externalTransactionReference;
    }

    public String getStatus() {
        return status;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExternalTransactionReference(String externalTransactionReference) {
        this.externalTransactionReference = externalTransactionReference;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
