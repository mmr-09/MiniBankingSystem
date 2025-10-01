package rmi.client;

import interfaces.IServices;
import models.Account;
import models.Transaction;
import models.TransactionTableModel;
import models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Customer extends JFrame {
    private final IServices bankService;
    private final User user;
    private JTextField amountField;
    private JTable transactionTable;
    private JLabel balanceLabel;
    private String accountNumber;

    public Customer(IServices bankService, User user) {
        this.bankService = bankService;
        this.user = user;

        // ===== Look & Feel =====
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        setTitle("Ngân Hàng - " + user.getUsername());
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBackground(new Color(245, 246, 250));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        // ===== Header =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 66, 128));
        headerPanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel header = new JLabel("DỊCH VỤ NGÂN HÀNG TRỰC TUYẾN", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(Color.WHITE);
        headerPanel.add(header, BorderLayout.CENTER);

        content.add(headerPanel, BorderLayout.NORTH);

        // ===== Panel thông tin tài khoản =====
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        infoPanel.setPreferredSize(new Dimension(280, 0)); // cố định chiều rộng panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Số tài khoản:"), gbc);

        gbc.gridx = 1;
        JTextField accountField = new JTextField();
        accountField.setEditable(false);
        accountField.setBackground(new Color(245, 245, 245));
        infoPanel.add(accountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Số dư hiện có:"), gbc);

        gbc.gridx = 1;
        balanceLabel = new JLabel("0 VND");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setForeground(new Color(0, 128, 0));
        infoPanel.add(balanceLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Số tiền:"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField();
        amountField.setBackground(new Color(250, 250, 250));
        infoPanel.add(amountField, gbc);

        // Lấy số tài khoản
        try {
            for (Account acc : bankService.getAllAccounts()) {
                if (acc.getUsername().equals(user.getUsername())) {
                    accountNumber = acc.getAccountNumber();
                    accountField.setText(accountNumber);
                    break;
                }
            }
        } catch (Exception e) {
            accountField.setText("Không tìm thấy");
        }

        // ===== Bảng giao dịch =====
        transactionTable = new JTable(new TransactionTableModel(new ArrayList<>()));
        transactionTable.setRowHeight(28);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        transactionTable.getTableHeader().setBackground(new Color(0, 102, 204));
        transactionTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(transactionTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Danh sách giao dịch"));
        scroll.setPreferredSize(new Dimension(600, 0)); // bảng chiếm phần còn lại

        // Gộp bảng + infoPanel bằng JSplitPane
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, infoPanel, scroll);
        centerSplit.setResizeWeight(0.3);
        centerSplit.setDividerSize(5);
        centerSplit.setOneTouchExpandable(false);
        content.add(centerSplit, BorderLayout.CENTER);

        // ===== Panel nút thao tác =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 246, 250));

        Color btnColor = new Color(0, 102, 204);

        JButton depositBtn = createActionButton("Nạp tiền", "💵", btnColor);
        JButton withdrawBtn = createActionButton("Rút tiền", "💳", btnColor);
        JButton transferBtn = createActionButton("Chuyển tiền", "🔁", btnColor);
        JButton viewBtn = createActionButton("Lịch sử giao dịch", "📜", btnColor);
        JButton logoutBtn = createActionButton("Đăng xuất", "🚪", new Color(200, 50, 50));

        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(transferBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(logoutBtn);

        // đặt panel nút xuống dưới
        content.add(buttonPanel, BorderLayout.SOUTH);

        // Định dạng cột số tiền
        DecimalFormat df = new DecimalFormat("#,###");
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            protected void setValue(Object value) {
                if (value instanceof Number)
                    setText(df.format(((Number) value).doubleValue()));
                else super.setValue(value);
            }
        };
        moneyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        transactionTable.getColumnModel().getColumn(3).setCellRenderer(moneyRenderer);

        // ===== Action =====
        depositBtn.addActionListener(e -> actionDeposit());
        withdrawBtn.addActionListener(e -> actionWithdraw());
        transferBtn.addActionListener(e -> actionTransfer());
        viewBtn.addActionListener(e -> loadTransactions());
        logoutBtn.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });

        updateBalance();
        
        // ===== Thêm timer tự động refresh số dư và giao dịch =====
        Timer autoRefreshTimer = new Timer(4000, e -> {
            updateBalance();
            loadTransactions();
        });
        autoRefreshTimer.start();
    }

    // ===== Button với style đẹp =====
    private JButton createActionButton(String text, String icon, Color bg) {
        JButton b = new JButton(icon + " " + text);
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(180, 45));
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng hover
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(bg.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    private void updateBalance() {
        try {
            double balance = bankService.getBalance(accountNumber);
            balanceLabel.setText(String.format("%,.0f VND", balance));
        } catch (Exception e) {
            balanceLabel.setText("Lỗi lấy số dư");
        }
    }

    private void actionDeposit() {
        try {
            double amt = Double.parseDouble(amountField.getText());
            bankService.deposit(accountNumber, amt);
            updateBalance();
            amountField.setText("");
            JOptionPane.showMessageDialog(this, "Nạp tiền thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void actionWithdraw() {
        try {
            double amt = Double.parseDouble(amountField.getText());
            bankService.withdraw(accountNumber, amt);
            updateBalance();
            amountField.setText("");
            JOptionPane.showMessageDialog(this, "Rút tiền thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void actionTransfer() {
        try {
            String toAcc = JOptionPane.showInputDialog(this, "Nhập số tài khoản nhận:");
            if (toAcc == null || toAcc.isBlank()) return;
            double amt = Double.parseDouble(amountField.getText());
            bankService.transfer(accountNumber, toAcc, amt);
            updateBalance();
            loadTransactions(); // Cập nhật lại bảng giao dịch sau khi chuyển tiền
            amountField.setText("");
            JOptionPane.showMessageDialog(this, "Chuyển tiền thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void loadTransactions() {
        try {
            ArrayList<Transaction> list = bankService.getTransactions(accountNumber);
            transactionTable.setModel(new TransactionTableModel(list));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}