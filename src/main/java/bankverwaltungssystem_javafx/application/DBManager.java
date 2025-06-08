package bankverwaltungssystem_javafx.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton-Klasse zur Verwaltung der Datenbankverbindung.
 * Stellt sicher, dass nur eine Verbindung gleichzeitig aktiv ist.
 */
public class DBManager {

    private static Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/bankverwaltungssystem";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Privater Konstruktor zur Verhinderung von Instanziierung.
     */
    private DBManager() {
        // verhindert Instanziierung
    }

    /**
     * Gibt eine gültige, aktive Datenbankverbindung zurück.
     * Falls noch keine Verbindung besteht oder sie geschlossen ist, wird eine neue geöffnet.
     *
     * @return Eine gültige JDBC-Verbindung zur Datenbank.
     * @throws SQLException wenn ein Fehler beim Verbindungsaufbau auftritt.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /**
     * Schließt die Datenbankverbindung, falls sie offen ist.
     *
     * @throws SQLException wenn ein Fehler beim Schließen auftritt.
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
