package bankverwaltungssystem_javafx.models;

import bankverwaltungssystem_javafx.application.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse KundenService stellt Methoden zur Verwaltung von Kunden bereit, darunter das Erstellen und Suchen von Kunden.
 */
public class KundenService {

    /** Der aktuell aktive Kunde (statisch, um übergreifend zugänglich zu sein). */
    private static Kunde kunde;

    /**
     * ObservableList, die alle Kunden enthält.
     * <p>
     * Diese Liste ist direkt an die Benutzeroberfläche (ListView) gebunden (setItems()).
     * Änderungen an der Liste (z.B. Hinzufügen oder Entfernen von Kunden)
     * werden automatisch in der UI angezeigt, ohne dass manuelles Aktualisieren notwendig ist.
     */
    private ObservableList<Kunde> kundenListe;

    /**
     * Konstruktor für den KundenService.
     * Initialisiert die Kundenliste als leere ObservableList.
     */
    public KundenService() {
        this.kundenListe = FXCollections.observableArrayList();
    }

    /**
     * Erstellt einen neuen Kunden, wenn dieser noch nicht existiert, oder gibt den vorhandenen Kunden zurück.
     *
     * @param name           Name des Kunden
     * @param ort            Wohnort des Kunden
     * @param email          E-Mail-Adresse des Kunden
     * @param id             Identifikationsnummer des Kunden
     * @param kreditwuerdig  Kreditwuerdigkeit des Kunden
     * @return Das erstellte oder bereits vorhandene Kunden-Objekt
     * @throws SQLException  Wenn ein Datenbankfehler auftritt
     */
    public Kunde erstelleKunde(String name, String ort, String email, String id, boolean kreditwuerdig) throws SQLException {
        Connection con = DBManager.getConnection();

        String checkSQL = "SELECT * FROM kunde WHERE identifikationsNr = ?";
        PreparedStatement checkStmt = con.prepareStatement(checkSQL);
        checkStmt.setString(1, id);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) { // Kunde bereits vorhanden
            String abgerufenerName = rs.getString("name");
            String abgerufenerOrt = rs.getString("ort");
            String abgerufeneEmail = rs.getString("email");
            String abgerufeneId = rs.getString("identifikationsNr");
            boolean abgerufeneKreditw = rs.getBoolean("kreWuerdigkeit");

            DBManager.closeConnection();
            rs.close();
            checkStmt.close();

            Kunde kunde = new Kunde(abgerufenerName, abgerufenerOrt, abgerufeneEmail, abgerufeneId, abgerufeneKreditw);
            KundenService.kunde = kunde;
            return kunde;
        } else { // Neuen Kunden erstellen
            String insertSQL = "INSERT INTO kunde (name, ort, email, identifikationsNr, kreWuerdigkeit) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = con.prepareStatement(insertSQL);
            insertStmt.setString(1, name);
            insertStmt.setString(2, ort);
            insertStmt.setString(3, email);
            insertStmt.setString(4, id);
            insertStmt.setBoolean(5, kreditwuerdig);
            insertStmt.executeUpdate();
            insertStmt.close();

            DBManager.closeConnection();
            rs.close();
            checkStmt.close();

            Kunde kunde = new Kunde(name, ort, email, id, kreditwuerdig); // Wichtig: um den Kunden beim Erstellen eines Kontos zu haben
            KundenService.kunde = kunde;                                  // (bzw. die kid darüber (über diesen Kunden) suchen zu können)
            return kunde;
        }
    }

    /**
     * Sucht alle Kunden mit dem angegebenen Namen in der Datenbank.
     *
     * @param name Der Name, nach dem gesucht werden soll
     * @return Liste der gefundenen Kunden
     * @throws SQLException Bei einem Datenbankfehler
     */
    public List<Kunde> sucheKunde(String name) throws SQLException {
        List<Kunde> kunden = new ArrayList<>();
        Connection con = DBManager.getConnection();

        String sql = "SELECT * FROM kunde WHERE name LIKE ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1,  name);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String kundeName = rs.getString("name");
            String ort = rs.getString("ort");
            String email = rs.getString("email");
            String id = rs.getString("identifikationsNr");
            boolean kreWuerdigkeit = rs.getBoolean("kreWuerdigkeit");

            Kunde kunde = new Kunde(kundeName, ort, email, id, kreWuerdigkeit); // Wichtig: um den Kunden beim Erstellen eines Kontos zu haben
            KundenService.kunde = kunde;                                        // (bzw. die kid darüber (über diesen Kunden) suchen zu können
            kunden.add(kunde);
        }

        DBManager.closeConnection();
        rs.close();
        stmt.close();
        return kunden;
    }

    /**
     * Initialisiert die ListView zur Anzeige von Kunden.
     * Dabei wird die Kundenliste gesetzt und eine benutzerdefinierte Darstellung definiert.
     *
     * @param listView Die zu initialisierende ListView
     */
    public void initialisiereListView(ListView<Kunde> listView) {
        listView.setItems(kundenListe);

        listView.setCellFactory(new Callback<ListView<Kunde>, ListCell<Kunde>>() {
            @Override
            public ListCell<Kunde> call(ListView<Kunde> param) {
                return new ListCell<Kunde>() {
                    @Override
                    protected void updateItem(Kunde kunde, boolean empty) {
                        super.updateItem(kunde, empty);
                        if (empty || kunde == null) {
                            setText(null);
                        } else {
                            setText(String.format("%s (ID: %s, Ort: %s)", 
                                kunde.getName(), 
                                kunde.getIdentifikationsNr(), 
                                kunde.getOrt()));
                        }
                    }
                };
            }
        });
    }

    /**
     * Setzt die angegebene Kundenliste in die ObservableList.
     *
     * @param kunden Liste der Kunden, die übernommen werden sollen
     */
    public void setKundenListe(List<Kunde> kunden) {
        kundenListe.clear();
        kundenListe.addAll(kunden);
    }

    /**
     * Holt einen Kunden anhand seiner Kunden-ID (kid) aus der Datenbank.
     *
     * @param con Die Datenbankverbindung
     * @return Das Kundenobjekt oder null, wenn kein Kunde gefunden wurde
     * @throws SQLException Wenn ein Datenbankfehler auftritt
     */
    public static Kunde getKundeById(Connection con) throws SQLException {
        String query = "SELECT * FROM kunde WHERE kid = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            int kid = KundenService.kunde.getKundeID(con);
            ps.setInt(1, kid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Kunde(
                            rs.getString("name"),
                            rs.getString("ort"),
                            rs.getString("email"),
                            rs.getString("identifikationsNr"),
                            rs.getBoolean("kreWuerdigkeit")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Holt ein Konto anhand der Kontonummer aus der Datenbank.
     * Es wird sowohl in der Girokonto- als auch in der Sparkonto-Tabelle gesucht.
     *
     * @param con      Die Datenbankverbindung
     * @param kontoNr  Die gesuchte Kontonummer
     * @return Ein GiroKonto- oder SparKonto-Objekt, oder null wenn keines gefunden wurde
     * @throws SQLException Wenn ein Datenbankfehler auftritt
     */
    public static Konto getKontoByKontoNr(Connection con, String kontoNr) throws SQLException {
        // Als Erstes schauen, ob es Girokonto ist
        String queryGK = "SELECT * FROM girokonto WHERE kontoNr = ?";
        try (PreparedStatement ps = con.prepareStatement(queryGK)) {
            ps.setString(1, kontoNr);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GiroKonto(
                        rs.getString("kontoNr"),
                        rs.getDouble("kontoStand"),
                        rs.getDouble("ueberziehungsLimit"),
                        rs.getDouble("negativZinssatz"),
                        rs.getDouble("positivZinssatz"),
                        rs.getDouble("spesen")
                    );
                }
            }
        }

        // Dann schauen, ob es SparKonto ist
        String querySK = "SELECT * FROM sparkonto WHERE kontoNr = ?";
        try (PreparedStatement ps = con.prepareStatement(querySK)) {
            ps.setString(1, kontoNr);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SparKonto(
                        rs.getString("kontoNr"),
                        rs.getDouble("kontoStand"),
                        rs.getDouble("zinssatz")
                    );
                }
            }
        }
        return null;
    }
}

