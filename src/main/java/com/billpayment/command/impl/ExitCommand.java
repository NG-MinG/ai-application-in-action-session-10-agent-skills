package com.billpayment.command.impl;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandName;

public class ExitCommand implements Command {
    public ExitCommand(CommandContext context) {
    }

    @Override
    public String name() {
        return CommandName.EXIT.name();
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Goodbye!");
        System.exit(0);
    }
}
