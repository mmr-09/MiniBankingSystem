package rmi.client;

import interfaces.IServices;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private IServices bankService;

    public Login() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            bankService = (IServices) registry.lookup("BankService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối server: " + e.getMessage());
            System.exit(1);
        }

        setTitle("Đăng Nhập");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(245, 245, 245));
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 245, 245));

        JLabel logoLabel = new JLabel("Đăng Nhập");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(new Color(70, 130, 180));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(logoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 30));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 30));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(loginButton, gbc);

        gbc.gridy = 4;
        JButton registerButton = new JButton("Đăng ký");
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(registerButton, gbc);

        add(panel);

        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                User user = bankService.login(username, password);
                if (user != null) {
                    if (username.equals("admin") && password.equals("admin123")) {
                        dispose();
                        new Admin(bankService, user).setVisible(true);
                    } else {
                        dispose();
                        new Customer(bankService, user).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        registerButton.addActionListener(e -> {
            setVisible(false);
            new Register(bankService, this).setVisible(true);
        });
    }

    public void showLogin() {
        setVisible(true);
    }
}