package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandName;
import com.billpayment.service.BillPaymentService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CashInCommand implements Command {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final CommandContext context;

    public CashInCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.CASH_IN.name();
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            return;
        }
        BillPaymentService service = context.getService();
        String phone = context.getActivePhone();
        long amount = Long.parseLong(args[0]);
        LocalDate date = args.length > 1 ? LocalDate.parse(args[1], DATE_FORMATTER) : LocalDate.now();
        service.cashIn(phone, amount, date);
        System.out.println("Your available balance: " + service.getAccountBalance(phone));
    }
}
