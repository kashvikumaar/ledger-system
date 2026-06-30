package com.kashvi.ledger_system.dto;

public class EntryRequest {

    private Long accountId;
    private String type;
    private Long amount;

    public EntryRequest() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getType() {
        return type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}