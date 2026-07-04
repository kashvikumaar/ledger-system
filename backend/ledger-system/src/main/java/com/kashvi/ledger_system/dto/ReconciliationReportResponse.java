package com.kashvi.ledger_system.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReconciliationReportResponse {

    private Long id;
    private Long totalRecords;
    private Long matched;
    private Long unmatched;
    private Long duplicates;
    private Long amountMismatches;
    private LocalDateTime reconciliationTimestamp;
    private List<ReconciliationRecordResponse> records;

    public ReconciliationReportResponse() {
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

    public List<ReconciliationRecordResponse> getRecords() {
        return records;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setRecords(List<ReconciliationRecordResponse> records) {
        this.records = records;
    }
}
