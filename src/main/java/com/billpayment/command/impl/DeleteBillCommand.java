package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandName;

public class DeleteBillCommand implements Command {
    private final CommandContext context;

    public DeleteBillCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.DELETE_BILL.name();
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            return;
        }
        long billId = Long.parseLong(args[0]);
        boolean deleted = context.getService().deleteBill(billId);
        System.out.println(deleted ? "Bill has been deleted." : "Bill not found.");
    }
}
