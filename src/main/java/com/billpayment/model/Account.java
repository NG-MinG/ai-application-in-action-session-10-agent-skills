package com.billpayment.model;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class Account {
    private long id;
    private long balance;
    private AccountStatus status;
    private final Map<YearMonth, Long> monthlyCashIn = new HashMap<>();

    public Account() {
        this.status = AccountStatus.ACTIVE;
    }

    public Account(long id, long balance, AccountStatus status) {
        this.id = id;
        this.balance = balance;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Map<YearMonth, Long> getMonthlyCashIn() {
        return monthlyCashIn;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void addCashIn(YearMonth month, long amount) {
        monthlyCashIn.put(month, monthlyCashIn.getOrDefault(month, 0L) + amount);
        balance += amount;
    }

    public void deduct(long amount) {
        balance -= amount;
    }
}
