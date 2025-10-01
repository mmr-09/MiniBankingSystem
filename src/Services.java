package rmi.server;

import interfaces.IServices;
import models.Account;
import models.Transaction;
import models.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;

public class Services extends UnicastRemoteObject implements IServices {
    private final Connection conn;

    public Services() throws RemoteException {
        super();
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RemoteException("Không kết nối được Database", e);
        }
    }

    // ================= User =================
    @Override
    public User login(String username, String password) throws RemoteException {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi login: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void register(String username, String password, String role) throws RemoteException {
        String sql = "INSERT INTO users(username,password,role) VALUES (?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Lỗi register: " + e.getMessage(), e);
        }
    }

    // ================= Account =================
    @Override
    public void createAccount(String username, String password, String role, String ownerName) throws RemoteException {
        try {
            conn.setAutoCommit(false);

            // Thêm user nếu chưa tồn tại
            String checkUser = "SELECT COUNT(*) FROM users WHERE username=?";
            try (PreparedStatement stmt = conn.prepareStatement(checkUser)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        String sqlUser = "INSERT INTO users(username,password,role) VALUES (?,?,?)";
                        try (PreparedStatement stmt2 = conn.prepareStatement(sqlUser)) {
                            stmt2.setString(1, username);
                            stmt2.setString(2, password);
                            stmt2.setString(3, role);
                            stmt2.executeUpdate();
                        }
                    }
                }
            }

            // Sinh account_number
            String accountNumber = String.valueOf(System.currentTimeMillis());

            String sqlAcc = "INSERT INTO accounts(account_number,username,owner_name,balance) VALUES(?,?,?,0)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlAcc)) {
                stmt.setString(1, accountNumber);
                stmt.setString(2, username);
                stmt.setString(3, ownerName);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RemoteException("Lỗi createAccount: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    @Override
    public void deleteAccount(String accountNumber) throws RemoteException {
        String sql = "DELETE FROM accounts WHERE account_number=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RemoteException("Lỗi deleteAccount: " + e.getMessage(), e);
        }
    }

    @Override
    public ArrayList<Account> getAllAccounts() throws RemoteException {
        ArrayList<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Account(
                        rs.getString("account_number"),
                        rs.getString("username"),
                        rs.getString("owner_name"),
                        rs.getDouble("balance")
                ));
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi getAllAccounts: " + e.getMessage(), e);
        }
        return list;
    }

    // ================= Transaction =================
    @Override
    public void deposit(String accountNumber, double amount) throws RemoteException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_number=?";
        String sqlTrans = "INSERT INTO transactions(account_number,type,amount) VALUES(?,?,?)";
        try {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, amount);
                stmt.setString(2, accountNumber);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlTrans)) {
                stmt.setString(1, accountNumber);
                stmt.setString(2, "deposit");
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi deposit: " + e.getMessage(), e);
        }
    }

    @Override
    public void withdraw(String accountNumber, double amount) throws RemoteException {
        String sqlCheck = "SELECT balance FROM accounts WHERE account_number=?";
        String sqlUpdate = "UPDATE accounts SET balance = balance - ? WHERE account_number=?";
        String sqlTrans = "INSERT INTO transactions(account_number,type,amount) VALUES(?,?,?)";

        try {
            double balance = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlCheck)) {
                stmt.setString(1, accountNumber);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) balance = rs.getDouble("balance");
                }
            }
            if (balance < amount) throw new RemoteException("Số dư không đủ");
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setDouble(1, amount);
                stmt.setString(2, accountNumber);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlTrans)) {
                stmt.setString(1, accountNumber);
                stmt.setString(2, "withdraw");
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi withdraw: " + e.getMessage(), e);
        }
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) throws RemoteException {
        try {
            conn.setAutoCommit(false);

            // Trừ tiền
            withdraw(fromAccountNumber, amount);

            // Cộng tiền
            deposit(toAccountNumber, amount);

            conn.commit();
        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RemoteException("Lỗi transfer: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    @Override
    public double getBalance(String accountNumber) throws RemoteException {
        String sql = "SELECT balance FROM accounts WHERE account_number=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi getBalance: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public ArrayList<Transaction> getTransactions(String accountNumber) throws RemoteException {
        ArrayList<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(
                            rs.getInt("id"),
                            rs.getString("account_number"),
                            rs.getString("type"),
                            rs.getDouble("amount"),
                            null, // description (not available in DB schema)
                            rs.getTimestamp("created_at") == null ? null : new java.util.Date(rs.getTimestamp("created_at").getTime())
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi getTransactions: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public ArrayList<Transaction> getAllTransactions() throws RemoteException {
        ArrayList<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Transaction(
                        rs.getInt("id"),
                        rs.getString("account_number"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        null, // description (not available in DB schema)
                        rs.getTimestamp("created_at") == null ? null : new java.util.Date(rs.getTimestamp("created_at").getTime())
                ));
            }
        } catch (SQLException e) {
            throw new RemoteException("Lỗi getAllTransactions: " + e.getMessage(), e);
        }
        return list;
    }

    // ================= Statistics =================
    @Override
    public int getAccountCount() throws RemoteException {
        String sql = "SELECT COUNT(*) FROM accounts";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RemoteException("Lỗi getAccountCount: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public double getTotalFunds() throws RemoteException {
        String sql = "SELECT SUM(balance) FROM accounts";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            throw new RemoteException("Lỗi getTotalFunds: " + e.getMessage(), e);
        }
        return 0;
    }
}