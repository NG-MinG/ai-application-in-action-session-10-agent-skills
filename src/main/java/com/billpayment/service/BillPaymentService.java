package com.billpayment.service;

import com.billpayment.model.Bill;
import com.billpayment.model.BillState;
import com.billpayment.model.BillType;
import com.billpayment.model.Account;
import com.billpayment.model.PaymentRecord;
import com.billpayment.model.PaymentState;
import com.billpayment.util.Constants;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class BillPaymentService {
    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<Long, Bill> bills = new HashMap<>();
    private final List<PaymentRecord> paymentHistory = new ArrayList<>();
    private long nextBillId = 1;

    public void registerAccount(String phone) {
        accounts.putIfAbsent(phone, new Account());
    }

    public void cashIn(String phone, long amount, LocalDate date) {
        YearMonth ym = YearMonth.from(date);
        Account account = accounts.computeIfAbsent(phone, ignored -> new Account());
        long total = account.getMonthlyCashIn().getOrDefault(ym, 0L) + amount;
        if (total > Constants.MONTHLY_CASH_IN_LIMIT) {
            throw new IllegalStateException(Constants.CASH_IN_EXCEEDS_MONTHLY_LIMIT);
        }
        account.addCashIn(ym, amount);
    }

    public long getAccountBalance(String phone) {
        return accounts.getOrDefault(phone, new Account()).getBalance();
    }

    public long getMonthlyCashInTotal(String phone, YearMonth month) {
        return accounts.getOrDefault(phone, new Account()).getMonthlyCashIn().getOrDefault(month, 0L);
    }

    public void createBill(Bill bill) {
        bills.put(bill.getId(), bill);
        nextBillId = Math.max(nextBillId, bill.getId() + 1);
    }

    public long createBillWithAutoId(String phone, String providerCode, String providerName, BillType type, long amount, LocalDate dueDate) {
        long billId = nextBillId++;
        Bill bill = new Bill(billId, phone, providerCode, providerName, type, amount, dueDate);
        bills.put(billId, bill);
        return billId;
    }

    public List<Bill> searchBillsByProvider(String token) {
        String normalized = token.toLowerCase();
        return bills.values().stream()
                .filter(bill -> bill.getProviderCode().toLowerCase().contains(normalized)
                        || bill.getProviderName().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }

    public List<Bill> searchBillsByBillId(long id) {
        Bill b = bills.get(id);
        if (b == null) return Collections.emptyList();
        return List.of(b);
    }

    public List<Bill> searchBillsByPhone(String phone) {
        return bills.values().stream()
                .filter(bill -> bill.getPhone().equals(phone))
                .collect(Collectors.toList());
    }

    public void updateBill(Bill bill) {
        Bill existing = bills.get(bill.getId());
        if (existing != null) {
            existing.setAmount(bill.getAmount());
            existing.setState(bill.getState());
            bills.put(existing.getId(), existing);
        }
    }

    public Bill findBillById(long id) {
        return bills.get(id);
    }

    public boolean deleteBill(long id) {
        return bills.remove(id) != null;
    }

    public List<Bill> listAllBills() {
        return new ArrayList<>(bills.values());
    }

    public List<Bill> listBillsByAccountPhone(String phone) {
        return searchBillsByPhone(phone);
    }

    public void payBills(String accountPhone, List<Long> billIds, LocalDate date) {
        List<Bill> selectedBills = billIds.stream()
                .map(bills::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Bill::getDueDate))
                .toList();

        long total = selectedBills.stream().mapToLong(Bill::getAmount).sum();
        long balance = getAccountBalance(accountPhone);
        if (balance < total) throw new IllegalStateException(Constants.INSUFFICIENT_BALANCE);

        Account account = accounts.computeIfAbsent(accountPhone, ignored -> new Account());
        for (Bill b : selectedBills) {
            account.deduct(b.getAmount());
            b.setState(BillState.PAID);
            bills.put(b.getId(), b);
            paymentHistory.add(new PaymentRecord(b.getId(), accountPhone, b.getAmount(), PaymentState.PROCESSED, date));
        }
    }

    public void schedulePayment(String accountPhone, long billId, LocalDate scheduledDate) {
        Bill b = bills.get(billId);
        if (b == null) throw new IllegalStateException(Constants.BILL_NOT_FOUND);
        paymentHistory.add(new PaymentRecord(billId, accountPhone, b.getAmount(), PaymentState.SCHEDULED, scheduledDate));
    }

    public void processScheduledPayments(LocalDate date) {
        List<PaymentRecord> pending = paymentHistory.stream()
                .filter(r -> r.getState() == PaymentState.SCHEDULED && date.equals(r.getScheduledDate()))
                .toList();
        for (PaymentRecord r : pending) {
            long balance = getAccountBalance(r.getAccountPhone());
            if (balance >= r.getAmount()) {
                accounts.computeIfAbsent(r.getAccountPhone(), ignored -> new Account()).deduct(r.getAmount());
                r.setState(PaymentState.PROCESSED);
                Bill b = bills.get(r.getBillId());
                if (b != null) {
                    b.setState(BillState.PAID);
                    bills.put(b.getId(), b);
                }
            }
        }
    }

    public List<PaymentRecord> getPaymentHistory() {
        return paymentHistory;
    }
}
