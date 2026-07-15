package com.kashvi.ledger_system.dto;

import java.util.List;

public class DashboardSummaryResponse {

    private Long totalAccounts;
    private Long totalTransactions;
    private Long successfulTransactions;
    private Long failedTransactions;
    private Long activeSagas;
    private Long pendingRecoveries;
    private List<TransactionSummaryResponse> recentTransactions;
    private List<SagaSummaryResponse> recentSagas;
    private List<ReconciliationReportResponse> recentReconciliationReports;

    public Long getTotalAccounts() {
        return totalAccounts;
    }

    public Long getTotalTransactions() {
        return totalTransactions;
    }

    public Long getSuccessfulTransactions() {
        return successfulTransactions;
    }

    public Long getFailedTransactions() {
        return failedTransactions;
    }

    public Long getActiveSagas() {
        return activeSagas;
    }

    public Long getPendingRecoveries() {
        return pendingRecoveries;
    }

    public List<TransactionSummaryResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public List<SagaSummaryResponse> getRecentSagas() {
        return recentSagas;
    }

    public List<ReconciliationReportResponse> getRecentReconciliationReports() {
        return recentReconciliationReports;
    }

    public void setTotalAccounts(Long totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public void setTotalTransactions(Long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public void setSuccessfulTransactions(Long successfulTransactions) {
        this.successfulTransactions = successfulTransactions;
    }

    public void setFailedTransactions(Long failedTransactions) {
        this.failedTransactions = failedTransactions;
    }

    public void setActiveSagas(Long activeSagas) {
        this.activeSagas = activeSagas;
    }

    public void setPendingRecoveries(Long pendingRecoveries) {
        this.pendingRecoveries = pendingRecoveries;
    }

    public void setRecentTransactions(List<TransactionSummaryResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public void setRecentSagas(List<SagaSummaryResponse> recentSagas) {
        this.recentSagas = recentSagas;
    }

    public void setRecentReconciliationReports(
            List<ReconciliationReportResponse> recentReconciliationReports
    ) {
        this.recentReconciliationReports = recentReconciliationReports;
    }
}
