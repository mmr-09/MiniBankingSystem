package models;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String accountNumber;
    private String type;
    private double amount;
    private String description;
    private Date date;

    public Transaction(int id, String accountNumber, String type, double amount, String description, Date date) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public Transaction(String type, double amount, String description) {
        this(0, null, type, amount, description, new Date());
    }

    @Override
    public String toString() {
        return type + ": " + amount + " (" + description + ") tại " + date;
    }

    public int getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }
}