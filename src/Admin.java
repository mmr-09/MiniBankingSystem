package rmi.client;

import interfaces.IServices;
import models.Account;
import models.Transaction;
import models.User;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

public class Admin extends JFrame {
    private IServices bankService;
    private User user;

    // Input fields
    private JTextField ownerNameField, usernameField, passwordField;
    private JComboBox<String> roleBox;

    // Tables
    private JTable accountTable, transactionTable;
    private AccountTableModel accountTableModel;
    private TransactionTableModel transactionTableModel;

    // Thêm biến cho tổng số tài khoản và tổng số tiền
    private JLabel totalAccountsLabel, totalFundsLabel;

    public Admin(IServices bankService, User user) {
        this.bankService = bankService;
        this.user = user;
        initUI();
        loadAccounts();
        loadTransactions();
    }

    public void initUI() {
        setTitle("Quản trị hệ thống ngân hàng mini");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Panel input
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Split layout cho Account list và Transaction list
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createAccountPanel(), createTransactionPanel());
        splitPane.setDividerLocation(500);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Panel các nút thao tác phía dưới
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        JButton refreshBtn = new JButton("Làm mới");
        refreshBtn.addActionListener(e -> refreshAll());
        JButton viewTransBtn = new JButton("Xem giao dịch tài khoản");
        viewTransBtn.addActionListener(e -> {
            int row = accountTable.getSelectedRow();
            if (row >= 0) {
                String accNum = (String) accountTable.getValueAt(row, 0);
                loadTransactionsByAccount(accNum);
            } else {
                JOptionPane.showMessageDialog(this, "Chọn tài khoản để xem giao dịch!");
            }
        });
        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
        actionPanel.add(refreshBtn);
        actionPanel.add(viewTransBtn);
        actionPanel.add(logoutBtn);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        panel.add(new JLabel("Tên chủ tài khoản:"));
        ownerNameField = new JTextField();
        panel.add(ownerNameField);

        panel.add(new JLabel("Tên đăng nhập:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Mật khẩu:"));
        passwordField = new JTextField();
        panel.add(passwordField);

        panel.add(new JLabel("Vai trò:"));
        String[] roles = {"Customer", "Admin"};
        roleBox = new JComboBox<>(roles);
        panel.add(roleBox);

        // Nút tạo tài khoản
        JButton createBtn = new JButton("Tạo tài khoản");
        createBtn.addActionListener(e -> createAccount());
        panel.add(createBtn);

        // Nút xóa tài khoản
        JButton deleteBtn = new JButton("Xóa tài khoản");
        deleteBtn.addActionListener(e -> deleteAccount());
        panel.add(deleteBtn);

        // Thêm panel thống kê vào cuối inputPanel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        totalAccountsLabel = new JLabel("Tổng số tài khoản: 0");
        totalFundsLabel = new JLabel("Tổng tiền: 0 VND");
        statsPanel.add(totalAccountsLabel);
        statsPanel.add(totalFundsLabel);
        // Thêm statsPanel vào dòng cuối cùng của inputPanel
        panel.add(statsPanel);
        // Thêm một ô trống để cân layout
        panel.add(new JLabel(""));

        return panel;
    }

    private JScrollPane createAccountPanel() {
        accountTableModel = new AccountTableModel();
        accountTable = new JTable(accountTableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Bỏ ListSelectionListener tự động load giao dịch khi chọn tài khoản
        JScrollPane scrollPane = new JScrollPane(accountTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách tài khoản"));
        return scrollPane;
    }

    private JScrollPane createTransactionPanel() {
        transactionTableModel = new TransactionTableModel();
        transactionTable = new JTable(transactionTableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lịch sử giao dịch"));
        return scrollPane;
    }

    private void createAccount() {
        try {
            String owner = ownerNameField.getText().trim();
            String usern = usernameField.getText().trim();
            String pass = passwordField.getText().trim();
            String role = (String) roleBox.getSelectedItem();

            if (owner.isEmpty() || usern.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // gọi service tạo account + user
            bankService.createAccount(usern, pass, role, owner);

            JOptionPane.showMessageDialog(this, "✅ Tạo tài khoản thành công!");
            clearFields();
            loadAccounts();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi: " + ex.getMessage());
        }
    }

    private void deleteAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn tài khoản để xóa!");
            return;
        }

        String accountNumber = (String) accountTable.getValueAt(selectedRow, 0); // cột 0 là số tài khoản

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tài khoản " + accountNumber + " không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bankService.deleteAccount(accountNumber);
                JOptionPane.showMessageDialog(this, "✅ Xóa tài khoản thành công!");
                loadAccounts();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi khi xóa: " + ex.getMessage());
            }
        }
    }

    private void clearFields() {
        ownerNameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        roleBox.setSelectedIndex(0);
    }

    private void refreshAll() {
        loadAccounts();
        loadTransactions();
        updateStats();
    }

    private void updateStats() {
        try {
            int totalAcc = bankService.getAccountCount();
            double totalFunds = bankService.getTotalFunds();
            totalAccountsLabel.setText("Tổng số tài khoản: " + totalAcc);
            totalFundsLabel.setText("Tổng tiền: " + String.format("%,.0f VND", totalFunds));
        } catch (Exception e) {
            totalAccountsLabel.setText("Tổng số tài khoản: ?");
            totalFundsLabel.setText("Tổng tiền: ?");
        }
    }

    private void loadTransactionsByAccount(String accountNumber) {
        try {
            ArrayList<Transaction> transactions = bankService.getTransactions(accountNumber);
            transactionTableModel.setTransactions(transactions);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tải được giao dịch tài khoản!");
        }
    }

    private void loadAccounts() {
        try {
            ArrayList<Account> accounts = bankService.getAllAccounts();
            accountTableModel.setAccounts(accounts);
            updateStats();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tải được danh sách tài khoản!");
        }
    }

    private void loadTransactions() {
        try {
            ArrayList<Transaction> transactions = bankService.getAllTransactions();
            transactionTableModel.setTransactions(transactions);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tải được lịch sử giao dịch!");
        }
    }

    // Account table model
    class AccountTableModel extends AbstractTableModel {
        private String[] cols = {"Số tài khoản", "Chủ tài khoản", "Số dư"};
        private ArrayList<Account> accounts = new ArrayList<>();

        public void setAccounts(ArrayList<Account> accounts) {
            this.accounts = accounts;
            fireTableDataChanged();
        }

        public int getRowCount() { return accounts.size(); }
        public int getColumnCount() { return cols.length; }
        public String getColumnName(int col) { return cols[col]; }

        public Object getValueAt(int row, int col) {
            Account acc = accounts.get(row);
            switch (col) {
                case 0: return acc.getAccountNumber();
                case 1: return acc.getOwnerName();
                case 2: return acc.getBalance();
                default: return "";
            }
        }
    }

    // Transaction table model
    class TransactionTableModel extends AbstractTableModel {
        private String[] cols = {"Tài khoản", "Loại GD", "Số tiền", "Ngày"};
        private ArrayList<Transaction> transactions = new ArrayList<>();

        public void setTransactions(ArrayList<Transaction> transactions) {
            this.transactions = transactions;
            fireTableDataChanged();
        }

        public int getRowCount() { return transactions.size(); }
        public int getColumnCount() { return cols.length; }
        public String getColumnName(int col) { return cols[col]; }

        public Object getValueAt(int row, int col) {
            Transaction tr = transactions.get(row);
            switch (col) {
                case 0: return tr.getAccountNumber();
                case 1: return tr.getType();
                case 2: return tr.getAmount();
                case 3: return tr.getDate();
                default: return "";
            }
        }
    }
}