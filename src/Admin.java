package rmi.client;

import interfaces.IServices;
import models.Account;
import models.Transaction;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Admin extends JFrame {
    private IServices bankService;
    private User user;
    private JTextField accountNumberField, ownerNameField, usernameField;
    private JTextArea resultArea;

    public Admin(IServices bankService, User user) {
        this.bankService = bankService;
        this.user = user;

        setTitle("Giao Diện Admin - " + user.getUsername());
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(245, 245, 245));
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Số tài khoản:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        accountNumberField = new JTextField();
        accountNumberField.setPreferredSize(new Dimension(300, 30));
        accountNumberField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(accountNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Tên chủ tài khoản:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        ownerNameField = new JTextField();
        ownerNameField.setPreferredSize(new Dimension(300, 30));
        ownerNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(ownerNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 30));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JButton createAccountButton = new JButton("Tạo tài khoản");
        createAccountButton.setBackground(new Color(70, 130, 180));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFocusPainted(false);
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(createAccountButton, gbc);

        gbc.gridx = 1;
        JButton deleteAccountButton = new JButton("Xóa tài khoản");
        deleteAccountButton.setBackground(new Color(70, 130, 180));
        deleteAccountButton.setForeground(Color.WHITE);
        deleteAccountButton.setFocusPainted(false);
        deleteAccountButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(deleteAccountButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        JButton listAccountsButton = new JButton("Danh sách tài khoản");
        listAccountsButton.setBackground(new Color(70, 130, 180));
        listAccountsButton.setForeground(Color.WHITE);
        listAccountsButton.setFocusPainted(false);
        listAccountsButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(listAccountsButton, gbc);

        gbc.gridy = 5;
        JButton countAccountsButton = new JButton("Thống kê số lượng tài khoản");
        countAccountsButton.setBackground(new Color(70, 130, 180));
        countAccountsButton.setForeground(Color.WHITE);
        countAccountsButton.setFocusPainted(false);
        countAccountsButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(countAccountsButton, gbc);

        gbc.gridy = 6;
        JButton totalFundsButton = new JButton("Thống kê tổng nguồn tiền");
        totalFundsButton.setBackground(new Color(70, 130, 180));
        totalFundsButton.setForeground(Color.WHITE);
        totalFundsButton.setFocusPainted(false);
        totalFundsButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(totalFundsButton, gbc);

        gbc.gridy = 7;
        JButton viewAllTransactionsButton = new JButton("Xem tổng lịch sử giao dịch");
        viewAllTransactionsButton.setBackground(new Color(70, 130, 180));
        viewAllTransactionsButton.setForeground(Color.WHITE);
        viewAllTransactionsButton.setFocusPainted(false);
        viewAllTransactionsButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(viewAllTransactionsButton, gbc);

        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setBackground(Color.WHITE);
        resultArea.setPreferredSize(new Dimension(500, 200));
        panel.add(new JScrollPane(resultArea), gbc);

        gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setBackground(new Color(211, 211, 211));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(logoutButton, gbc);

        add(panel);

        createAccountButton.addActionListener(e -> {
            try {
                String accountNumber = accountNumberField.getText();
                String ownerName = ownerNameField.getText();
                String username = usernameField.getText();
                bankService.createAccount(username, accountNumber, ownerName);
                resultArea.setText("Tạo tài khoản thành công.");
                accountNumberField.setText("");
                ownerNameField.setText("");
                usernameField.setText("");
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        deleteAccountButton.addActionListener(e -> {
            try {
                String accountNumber = accountNumberField.getText();
                bankService.deleteAccount(accountNumber);
                resultArea.setText("Xóa tài khoản thành công.");
                accountNumberField.setText("");
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        listAccountsButton.addActionListener(e -> {
            try {
                ArrayList<Account> accounts = bankService.getAllAccounts();
                if (accounts == null || accounts.isEmpty()) {
                    resultArea.setText("Không có tài khoản nào trong danh sách.");
                } else {
                    StringBuilder sb = new StringBuilder("Danh sách tài khoản:\n");
                    for (Account acc : accounts) {
                        sb.append("Số TK: ").append(acc.getAccountNumber())
                          .append(", Chủ TK: ").append(acc.getOwnerName())
                          .append(", Số dư: ").append(acc.getBalance())
                          .append("\n");
                    }
                    resultArea.setText(sb.toString());
                }
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        countAccountsButton.addActionListener(e -> {
            try {
                int accountCount = bankService.getAccountCount();
                resultArea.setText("Tổng số lượng tài khoản: " + accountCount);
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        totalFundsButton.addActionListener(e -> {
            try {
                double totalFunds = bankService.getTotalFunds();
                resultArea.setText("Tổng nguồn tiền: " + totalFunds + " VND");
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        viewAllTransactionsButton.addActionListener(e -> {
            try {
                ArrayList<Transaction> transactions = bankService.getAllTransactions();
                if (transactions == null || transactions.isEmpty()) {
                    resultArea.setText("Không có giao dịch nào trong hệ thống.");
                } else {
                    StringBuilder sb = new StringBuilder("Tổng lịch sử giao dịch:\n");
                    for (Transaction t : transactions) {
                        sb.append(t.toString()).append("\n");
                    }
                    resultArea.setText(sb.toString());
                }
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        logoutButton.addActionListener(e -> {
            dispose();
            new rmi.client.Login().setVisible(true);
        });
    }
}