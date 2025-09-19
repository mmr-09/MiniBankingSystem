package rmi.server;

import interfaces.IServices;
import models.Account;
import models.Transaction;
import models.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class Services extends UnicastRemoteObject implements IServices {
    private Connection conn;

    public Services() throws RemoteException {
        try {
            conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RemoteException("Lỗi kết nối database: " + e.getMessage());
        }
    }

    @Override
    public User login(String username, String password) throws RemoteException {
        try {
            if (username.equals("admin") && password.equals("admin123")) {
                return new User(username, password, "Admin");
            }
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new User(rs.getString("username"), rs.getString("password"), rs.getString("role"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
    }

    @Override
    public void register(String username, String password, String role) throws RemoteException {
        try {
            // Kiểm tra xem username đã tồn tại chưa
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RemoteException("Tên đăng nhập đã tồn tại.");
                }
            }

            // Thêm người dùng vào bảng users
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, role);
                stmt.executeUpdate();
            }

            // Tự động tạo tài khoản ngân hàng mặc định cho người dùng
            String accountNumber = String.valueOf(System.currentTimeMillis());
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO accounts (account_number, username, owner_name, balance) VALUES (?, ?, ?, 0)")) {
                stmt.setString(1, accountNumber);
                stmt.setString(2, username);
                stmt.setString(3, username);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
    }

    @Override
    public void createAccount(String username, String accountNumber, String ownerName) throws RemoteException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO accounts (account_number, username, owner_name, balance) VALUES (?, ?, ?, 0)")) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, username);
            stmt.setString(3, ownerName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
    }

    @Override
    public void deleteAccount(String accountNumber) throws RemoteException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM accounts WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Account> getAllAccounts() throws RemoteException {
        ArrayList<Account> accounts = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM accounts")) {
            while (rs.next()) {
                accounts.add(new Account(rs.getString("account_number"), rs.getString("owner_name"), rs.getString("username"), rs.getDouble("balance")));
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
        return accounts;
    }

    @Override
    public void deposit(String accountNumber, double amount) throws RemoteException {
        try {
            // Kiểm tra số tiền hợp lệ
            if (amount <= 0) {
                throw new RemoteException("Số tiền nạp phải lớn hơn 0.");
            }
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?")) {
                stmt.setDouble(1, amount);
                stmt.setString(2, accountNumber);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RemoteException("Tài khoản không tồn tại.");
                }
                // Ghi lại giao dịch
                try (PreparedStatement transStmt = conn.prepareStatement(
                        "INSERT INTO transactions (account_number, type, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?)")) {
                    transStmt.setString(1, accountNumber);
                    transStmt.setString(2, "Nạp tiền");
                    transStmt.setDouble(3, amount);
                    transStmt.setString(4, "Nạp tiền vào tài khoản");
                    transStmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                    transStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
    }

    @Override
    public void withdraw(String accountNumber, double amount) throws RemoteException {
        try {
            // Kiểm tra số dư
            double balance = getBalance(accountNumber);
            if (balance < amount) {
                throw new RemoteException("Số dư không đủ.");
            }
            if (amount <= 0) {
                throw new RemoteException("Số tiền rút phải lớn hơn 0.");
            }
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_number = ?")) {
                stmt.setDouble(1, amount);
                stmt.setString(2, accountNumber);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RemoteException("Tài khoản không tồn tại.");
                }
                // Ghi lại giao dịch
                try (PreparedStatement transStmt = conn.prepareStatement(
                        "INSERT INTO transactions (account_number, type, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?)")) {
                    transStmt.setString(1, accountNumber);
                    transStmt.setString(2, "Rút tiền");
                    transStmt.setDouble(3, amount);
                    transStmt.setString(4, "Rút tiền từ tài khoản");
                    transStmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                    transStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) throws RemoteException {
        try {
            conn.setAutoCommit(false); // Bắt đầu giao dịch
            // Kiểm tra số dư tài khoản nguồn
            double balance = getBalance(fromAccountNumber);
            if (balance < amount) {
                throw new RemoteException("Số dư không đủ.");
            }
            if (amount <= 0) {
                throw new RemoteException("Số tiền chuyển phải lớn hơn 0.");
            }
            // Rút tiền từ tài khoản nguồn
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_number = ?")) {
                stmt.setDouble(1, amount);
                stmt.setString(2, fromAccountNumber);
                stmt.executeUpdate();
            }
            // Nạp tiền vào tài khoản đích
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?")) {
                stmt.setDouble(1, amount);
                stmt.setString(2, toAccountNumber);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RemoteException("Tài khoản đích không tồn tại.");
                }
            }
            // Ghi lại giao dịch
            try (PreparedStatement transStmt = conn.prepareStatement(
                    "INSERT INTO transactions (account_number, type, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?)")) {
                transStmt.setString(1, fromAccountNumber);
                transStmt.setString(2, "Chuyển tiền");
                transStmt.setDouble(3, amount);
                transStmt.setString(4, "Chuyển tiền đến " + toAccountNumber);
                transStmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                transStmt.executeUpdate();
            }
            conn.commit(); // Hoàn tất giao dịch
        } catch (SQLException e) {
            try {
                conn.rollback(); // Hoàn tác nếu có lỗi
            } catch (SQLException rollbackEx) {
                throw new RemoteException("Lỗi rollback: " + rollbackEx.getMessage());
            }
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RemoteException("Lỗi reset auto-commit: " + e.getMessage());
            }
        }
    }

    @Override
    public double getBalance(String accountNumber) throws RemoteException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM accounts WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
            throw new RemoteException("Tài khoản không tồn tại.");
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Transaction> getTransactions(String accountNumber) throws RemoteException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM transactions WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getTimestamp("transaction_date") != null ? new Date(rs.getTimestamp("transaction_date").getTime()) : new Date();
                    transactions.add(new Transaction(
                            rs.getInt("id"),
                            rs.getString("account_number"),
                            rs.getString("type"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            date
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public int getAccountCount() throws RemoteException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM accounts")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
    }

    @Override
    public double getTotalFunds() throws RemoteException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(balance) FROM accounts")) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Transaction> getAllTransactions() throws RemoteException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM transactions")) {
            while (rs.next()) {
                Date date = rs.getTimestamp("transaction_date") != null ? new Date(rs.getTimestamp("transaction_date").getTime()) : new Date();
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getString("account_number"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        date
                ));
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi SQL: " + e.getMessage());
        }
        return transactions;
    }
}