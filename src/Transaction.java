package models;

import java.io.Serializable;
import java.util.Date;

/**
 * Transaction model có hỗ trợ cả constructor chứa id (dùng khi đọc từ DB)
 * và constructor không chứa id (dùng khi tạo tạm/local).
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    // giữ id nhưng không bắt buộc phải dùng
    private int id;
    private String accountNumber;
    private String type;
    private double amount;
    private String description;
    private Date date;

    // Constructor dùng khi đọc từ DB (có id)
    public Transaction(int id, String accountNumber, String type, double amount, String description, Date date) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Constructor không id (tùy chọn)
    public Transaction(String accountNumber, String type, double amount, String description, Date date) {
        this(0, accountNumber, type, amount, description, date);
    }

    // getters + optional setter cho id nếu cần
    public int getId() {
        return id;
    }

    // nếu cần set id sau khi insert (thường DB auto_increment), có thể dùng setter:
    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "[" + id + "] " + type + ": " + amount + " (" + description + ") tại " + date;
    }
}
