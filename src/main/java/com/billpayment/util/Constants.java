package com.billpayment.util;

public final class Constants {
    private Constants() {
    }

    public static final long MONTHLY_CASH_IN_LIMIT = 100_000_000L;
    public static final String CASH_IN_EXCEEDS_MONTHLY_LIMIT = "Cash in exceeds monthly limit";
    public static final String INSUFFICIENT_BALANCE = "Insufficient balance";
    public static final String BILL_NOT_FOUND = "Bill not found";
    public static final String BILL_NOT_FOUND_BY_ID = "Not found a bill with such id";
    public static final String NOT_ENOUGH_FUND = "Not enough fund to proceed with payment.";
    public static final String PAYMENT_COMPLETED = "Payment has been completed for Bill with id %d.";
    public static final String PAYMENT_SCHEDULED = "Payment for bill id %d is scheduled on %s";
}
