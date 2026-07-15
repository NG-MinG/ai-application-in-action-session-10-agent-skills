package com.billpayment.service;

import com.billpayment.model.Bill;
import com.billpayment.model.BillState;
import com.billpayment.model.BillType;
import com.billpayment.model.PaymentState;
import com.billpayment.util.Constants;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;

class BillPaymentServiceTest {

    @Test
    void cashInAllowsExactMonthlyLimitAndRejectsOverflow() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");

        service.cashIn("0901000001", 100_000_000, LocalDate.of(2026, 5, 10));

        assertEquals(100_000_000, service.getAccountBalance("0901000001"));
        assertEquals(100_000_000, service.getMonthlyCashInTotal("0901000001", YearMonth.of(2026, 5)));
    }

    @Test
    void cashInRejectsWhenMonthlyLimitWouldOverflow() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");

        service.cashIn("0901000001", 100_000_000, LocalDate.of(2026, 5, 10));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.cashIn("0901000001", 1, LocalDate.of(2026, 5, 11))
        );

        assertEquals(Constants.CASH_IN_EXCEEDS_MONTHLY_LIMIT, exception.getMessage());
        assertEquals(100_000_000, service.getAccountBalance("0901000001"));
        assertEquals(100_000_000, service.getMonthlyCashInTotal("0901000001", YearMonth.of(2026, 5)));
    }

    @Test
    void searchBillsByProviderCodeIsCaseInsensitive() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);

        assertEquals(List.of(1L), service.searchBillsByProvider("evn").stream().map(Bill::getId).toList());
    }

    @Test
    void searchBillsByProviderNameIsCaseInsensitive() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);

        assertEquals(List.of(1L, 2L), service.searchBillsByProvider("hcmc").stream().map(Bill::getId).toList());
    }

    @Test
    void searchBillsByBillIdReturnsOnlyMatchingBill() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);

        assertEquals(List.of(3L), service.searchBillsByBillId(3L).stream().map(Bill::getId).toList());
    }

    @Test
    void searchBillsByPhoneReturnsOnlyLinkedBills() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);

        assertEquals(List.of(1L, 2L), service.searchBillsByPhone("0901000001").stream().map(Bill::getId).toList());
    }

    @Test
    void updateBillChangesAmountWithoutChangingState() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);

        service.updateBill(new Bill(1, "0901000001", "EVN", "EVN HCMC", BillType.ELECTRIC, 220_000, LocalDate.of(2026, 10, 26)));

        assertEquals(220_000, service.findBillById(1).getAmount());
        assertEquals(BillState.NOT_PAID, service.findBillById(1).getState());
    }

    @Test
    void deleteBillRemovesExistingBill() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);

        assertTrue(service.deleteBill(2));
        assertEquals(List.of(1L), service.listBillsByAccountPhone("0901000001").stream().map(Bill::getId).toList());
    }

    @Test
    void listBillsByAccountPhoneOnlyReturnsBillsOfThatAccount() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);

        assertEquals(List.of(1L, 2L), service.listBillsByAccountPhone("0901000001").stream().map(Bill::getId).toList());
        assertEquals(List.of(3L), service.listBillsByAccountPhone("0901000002").stream().map(Bill::getId).toList());
    }

    @Test
    void payBillsPrioritizesEarliestDueDate() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        service.cashIn("0901000001", 1_000_000, LocalDate.of(2026, 5, 10));

        service.createBill(new Bill(1, "0901000001", "EVN", "EVN HCMC", BillType.ELECTRIC, 200_000, LocalDate.of(2026, 11, 25)));
        service.createBill(new Bill(2, "0901000001", "SAVACO", "SAVACO HCMC", BillType.WATER, 175_000, LocalDate.of(2026, 10, 30)));
        service.createBill(new Bill(3, "0901000001", "VNPT", "VNPT", BillType.INTERNET, 100_000, LocalDate.of(2026, 11, 5)));

        service.payBills("0901000001", List.of(1L, 3L, 2L), LocalDate.of(2026, 10, 28));

        assertEquals(List.of(2L, 3L, 1L), service.getPaymentHistory().stream().map(record -> record.getBillId()).toList());
    }

    @Test
    void payBillsDeductsTotalAmountFromAccountBalance() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        service.cashIn("0901000001", 1_000_000, LocalDate.of(2026, 5, 10));

        service.createBill(new Bill(1, "0901000001", "EVN", "EVN HCMC", BillType.ELECTRIC, 200_000, LocalDate.of(2026, 11, 25)));
        service.createBill(new Bill(2, "0901000001", "SAVACO", "SAVACO HCMC", BillType.WATER, 175_000, LocalDate.of(2026, 10, 30)));
        service.createBill(new Bill(3, "0901000001", "VNPT", "VNPT", BillType.INTERNET, 100_000, LocalDate.of(2026, 11, 5)));

        service.payBills("0901000001", List.of(1L, 3L, 2L), LocalDate.of(2026, 10, 28));

        assertEquals(525_000, service.getAccountBalance("0901000001"));
    }

    @Test
    void payBillsMarksAllSelectedBillsAsPaid() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        service.cashIn("0901000001", 1_000_000, LocalDate.of(2026, 5, 10));

        service.createBill(new Bill(1, "0901000001", "EVN", "EVN HCMC", BillType.ELECTRIC, 200_000, LocalDate.of(2026, 11, 25)));
        service.createBill(new Bill(2, "0901000001", "SAVACO", "SAVACO HCMC", BillType.WATER, 175_000, LocalDate.of(2026, 10, 30)));
        service.createBill(new Bill(3, "0901000001", "VNPT", "VNPT", BillType.INTERNET, 100_000, LocalDate.of(2026, 11, 5)));

        service.payBills("0901000001", List.of(1L, 3L, 2L), LocalDate.of(2026, 10, 28));

        assertEquals(BillState.PAID, service.findBillById(1).getState());
        assertEquals(BillState.PAID, service.findBillById(2).getState());
        assertEquals(BillState.PAID, service.findBillById(3).getState());
    }

    @Test
    void payBillsRejectsWhenBalanceIsInsufficient() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        service.cashIn("0901000001", 250_000, LocalDate.of(2026, 5, 10));

        service.createBill(new Bill(1, "0901000001", "EVN", "EVN HCMC", BillType.ELECTRIC, 200_000, LocalDate.of(2026, 10, 25)));
        service.createBill(new Bill(2, "0901000001", "SAVACO", "SAVACO HCMC", BillType.WATER, 175_000, LocalDate.of(2026, 10, 30)));

        assertThrows(IllegalStateException.class, () -> service.payBills("0901000001", List.of(1L, 2L), LocalDate.of(2026, 10, 28)));

        assertEquals(250_000, service.getAccountBalance("0901000001"));
        assertEquals(BillState.NOT_PAID, service.findBillById(1).getState());
        assertEquals(BillState.NOT_PAID, service.findBillById(2).getState());
        assertEquals(0, service.getPaymentHistory().size());
    }

    @Test
    void schedulePaymentStoresPendingRecord() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        service.cashIn("0901000001", 500_000, LocalDate.of(2026, 5, 10));
        service.createBill(new Bill(1, "0901000001", "VNPT", "VNPT", BillType.INTERNET, 200_000, LocalDate.of(2026, 11, 30)));

        service.schedulePayment("0901000001", 1L, LocalDate.of(2026, 10, 28));

        assertEquals(PaymentState.SCHEDULED, service.getPaymentHistory().get(0).getState());
        assertEquals(BillState.NOT_PAID, service.findBillById(1).getState());
    }

    @Test
    void processScheduledPaymentsSkipsBeforeExecuteDate() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        service.cashIn("0901000001", 500_000, LocalDate.of(2026, 5, 10));
        service.createBill(new Bill(1, "0901000001", "VNPT", "VNPT", BillType.INTERNET, 200_000, LocalDate.of(2026, 11, 30)));

        service.schedulePayment("0901000001", 1L, LocalDate.of(2026, 10, 28));

        service.processScheduledPayments(LocalDate.of(2026, 10, 27));

        assertEquals(PaymentState.SCHEDULED, service.getPaymentHistory().get(0).getState());
        assertEquals(BillState.NOT_PAID, service.findBillById(1).getState());
    }

    @Test
    void processScheduledPaymentsMarksBillPaidOnExecuteDate() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        service.cashIn("0901000001", 500_000, LocalDate.of(2026, 5, 10));
        service.createBill(new Bill(1, "0901000001", "VNPT", "VNPT", BillType.INTERNET, 200_000, LocalDate.of(2026, 11, 30)));

        service.schedulePayment("0901000001", 1L, LocalDate.of(2026, 10, 28));

        service.processScheduledPayments(LocalDate.of(2026, 10, 28));

        assertEquals(PaymentState.PROCESSED, service.getPaymentHistory().get(0).getState());
        assertEquals(BillState.PAID, service.findBillById(1).getState());
    }

    @Test
    void deleteBillReturnsFalseForMissingBill() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        assertFalse(service.deleteBill(99L));
    }

    @Test
    void createBillWithAutoIdGeneratesSequentialIds() {
        BillPaymentService service = new BillPaymentService();
        
        long id1 = service.createBillWithAutoId("0901000001", "EVN", "EVN", BillType.ELECTRIC, 100_000, LocalDate.of(2026, 10, 25));
        long id2 = service.createBillWithAutoId("0901000001", "VNPT", "VNPT", BillType.INTERNET, 200_000, LocalDate.of(2026, 10, 30));
        
        assertEquals(1, id1);
        assertEquals(2, id2);
    }

    @Test
    void searchBillsByProviderReturnsEmptyForNoMatch() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);
        
        assertTrue(service.searchBillsByProvider("xyz").isEmpty());
    }

    @Test
    void searchBillsByBillIdReturnsEmptyListForNonExistentId() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);
        
        assertTrue(service.searchBillsByBillId(99L).isEmpty());
    }

    @Test
    void searchBillsByPhoneReturnsEmptyForUnknownPhone() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);
        
        assertTrue(service.searchBillsByPhone("0901000003").isEmpty());
    }

    @Test
    void updateBillDoesNothingForNonExistentBill() {
        BillPaymentService service = new BillPaymentService();
        
        service.updateBill(new Bill(99, "0901000001", "EVN", "EVN", BillType.ELECTRIC, 100_000, LocalDate.now()));
        
        assertNull(service.findBillById(99));
    }

    @Test
    void findBillByIdReturnsNullForNonExistentBill() {
        BillPaymentService service = new BillPaymentService();
        
        assertNull(service.findBillById(99L));
    }

    @Test
    void listAllBillsReturnsAllBills() {
        BillPaymentService service = new BillPaymentService();
        createSampleBills(service);
        
        assertEquals(3, service.listAllBills().size());
    }

    @Test
    void listAllBillsReturnsEmptyWhenNoBills() {
        BillPaymentService service = new BillPaymentService();
        
        assertTrue(service.listAllBills().isEmpty());
    }

    @Test
    void schedulePaymentThrowsForNonExistentBill() {
        BillPaymentService service = new BillPaymentService();
        
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.schedulePayment("0901000001", 99L, LocalDate.of(2026, 10, 28))
        );
        
        assertEquals(Constants.BILL_NOT_FOUND, exception.getMessage());
    }

    @Test
    void processScheduledPaymentsHandlesMultipleSchedules() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        service.cashIn("0901000001", 500_000, LocalDate.of(2026, 5, 10));
        
        service.createBill(new Bill(1, "0901000001", "EVN", "EVN", BillType.ELECTRIC, 100_000, LocalDate.of(2026, 11, 30)));
        service.createBill(new Bill(2, "0901000001", "VNPT", "VNPT", BillType.INTERNET, 150_000, LocalDate.of(2026, 11, 30)));
        
        service.schedulePayment("0901000001", 1L, LocalDate.of(2026, 10, 28));
        service.schedulePayment("0901000001", 2L, LocalDate.of(2026, 10, 28));
        
        service.processScheduledPayments(LocalDate.of(2026, 10, 28));
        
        assertEquals(2, service.getPaymentHistory().size());
        assertEquals(PaymentState.PROCESSED, service.getPaymentHistory().get(0).getState());
        assertEquals(PaymentState.PROCESSED, service.getPaymentHistory().get(1).getState());
    }

    @Test
    void cashInAllowsSeparateMonthsIndependently() {
        BillPaymentService service = new BillPaymentService();
        service.registerAccount("0901000001");
        
        service.cashIn("0901000001", 100_000_000, LocalDate.of(2026, 5, 10));
        service.cashIn("0901000001", 100_000_000, LocalDate.of(2026, 6, 10));
        
        assertEquals(200_000_000, service.getAccountBalance("0901000001"));
        assertEquals(100_000_000, service.getMonthlyCashInTotal("0901000001", YearMonth.of(2026, 5)));
        assertEquals(100_000_000, service.getMonthlyCashInTotal("0901000001", YearMonth.of(2026, 6)));
    }

    @Test
    void createBillWithDuplicateIdUsesMaxId() {
        BillPaymentService service = new BillPaymentService();
        
        service.createBill(new Bill(10, "0901000001", "EVN", "EVN", BillType.ELECTRIC, 100_000, LocalDate.now()));
        long newId = service.createBillWithAutoId("0901000001", "VNPT", "VNPT", BillType.INTERNET, 200_000, LocalDate.now());
        
        assertEquals(11, newId);
    }

    private void createSampleBills(BillPaymentService service) {
        service.registerAccount("0901000001");
        service.registerAccount("0901000002");

        service.createBill(new Bill(1, "0901000001", "EVN", "EVN HCMC", BillType.ELECTRIC, 200_000, LocalDate.of(2026, 10, 25)));
        service.createBill(new Bill(2, "0901000001", "SAVACO", "SAVACO HCMC", BillType.WATER, 175_000, LocalDate.of(2026, 10, 30)));
        service.createBill(new Bill(3, "0901000002", "VNPT", "VNPT", BillType.INTERNET, 800_000, LocalDate.of(2026, 11, 30)));
    }
}
