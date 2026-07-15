package com.billpayment.command;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public void register(Command c) {
        commands.put(c.name(), c);
    }

    public Command get(String name) {
        return commands.get(name);
    }
}
