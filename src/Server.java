package rmi.server;

import interfaces.IServices;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            IServices services = new Services();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("BankService", services);
            System.out.println("Server RMI đang chạy...");
        } catch (Exception e) {
            System.err.println("Lỗi server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}