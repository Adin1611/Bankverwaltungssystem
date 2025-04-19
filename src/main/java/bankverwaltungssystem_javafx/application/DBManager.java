package bankverwaltungssystem_javafx.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/bankverwaltungssystem"; // oder MySQL: jdbc:mysql://localhost:3306/bank
    private static final String USER = "root"; // bei SQLite leer
    private static final String PASSWORD = "";

    private DBManager() {
        // privater Konstruktor, weil Singleton
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

