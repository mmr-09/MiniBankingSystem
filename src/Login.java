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
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(235, 235, 235));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60,120,200), 3, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(60,120,200));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Label Username
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);

        // TextField Username
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0; 
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30)); // fix chiều rộng + cao
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(usernameField, gbc);

        // Label Password
        gbc.gridy = 2; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        // TextField Password
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(passwordField, gbc);

        // Button Đăng nhập
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton loginButton = createBlueButton("Đăng nhập");
        panel.add(loginButton, gbc);

        // Button Đăng ký
        gbc.gridy = 4;
        JButton registerButton = createBlueButton("Đăng ký");
        panel.add(registerButton, gbc);

        add(panel);

        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                User user = bankService.login(username, password);
                if (user != null) {
                    dispose();
                    if ("Admin".equalsIgnoreCase(user.getRole())) {
                        new Admin(bankService, user).setVisible(true);
                    } else {
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

    private JButton createBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(60,120,200));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
    }

    public void showLogin() {
        setVisible(true);
    }
}
