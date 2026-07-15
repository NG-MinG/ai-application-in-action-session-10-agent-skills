package com.billpayment.model;

import java.time.LocalDate;

public class Bill {
    private long id;
    private String phone;
    private String providerCode;
    private String providerName;
    private BillType type;
    private long amount;
    private LocalDate dueDate;
    private BillState state = BillState.NOT_PAID;

    public Bill(long id, String phone, String providerCode, String providerName, BillType type, long amount, LocalDate dueDate) {
        this.id = id;
        this.phone = phone;
        this.providerCode = providerCode;
        this.providerName = providerName;
        this.type = type;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public long getId() { return id; }
    public String getPhone() { return phone; }
    public String getProviderCode() { return providerCode; }
    public String getProviderName() { return providerName; }
    public BillType getType() { return type; }
    public long getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
    public BillState getState() { return state; }

    public void setAmount(long amount) { this.amount = amount; }
    public void setState(BillState state) { this.state = state; }
}
