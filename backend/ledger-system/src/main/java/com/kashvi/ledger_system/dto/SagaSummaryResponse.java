package com.kashvi.ledger_system.dto;

import java.time.LocalDateTime;

public class SagaSummaryResponse {

    private Long id;
    private String status;
    private String currentStep;
    private String transactionReference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SagaSummaryResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
