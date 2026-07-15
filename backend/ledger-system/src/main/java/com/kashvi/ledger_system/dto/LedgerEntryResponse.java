package com.kashvi.ledger_system.dto;

import java.time.LocalDateTime;

public class LedgerEntryResponse {

    private Long id;
    private Long accountId;
    private String accountName;
    private String type;
    private Long amount;
    private LocalDateTime createdAt;

    public LedgerEntryResponse() {
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getType() {
        return type;
    }

    public Long getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
