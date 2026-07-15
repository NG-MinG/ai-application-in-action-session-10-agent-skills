package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandName;
import com.billpayment.model.BillType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CreateBillCommand implements Command {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final CommandContext context;

    public CreateBillCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.CREATE_BILL.name();
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 5) {
            System.out.println("Usage: CREATE_BILL <type> <amount> <dueDate> <providerCode> <providerName>");
            return;
        }
        try {
            BillType type = BillType.valueOf(args[0].toUpperCase());
            long amount = Long.parseLong(args[1]);
            LocalDate dueDate = LocalDate.parse(args[2], DATE_FORMATTER);
            String providerCode = args[3];
            String providerName = args[4];
            
            long billId = context.getService().createBillWithAutoId(
                context.getActivePhone(), providerCode, providerName, type, amount, dueDate);
            System.out.println("Bill created successfully with id: " + billId);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid bill type. Supported types: ELECTRIC, WATER, INTERNET, OTHER");
        } catch (Exception e) {
            System.out.println("Error creating bill: " + e.getMessage());
        }
    }
}
