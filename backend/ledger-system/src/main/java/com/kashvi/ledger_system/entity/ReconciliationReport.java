package com.kashvi.ledger_system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_reports")
public class ReconciliationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_records", nullable = false)
    private Long totalRecords;

    @Column(nullable = false)
    private Long matched;

    @Column(nullable = false)
    private Long unmatched;

    @Column(nullable = false)
    private Long duplicates;

    @Column(name = "amount_mismatches", nullable = false)
    private Long amountMismatches;

    @CurrentTimestamp
    @Column(name = "reconciliation_timestamp", nullable = false, updatable = false)
    private LocalDateTime reconciliationTimestamp;

    public ReconciliationReport() {
    }

    public Long getId() {
        return id;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public Long getMatched() {
        return matched;
    }

    public Long getUnmatched() {
        return unmatched;
    }

    public Long getDuplicates() {
        return duplicates;
    }

    public Long getAmountMismatches() {
        return amountMismatches;
    }

    public LocalDateTime getReconciliationTimestamp() {
        return reconciliationTimestamp;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public void setMatched(Long matched) {
        this.matched = matched;
    }

    public void setUnmatched(Long unmatched) {
        this.unmatched = unmatched;
    }

    public void setDuplicates(Long duplicates) {
        this.duplicates = duplicates;
    }

    public void setAmountMismatches(Long amountMismatches) {
        this.amountMismatches = amountMismatches;
    }

    public void setReconciliationTimestamp(LocalDateTime reconciliationTimestamp) {
        this.reconciliationTimestamp = reconciliationTimestamp;
    }
}
