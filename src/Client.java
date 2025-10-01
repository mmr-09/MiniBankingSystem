package rmi.client;

public class Client {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true); // Mở thẳng giao diện đăng nhập
        });
    }
}
