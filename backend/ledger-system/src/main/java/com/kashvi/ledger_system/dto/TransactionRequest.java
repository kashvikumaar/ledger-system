package com.kashvi.ledger_system.dto;

import java.util.List;

public class TransactionRequest {

    private List<EntryRequest> entries;

    public TransactionRequest() {
    }

    public List<EntryRequest> getEntries() {
        return entries;
    }

    public void setEntries(List<EntryRequest> entries) {
        this.entries = entries;
    }
}