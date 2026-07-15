package com.billpayment.model;

import java.time.LocalDate;

public class User {
    private long id;
    private String fullname;
    private String phone;
    private String email;
    private LocalDate dob;
    private Account paymentAccount;

    public User(long id, String fullname, String phone, String email, LocalDate dob, Account paymentAccount) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.dob = dob;
        this.paymentAccount = paymentAccount;
    }

    public long getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public Account getPaymentAccount() {
        return paymentAccount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setPaymentAccount(Account paymentAccount) {
        this.paymentAccount = paymentAccount;
    }
}
