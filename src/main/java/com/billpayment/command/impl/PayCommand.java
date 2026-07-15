package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandName;
import com.billpayment.model.Bill;
import com.billpayment.util.Constants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PayCommand implements Command {
    private final CommandContext context;

    public PayCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.PAY.name();
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            return;
        }
        List<Long> billIds = new ArrayList<>();
        for (String arg : args) {
            billIds.add(Long.parseLong(arg));
        }

        List<Bill> billsToPay = billIds.stream()
                .map(context.getService()::findBillById)
                .sorted(Comparator.comparing(Bill::getDueDate))
                .toList();

        for (Bill bill : billsToPay) {
            if (bill == null) {
                System.out.println(Constants.BILL_NOT_FOUND_BY_ID);
                return;
            }
        }

        try {
            context.getService().payBills(context.getActivePhone(), billsToPay.stream().map(Bill::getId).toList(), LocalDate.now());
        } catch (IllegalStateException exception) {
            System.out.println(Constants.NOT_ENOUGH_FUND);
            return;
        }

        for (Bill bill : billsToPay) {
            long billId = bill.getId();
            System.out.println(String.format(Constants.PAYMENT_COMPLETED, billId));
        }
        System.out.println("Your current balance is: " + context.getService().getAccountBalance(context.getActivePhone()));
    }
}
