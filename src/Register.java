package rmi.client;

import interfaces.IServices;
import javax.swing.*;
import java.awt.*;

public class Register extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField ownerNameField;
    private final IServices bankService;
    private final Login loginFrame;

    public Register(IServices bankService, Login loginFrame) {
        this.bankService = bankService;
        this.loginFrame = loginFrame;

        setTitle("Đăng Ký");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(235,235,235));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90,90,90), 3, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ĐĂNG KÝ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(60,120,200));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(passwordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(confirmPasswordField, gbc);

        gbc.gridy = 4; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tên chủ tài khoản:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        ownerNameField = new JTextField(20);
        ownerNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(ownerNameField, gbc);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton registerButton = createBlueButton("Đăng ký");
        panel.add(registerButton, gbc);
        gbc.gridy = 6;
        JButton backButton = createGrayButton("Quay lại");
        panel.add(backButton, gbc);

        add(panel);

        registerButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirm = new String(confirmPasswordField.getPassword());
                String ownerName = ownerNameField.getText().trim();
                if (!password.equals(confirm)) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
                    return;
                }
                if (ownerName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập tên chủ tài khoản!");
                    return;
                }
                // Đăng ký user và tạo account luôn
                bankService.createAccount(username, password, "Customer", ownerName);
                JOptionPane.showMessageDialog(this,
                        "Đăng ký thành công!\nBạn có thể đăng nhập ngay.");
                dispose();
                loginFrame.showLogin();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            loginFrame.showLogin();
        });
    }

    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(60,120,200));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(170, 35));
        return btn;
    }

    private JButton createGrayButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(200, 200, 200));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(170, 35));
        return btn;
    }
}