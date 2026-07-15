package com.billpayment.command;

import com.billpayment.service.BillPaymentService;

public class CommandContext {
    private final BillPaymentService service;
    private String activePhone;

    public CommandContext(BillPaymentService service) {
        this.service = service;
    }

    public BillPaymentService getService() {
        return service;
    }

    public String getActivePhone() {
        return activePhone;
    }

    public void setActivePhone(String activePhone) {
        this.activePhone = activePhone;
    }
}
