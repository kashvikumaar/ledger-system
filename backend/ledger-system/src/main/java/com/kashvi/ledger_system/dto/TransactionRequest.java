package com.kashvi.ledger_system.dto;

import java.util.List;

public class TransactionRequest {

    private String externalTransactionReference;

    private List<EntryRequest> entries;

    public TransactionRequest() {
    }

    public List<EntryRequest> getEntries() {
        return entries;
    }

    public String getExternalTransactionReference() {
        return externalTransactionReference;
    }

    public void setExternalTransactionReference(
            String externalTransactionReference
    ) {
        this.externalTransactionReference = externalTransactionReference;
    }

    public void setEntries(List<EntryRequest> entries) {
        this.entries = entries;
    }
}
