package com.billpayment.command;

import com.billpayment.model.Bill;
import com.billpayment.model.PaymentRecord;

import java.time.format.DateTimeFormatter;

public final class CommandFormatter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CommandFormatter() {
    }

    public static String formatBill(Bill bill) {
        return String.format(
            "id: %d | type: %s | amount: %d | due: %s | status: %s | provider: %s (%s)",
            bill.getId(),
            bill.getType(),
            bill.getAmount(),
            DATE_FORMATTER.format(bill.getDueDate()),
            bill.getState(),
            bill.getProviderCode(),
            bill.getProviderName()
        );
    }

    public static String formatPayment(PaymentRecord record) {
        return String.format(
            "id: %d | billId: %d | amount: %d | date: %s | state: %s",
            System.identityHashCode(record),
            record.getBillId(),
            record.getAmount(),
            DATE_FORMATTER.format(record.getScheduledDate()),
            record.getState()
        );
    }
}
