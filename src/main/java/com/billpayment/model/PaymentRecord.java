package com.billpayment.model;

import java.time.LocalDate;

public class PaymentRecord {
    private final long billId;
    private final String accountPhone;
    private final long amount;
    private PaymentState state;
    private final LocalDate scheduledDate;

    public PaymentRecord(long billId, String accountPhone, long amount, PaymentState state, LocalDate scheduledDate) {
        this.billId = billId;
        this.accountPhone = accountPhone;
        this.amount = amount;
        this.state = state;
        this.scheduledDate = scheduledDate;
    }

    public long getBillId() { return billId; }
    public String getAccountPhone() { return accountPhone; }
    public long getAmount() { return amount; }
    public PaymentState getState() { return state; }
    public void setState(PaymentState state) { this.state = state; }
    public LocalDate getScheduledDate() { return scheduledDate; }
}
