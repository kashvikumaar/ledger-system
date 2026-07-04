package com.kashvi.ledger_system.entity;

import com.kashvi.ledger_system.enums.ReconciliationStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_records")
public class ReconciliationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private ReconciliationReport report;

    @Column(name = "transaction_reference", nullable = false)
    private String transactionReference;

    @Column(name = "external_amount")
    private Long externalAmount;

    @Column(name = "internal_amount")
    private Long internalAmount;

    private String currency;

    @Column(name = "external_timestamp")
    private LocalDateTime externalTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReconciliationStatus status;

    private String details;

    @CurrentTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ReconciliationRecord() {
    }

    public Long getId() {
        return id;
    }

    public ReconciliationReport getReport() {
        return report;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setReport(ReconciliationReport report) {
        this.report = report;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
