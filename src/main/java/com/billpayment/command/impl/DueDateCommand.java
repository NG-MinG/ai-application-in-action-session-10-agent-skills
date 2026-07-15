package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandFormatter;
import com.billpayment.command.CommandName;
import com.billpayment.model.BillState;

import java.util.Comparator;

public class DueDateCommand implements Command {
    private final CommandContext context;

    public DueDateCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.DUE_DATE.name();
    }

    @Override
    public void execute(String[] args) {
        var bills = context.getService().listAllBills().stream()
                .filter(bill -> bill.getState() == BillState.NOT_PAID)
                .sorted(Comparator.comparing(bill -> bill.getDueDate()))
                .toList();
        if (bills.isEmpty()) {
            System.out.println("No unpaid bills to display.");
        } else {
            bills.stream()
                    .map(CommandFormatter::formatBill)
                    .forEach(System.out::println);
        }
    }
}
