package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandFormatter;
import com.billpayment.command.CommandName;

public class SearchBillByIdCommand implements Command {
    private final CommandContext context;

    public SearchBillByIdCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.SEARCH_BILL_BY_ID.name();
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            return;
        }
        long billId = Long.parseLong(args[0]);
        var results = context.getService().searchBillsByBillId(billId);
        if (results.isEmpty()) {
            System.out.println("Not found a bill with such id");
        } else {
            results.stream()
                    .map(CommandFormatter::formatBill)
                    .forEach(System.out::println);
        }
    }
}
