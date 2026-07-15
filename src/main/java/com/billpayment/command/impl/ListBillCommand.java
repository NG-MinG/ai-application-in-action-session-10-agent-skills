package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandFormatter;
import com.billpayment.command.CommandName;

import java.util.Comparator;

public class ListBillCommand implements Command {
    private final CommandContext context;

    public ListBillCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.LIST_BILL.name();
    }

    @Override
    public void execute(String[] args) {
        var bills = context.getService().listAllBills();
        if (bills.isEmpty()) {
            System.out.println("No bills found.");
        } else {
            bills.stream()
                    .sorted(Comparator.comparingLong(bill -> bill.getId()))
                    .map(CommandFormatter::formatBill)
                    .forEach(System.out::println);
        }
    }
}
