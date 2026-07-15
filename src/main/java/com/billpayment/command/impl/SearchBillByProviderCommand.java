package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandFormatter;
import com.billpayment.command.CommandName;

public class SearchBillByProviderCommand implements Command {
    private final CommandContext context;

    public SearchBillByProviderCommand(CommandContext context) {
        this.context = context;
    }

    @Override
    public String name() {
        return CommandName.SEARCH_BILL_BY_PROVIDER.name();
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            return;
        }
        var results = context.getService().searchBillsByProvider(args[0]);
        if (results.isEmpty()) {
            System.out.println("Not found bills matching provider: " + args[0]);
        } else {
            results.stream()
                    .map(CommandFormatter::formatBill)
                    .forEach(System.out::println);
        }
    }
}
