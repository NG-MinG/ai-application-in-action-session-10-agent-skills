package com.billpayment;

import com.billpayment.command.Command;
import com.billpayment.command.CommandContext;
import com.billpayment.command.CommandRegistry;
import com.billpayment.command.CommandName;
import com.billpayment.command.impl.CashInCommand;
import com.billpayment.command.impl.CreateBillCommand;
import com.billpayment.command.impl.DeleteBillCommand;
import com.billpayment.command.impl.DueDateCommand;
import com.billpayment.command.impl.ExitCommand;
import com.billpayment.command.impl.ListBillCommand;
import com.billpayment.command.impl.ListPaymentCommand;
import com.billpayment.command.impl.PayCommand;
import com.billpayment.command.impl.ScheduleCommand;
import com.billpayment.command.impl.SearchBillByIdCommand;
import com.billpayment.command.impl.SearchBillByPhoneCommand;
import com.billpayment.command.impl.SearchBillByProviderCommand;
import com.billpayment.model.Bill;
import com.billpayment.model.BillType;
import com.billpayment.service.BillPaymentService;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final String DEFAULT_PHONE = "0901000001";

    public static void main(String[] args) {
        BillPaymentService service = new BillPaymentService();
        CommandContext context = new CommandContext(service);
        context.setActivePhone(DEFAULT_PHONE);

        service.registerAccount(DEFAULT_PHONE);
        service.cashIn(DEFAULT_PHONE, 0, LocalDate.of(2026, 5, 10));
        service.createBill(new Bill(1, DEFAULT_PHONE, "EVN", "EVN HCMC", BillType.ELECTRIC, 200_000, LocalDate.of(2026, 10, 25)));
        service.createBill(new Bill(2, DEFAULT_PHONE, "SAVACO", "SAVACO HCMC", BillType.WATER, 175_000, LocalDate.of(2026, 10, 30)));
        service.createBill(new Bill(3, DEFAULT_PHONE, "VNPT", "VNPT", BillType.INTERNET, 800_000, LocalDate.of(2026, 11, 30)));

        CommandRegistry registry = new CommandRegistry();
        registry.register(new CashInCommand(context));
        registry.register(new CreateBillCommand(context));
        registry.register(new DeleteBillCommand(context));
        registry.register(new DueDateCommand(context));
        registry.register(new ExitCommand(context));
        registry.register(new ListBillCommand(context));
        registry.register(new ListPaymentCommand(context));
        registry.register(new PayCommand(context));
        registry.register(new ScheduleCommand(context));
        registry.register(new SearchBillByIdCommand(context));
        registry.register(new SearchBillByPhoneCommand(context));
        registry.register(new SearchBillByProviderCommand(context));

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] tokens = line.split("\\s+");
                String commandName = tokens[0].toUpperCase();
                if (commandName.equals(CommandName.EXIT.name())) {
                    Command exitCommand = registry.get(commandName);
                    if (exitCommand != null) {
                        exitCommand.execute(new String[0]);
                    }
                    break;
                }

                Command command = registry.get(commandName);
                if (command == null) {
                    System.out.println("Unknown command: " + tokens[0]);
                    continue;
                }

                String[] commandArgs = new String[tokens.length - 1];
                System.arraycopy(tokens, 1, commandArgs, 0, commandArgs.length);
                command.execute(commandArgs);
            }
        }
    }
}
