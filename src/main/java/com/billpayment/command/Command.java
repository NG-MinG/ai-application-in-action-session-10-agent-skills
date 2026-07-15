package com.billpayment.command;

public interface Command {
    String name();
    void execute(String[] args);
}
