package com.kashvi.ledger_system.dto;

import java.util.List;

public class TransactionDetailResponse extends TransactionSummaryResponse {

    private List<LedgerEntryResponse> entries;
    private SagaSummaryResponse saga;

    public TransactionDetailResponse() {
    }

    public List<LedgerEntryResponse> getEntries() {
        return entries;
    }

    public SagaSummaryResponse getSaga() {
        return saga;
    }

    public void setEntries(List<LedgerEntryResponse> entries) {
        this.entries = entries;
    }

    public void setSaga(SagaSummaryResponse saga) {
        this.saga = saga;
    }
}
