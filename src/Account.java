package models;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    private String ownerName;
    private String username;
    private double balance;

    public Account(String accountNumber, String ownerName, String username, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.username = username;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account: " + accountNumber + ", Owner: " + ownerName + ", Balance: " + balance;
    }
}