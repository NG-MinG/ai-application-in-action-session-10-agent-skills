package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandFormatter;
import com.billpayment.command.CommandName;

public class ListPaymentCommand implements Command {
    private final CommandContext context;

    public ListPaymentCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.LIST_PAYMENT.name();
    }

    @Override
    public void execute(String[] args) {
        var payments = context.getService().getPaymentHistory();
        if (payments.isEmpty()) {
            System.out.println("No payment history found.");
        } else {
            payments.stream()
                    .map(CommandFormatter::formatPayment)
                    .forEach(System.out::println);
        }
    }
}
