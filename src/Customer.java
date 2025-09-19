package rmi.client;

import interfaces.IServices;
import models.Account;
import models.Transaction;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Customer extends JFrame {
    private IServices bankService;
    private User user;
    private JTextField accountNumberField, amountField;
    private JTextArea resultArea;
    private JLabel balanceLabel;

    public Customer(IServices bankService, User user) {
        this.bankService = bankService;
        this.user = user;

        setTitle("Giao Diện Khách Hàng - " + user.getUsername());
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(245, 245, 245));
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Số tài khoản
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
        try {
            ArrayList<Account> accounts = bankService.getAllAccounts();
            for (Account acc : accounts) {
                if (acc.getUsername().equals(user.getUsername())) {
                    accountNumberField.setText(acc.getAccountNumber());
                    break;
                }
            }
        } catch (Exception e) {
            accountNumberField.setText("Không tìm thấy tài khoản");
        }
        accountNumberField.setEditable(false);
        panel.add(accountNumberField, gbc);

        // Số dư hiện có
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Số dư hiện có:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        balanceLabel = new JLabel("0.0");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        balanceLabel.setForeground(new Color(0, 128, 0));
        panel.add(balanceLabel, gbc);

        // Số tiền
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Số tiền:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(300, 30));
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(amountField, gbc);

        // Các nút
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        JButton depositButton = new JButton("Nạp tiền");
        depositButton.setBackground(new Color(70, 130, 180));
        depositButton.setForeground(Color.WHITE);
        depositButton.setFocusPainted(false);
        depositButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(depositButton, gbc);

        gbc.gridy = 4;
        JButton withdrawButton = new JButton("Rút tiền");
        withdrawButton.setBackground(new Color(70, 130, 180));
        withdrawButton.setForeground(Color.WHITE);
        withdrawButton.setFocusPainted(false);
        withdrawButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(withdrawButton, gbc);

        gbc.gridy = 5;
        JButton transferButton = new JButton("Chuyển tiền");
        transferButton.setBackground(new Color(70, 130, 180));
        transferButton.setForeground(Color.WHITE);
        transferButton.setFocusPainted(false);
        transferButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(transferButton, gbc);

        gbc.gridy = 6;
        JButton viewTransactionsButton = new JButton("Xem lịch sử giao dịch");
        viewTransactionsButton.setBackground(new Color(70, 130, 180));
        viewTransactionsButton.setForeground(Color.WHITE);
        viewTransactionsButton.setFocusPainted(false);
        viewTransactionsButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(viewTransactionsButton, gbc);

        gbc.gridy = 7;
        resultArea = new JTextArea(6, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setBackground(Color.WHITE);
        panel.add(new JScrollPane(resultArea), gbc);

        gbc.gridy = 8;
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setBackground(new Color(211, 211, 211));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(logoutButton, gbc);

        add(panel);

        // Cập nhật số dư ban đầu
        updateBalance();

        // Action Listeners
        depositButton.addActionListener(e -> {
            try {
                String accountNumber = accountNumberField.getText();
                double amount = Double.parseDouble(amountField.getText());
                bankService.deposit(accountNumber, amount);
                resultArea.setText("Nạp tiền thành công. Số dư: " + bankService.getBalance(accountNumber));
                updateBalance();
                amountField.setText("");
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        withdrawButton.addActionListener(e -> {
            try {
                String accountNumber = accountNumberField.getText();
                double amount = Double.parseDouble(amountField.getText());
                bankService.withdraw(accountNumber, amount);
                resultArea.setText("Rút tiền thành công. Số dư: " + bankService.getBalance(accountNumber));
                updateBalance();
                amountField.setText("");
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        transferButton.addActionListener(e -> {
            try {
                String fromAccount = accountNumberField.getText();
                String toAccount = JOptionPane.showInputDialog(this, "Nhập số tài khoản nhận:");
                double amount = Double.parseDouble(amountField.getText());
                bankService.transfer(fromAccount, toAccount, amount);
                resultArea.setText("Chuyển tiền thành công. Số dư: " + bankService.getBalance(fromAccount));
                updateBalance();
                amountField.setText("");
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        viewTransactionsButton.addActionListener(e -> {
            try {
                String accountNumber = accountNumberField.getText();
                ArrayList<Transaction> transactions = bankService.getTransactions(accountNumber);
                StringBuilder sb = new StringBuilder("Lịch sử giao dịch:\n");
                for (Transaction t : transactions) {
                    sb.append(t.toString()).append("\n");
                }
                resultArea.setText(sb.toString());
            } catch (Exception ex) {
                resultArea.setText("Lỗi: " + ex.getMessage());
            }
        });

        logoutButton.addActionListener(e -> {
            dispose();
            new rmi.client.Login().setVisible(true);
        });
    }

    private void updateBalance() {
        try {
            String accountNumber = accountNumberField.getText();
            double balance = bankService.getBalance(accountNumber);
            balanceLabel.setText(String.format("%.2f VND", balance));
        } catch (Exception e) {
            balanceLabel.setText("Lỗi khi lấy số dư");
        }
    }
}