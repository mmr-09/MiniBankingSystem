package rmi.client;

import interfaces.IServices;
import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class Register extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private IServices bankService;
    private Login loginFrame;

    public Register(IServices bankService, Login loginFrame) {
        this.bankService = bankService;
        this.loginFrame = loginFrame;

        setTitle("Đăng Ký");
        setSize(500, 500); // Kích thước đủ để hiển thị
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(245, 245, 245));
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 245, 245));

        JLabel logoLabel = new JLabel("Đăng Ký");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(new Color(70, 130, 180));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 10); // Đều nhau cho tất cả
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(logoLabel, gbc);

        // Label và TextField cho Tên đăng nhập
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST; // Căn đông (phải)
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setPreferredSize(new Dimension(150, 30)); // Đặt chiều rộng cố định
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST; // Căn tây (trái)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 30));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameField, gbc);

        // Label và TextField cho Mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setPreferredSize(new Dimension(150, 30)); // Đặt chiều rộng cố định
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 30));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(passwordField, gbc);

        // Label và TextField cho Xác nhận mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordLabel.setPreferredSize(new Dimension(150, 30)); // Đặt chiều rộng cố định
        panel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(250, 30));
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(confirmPasswordField, gbc);

        // Nút Đăng ký
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerButton = new JButton("Đăng ký");
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(registerButton, gbc);

        // Nút Quay lại
        gbc.gridy = 5;
        JButton backButton = new JButton("Quay lại");
        backButton.setBackground(new Color(211, 211, 211));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(backButton, gbc);

        add(panel);

        registerButton.addActionListener(e -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu và xác nhận mật khẩu không khớp!");
                    return;
                }

                bankService.register(username, password, "customer");
                String accountNumber = String.valueOf(System.currentTimeMillis());
                JOptionPane.showMessageDialog(this, "Đăng ký thành công! Số tài khoản tự động tạo: " + accountNumber);
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
}