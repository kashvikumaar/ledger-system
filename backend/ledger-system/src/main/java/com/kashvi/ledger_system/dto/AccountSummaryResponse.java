package com.kashvi.ledger_system.dto;

public class AccountSummaryResponse {

    private Long id;
    private String name;
    private String type;
    private String currency;
    private Long balance;

    public AccountSummaryResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public Long getBalance() {
        return balance;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
