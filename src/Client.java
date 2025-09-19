package rmi.client;

import javax.swing.*;
import java.awt.*;

public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hệ Thống Ngân Hàng Mini");
            frame.setSize(400, 250);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            // Thiết lập background
            frame.getContentPane().setBackground(new Color(240, 248, 255)); // Màu xanh nhạt

            // Sử dụng BorderLayout
            frame.setLayout(new BorderLayout(10, 10));
            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setBackground(new Color(240, 248, 255));

            JButton loginButton = new JButton("Đăng Nhập");

            // Tùy chỉnh nút
            loginButton.setBackground(new Color(70, 130, 180)); // Màu xanh dương
            loginButton.setForeground(Color.WHITE);
            loginButton.setFocusPainted(false);
            loginButton.setFont(new Font("Arial", Font.BOLD, 14));
            loginButton.setPreferredSize(new Dimension(200, 50));

            // Thêm sự kiện
            loginButton.addActionListener(e -> {
                frame.dispose();
                new rmi.client.Login().setVisible(true); // Sử dụng Login từ package com.bank.rmi.client
            });

            // Sử dụng GridBagConstraints để căn giữa
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0); // Padding
            gbc.gridx = 0;
            gbc.gridy = 0;
            centerPanel.add(loginButton, gbc);

            frame.add(centerPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}