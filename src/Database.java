package rmi.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/bank_system?useSSL=false";
    private static final String USER = "root"; // Thay bằng username MySQL của bạn
    private static final String PASSWORD = "felis0208"; // Thay bằng password MySQL của bạn

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}