package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandName;
import com.billpayment.util.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ScheduleCommand implements Command {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final CommandContext context;

    public ScheduleCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.SCHEDULE.name();
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            return;
        }
        long billId = Long.parseLong(args[0]);
        LocalDate scheduledDate = LocalDate.parse(args[1], DATE_FORMATTER);
        context.getService().schedulePayment(context.getActivePhone(), billId, scheduledDate);
        System.out.println(String.format(Constants.PAYMENT_SCHEDULED, billId, args[1]));
    }
}
