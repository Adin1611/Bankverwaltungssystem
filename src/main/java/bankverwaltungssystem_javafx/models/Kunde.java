package bankverwaltungssystem_javafx.models;

import bankverwaltungssystem_javafx.application.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse Kunde repraesentiert einen Bankkunden und enthaelt Informationen wie Name, Wohnort,
 * E-Mail-Adresse, Kreditwuerdigkeit und Kunden-ID. Sie ermoeglicht auch die Eroeffnung von Girokonten
 * und Sparkonten fuer den Kunden.
 */
public class Kunde {

    /**
     * Der Name des Kunden.
     */
    private String name;

    /**
     * Der Wohnort des Kunden.
     */
    private String ort;

    /**
     * Die E-Mail-Adresse des Kunden.
     */
    private String email;

    /**
     * Die Identifikationsnummer des Kunden.
     */
    private String identifikationsNr;

    /**
     * Die Kreditwuerdigkeit des Kunden.
     */
    private boolean kreWuerdigkeit;

    private ObservableList<GiroKonto> girokontenListe;
    private ObservableList<SparKonto> sparkontenListe;


    /**
     * Erzeugt ein Kunde-Objekt und initialisiert es mit Benutzereingaben.
     *
     * @param name Der Name des Kunden.
     * @param ort Der Ort des Kunden.
     * @param email Die E-Mail des Kunden.
     * @param identifikationsNr Die Identifikationsnummer des Kunden.
     * @param kreWuerdigkeit Die Kreditwuerdigkeit des Kunden.
     */
    public Kunde(String name, String ort, String email, String identifikationsNr, boolean kreWuerdigkeit) {
        this.name = name;
        this.ort = ort;
        this.email = email;
        this.identifikationsNr = identifikationsNr;
        this.kreWuerdigkeit = kreWuerdigkeit;

        this.girokontenListe = FXCollections.observableArrayList();
        this.sparkontenListe = FXCollections.observableArrayList();
    }

    /**
     * Liefert den Namen des Kunden.
     *
     * @return Der Name des Kunden.
     */
    public String getName(){
        return name;
    }

    /**
     * Liefert den Wohnort des Kunden.
     *
     * @return Der Wohnort des Kunden.
     */
    public String getOrt(){
        return ort;
    }

    public String getEmail(){
        return email;
    }
    /**
     * Liefert die Identifikationsnummer des Kunden.
     *
     * @return Die Identifikationsnummer des Kunden.
     */
    public String getIdentifikationsNr(){
        return identifikationsNr;
    }

    public boolean getKreWuerdigkeit(){
        return kreWuerdigkeit;
    }

    /**
     * Liefert die Kunden-ID anhand der Identifikationsnummer aus der Datenbank.
     *
     * @param con Die Verbindung zur Datenbank
     * @return Die Kunden-ID.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public int getKundeID(Connection con) throws SQLException{
        String abfrageId = "SELECT kid FROM kunde WHERE identifikationsNr = '" + identifikationsNr + "'";
        PreparedStatement psKundeID = con.prepareStatement(abfrageId);
        ResultSet rsKundeID = psKundeID.executeQuery();
        int kid = 0;

        if (rsKundeID.next()) {
            kid = rsKundeID.getInt("kid"); // getInt, um den Wert direkt als Integer abzurufen
        }

        psKundeID.close();
        rsKundeID.close();
        return kid;
    }

    /**
     * Gibt eine Zeichenkettendarstellung des Kunden-Objekts zurueck.
     *
     * @param con Die Verbindung zur Datenbank.
     * @return Eine Zeichenkettendarstellung des Kunden.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public String toString(Connection con) throws SQLException{
        return "Kunde{" +
                "Name:'" + name + '\'' +
                ", Ort:'" + ort + '\'' +
                ", Email:'" + email + '\'' +
                ", Kreditwuerdigkeit:" + kreWuerdigkeit +
                ", KundeID:" + getKundeID(con) +
                "}";
    }

    /**
     * Eroeffnet ein neues Girokonto fuer den Kunden.
     *
     * @return Das neu eroeffnete Girokonto.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public GiroKonto eroeffneGiroKonto(String kontoNr, String kontoStandStr, boolean kontoAktiv,
                                       String spesenStr, String ueberziehungsLimitStr,
                                       String negativZinssatzStr, String positivZinssatzStr)
                                       throws SQLException, IllegalArgumentException {

        Connection con = DBManager.getConnection();
        int kid = getKundeID(con);
        double kontoStand = Double.parseDouble(kontoStandStr);
        double spesen = Double.parseDouble(spesenStr);
        double ueberziehungsLimit = Double.parseDouble(ueberziehungsLimitStr);
        double negativZinssatz = Double.parseDouble(negativZinssatzStr);
        double positivZinssatz = Double.parseDouble(positivZinssatzStr);

        // Überprüfung des Kontostands
        if (kontoStand < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kontostand Fehler");
            alert.setHeaderText("Ungültiger Kontostand");
            alert.setContentText("Der Kontostand darf nicht negativ sein. Bitte geben Sie einen positiven Wert ein.");
            alert.showAndWait();
        }

        // Überprüfen, ob es das Girokonto schon gibt, also ob das Girokonto schon in der Datenbank vorhanden ist
        String abfrageGiroKonto = "SELECT kontoNr FROM girokonto WHERE kontoNr = ?";
        PreparedStatement psAbfrageGiroKonto = con.prepareStatement(abfrageGiroKonto);
        psAbfrageGiroKonto.setString(1, kontoNr);
        ResultSet rsAbfrageGiroKonto = psAbfrageGiroKonto.executeQuery();

        if (rsAbfrageGiroKonto.next()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Girokonto Fehler");
            alert.setHeaderText("Konto bereits vorhanden");
            alert.setContentText("Ein Girokonto mit dieser Kontonummer existiert bereits. Bitte geben Sie eine andere Kontonummer ein.");
            alert.showAndWait();
        }
        else { // Daten in die Datenbank einfügen
            String sqlDaten = "INSERT INTO girokonto (kontoNr, kontoStand, kontoAktiv, summeEinzahlungen, summeAuszahlungen, " +
                    "ueberziehungsLimit, negativZinssatz, positivZinssatz, spesen, kid) VALUES (?,?,?,0,0,?,?,?,?,?)";
            PreparedStatement psDaten = con.prepareStatement(sqlDaten);
            psDaten.setString(1, kontoNr);
            psDaten.setDouble(2, kontoStand);
            psDaten.setBoolean(3, kontoAktiv);
            psDaten.setDouble(4, ueberziehungsLimit);
            psDaten.setDouble(5, negativZinssatz);
            psDaten.setDouble(6, positivZinssatz);
            psDaten.setDouble(7, spesen);
            psDaten.setInt(8, kid);
            psDaten.executeUpdate();
            psDaten.close();
        }

        DBManager.closeConnection();
        psAbfrageGiroKonto.close();
        rsAbfrageGiroKonto.close();
        return new GiroKonto(kontoNr,kontoStand,ueberziehungsLimit,negativZinssatz,positivZinssatz,spesen);
    }

    public List<GiroKonto> sucheGiroKonto(int kid) throws SQLException {
        List<GiroKonto> girokonten = new ArrayList<>();
        Connection con = DBManager.getConnection();

        String sql = "SELECT * FROM girokonto WHERE kid = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, kid);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String kontoNr = rs.getString("kontoNr");
            double kontoStand = rs.getDouble("kontoStand");
            double summeEinzahlungen = rs.getDouble("summeEinzahlungen");
            double summeAuszahlungen = rs.getDouble("summeAuszahlungen");
            double ueberziehungsLimit = rs.getDouble("ueberziehungsLimit");
            double negativZinssatz = rs.getDouble("negativZinssatz");
            double positivZinssatz = rs.getDouble("positivZinssatz");
            double spesen = rs.getDouble("spesen");

            GiroKonto girokonto = new GiroKonto(kontoNr, kontoStand, ueberziehungsLimit, 
                                              negativZinssatz, positivZinssatz, spesen);
            girokonto.setSummeEinzahlungen(summeEinzahlungen); // Wichtig: ohne dem würden die bereits zuvor getätigten Einzahlungen (summeEinzahlungen) bzw.
            girokonto.setSummeAuszahlungen(summeAuszahlungen); // die zuvor getätigten Auszahlungen (summeAuszahlungen) nicht selected werden (also summeEinzahlungen/summeAuszahlungen
                                                               // wären immer 0, obwohl sie zuvor eigentlich schon Einzahlungen/Auszahlungen geschehen sind)
            girokonten.add(girokonto);
        }

        DBManager.closeConnection();
        rs.close();
        stmt.close();
        return girokonten;
    }

    public void initialisiereListViewGK(ListView<GiroKonto> listView) {
        listView.setItems(girokontenListe);
        
        // Setze den CellFactory für die ListView
        listView.setCellFactory(new Callback<ListView<GiroKonto>, ListCell<GiroKonto>>() {
            @Override
            public ListCell<GiroKonto> call(ListView<GiroKonto> param) {
                return new ListCell<GiroKonto>() {
                    @Override
                    protected void updateItem(GiroKonto giroKonto, boolean empty) {
                        super.updateItem(giroKonto, empty);
                        if (empty || giroKonto == null) {
                            setText(null);
                        } else {
                            setText(String.format("%s (Kontostand: %s)",
                                giroKonto.getKontoNr(),
                                giroKonto.getKontoStand()));
                        }
                    }
                };
            }
        });
    }

    public void setGirokontenListe(List<GiroKonto> girokonten) {
        girokontenListe.clear();
        girokontenListe.addAll(girokonten);
    }

    /**
     * Eroeffnet ein neues Sparkonto fuer den Kunden.
     *
     * @return Das neu eroeffnete Sparkonto.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public SparKonto eroeffneSparKonto(String kontoNr, String kontoStandStr, boolean kontoAktiv, String zinssatzStr)
                                       throws SQLException, IllegalArgumentException{

        Connection con = DBManager.getConnection();
        int kid = getKundeID(con);
        double kontoStand = Double.parseDouble(kontoStandStr);
        double zinssatz = Double.parseDouble(zinssatzStr);

        // Überprüfung des Kontostands
        if (kontoStand < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kontostand-Fehler");
            alert.setHeaderText("Ungültiger Kontostand");
            alert.setContentText("Der Kontostand darf nicht negativ sein. Bitte geben Sie einen positiven Wert ein.");
            alert.showAndWait();
        }

        // Überprüfen, ob es das Girokonto schon gibt, also ob das Girokonto schon in der Datenbank vorhanden ist
        String abfrageSparKonto = "SELECT kontoNr FROM sparkonto WHERE kontoNr = ?";
        PreparedStatement psAbfrageSparKonto = con.prepareStatement(abfrageSparKonto);
        psAbfrageSparKonto.setString(1, kontoNr);
        ResultSet rsAbfrageSparKonto = psAbfrageSparKonto.executeQuery();

        if (rsAbfrageSparKonto.next()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sparkonto-Fehler");
            alert.setHeaderText("Konto bereits vorhanden");
            alert.setContentText("Ein Sparkonto mit dieser Kontonummer existiert bereits.");
            alert.showAndWait();
        }

        else {
            // Daten in die Datenbank einfügen
            String sqlDaten = "INSERT INTO sparkonto (kontoNr, kontoStand, kontoAktiv, summeEinzahlungen, summeAuszahlungen, " +
                    "zinssatz, kid) VALUES (?,?,?,0,0,?,?)";
            PreparedStatement psDaten = con.prepareStatement(sqlDaten);
            psDaten.setString(1, kontoNr);
            psDaten.setDouble(2, kontoStand);
            psDaten.setBoolean(3, kontoAktiv);
            psDaten.setDouble(4, zinssatz);
            psDaten.setInt(5, kid);
            psDaten.executeUpdate();
            psDaten.close();
        }

        DBManager.closeConnection();
        psAbfrageSparKonto.close();
        rsAbfrageSparKonto.close();
        return new SparKonto(kontoNr,kontoStand,zinssatz);
    }

    public List<SparKonto> sucheSparKonto(int kid) throws SQLException {
        List<SparKonto> sparkonten = new ArrayList<>();
        Connection con = DBManager.getConnection();

        String sql = "SELECT * FROM sparkonto WHERE kid = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, kid);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String kontoNr = rs.getString("kontoNr");
            double kontoStand = rs.getDouble("kontoStand");
            double summeEinzahlungen = rs.getDouble("summeEinzahlungen");
            double summeAuszahlungen = rs.getDouble("summeAuszahlungen");
            double zinssatz = rs.getDouble("zinssatz");

            SparKonto sparkonto = new SparKonto(kontoNr, kontoStand, zinssatz);
            sparkonto.setSummeEinzahlungen(summeEinzahlungen); // Wichtig: ohne dem würden die bereits zuvor getätigten Einzahlungen (summeEinzahlungen) bzw.
            sparkonto.setSummeAuszahlungen(summeAuszahlungen); // die zuvor getätigten Auszahlungen (summeAuszahlungen) nicht selected werden (also summeEinzahlungen/summeAuszahlungen
                                                               // wären immer 0, obwohl sie zuvor eigentlich schon Einzahlungen/Auszahlungen geschehen sind)
            sparkonten.add(sparkonto);
        }

        DBManager.closeConnection();
        rs.close();
        stmt.close();
        return sparkonten;
    }

    public void initialisiereListViewSK(ListView<SparKonto> listView) {
        listView.setItems(sparkontenListe);

        // Setze den CellFactory für die ListView
        listView.setCellFactory(new Callback<ListView<SparKonto>, ListCell<SparKonto>>() {
            @Override
            public ListCell<SparKonto> call(ListView<SparKonto> param) {
                return new ListCell<SparKonto>() {
                    @Override
                    protected void updateItem(SparKonto sparKonto, boolean empty) {
                        super.updateItem(sparKonto, empty);
                        if (empty || sparKonto == null) {
                            setText(null);
                        } else {
                            setText(String.format("%s (Kontostand: %s)",
                                    sparKonto.getKontoNr(),
                                    sparKonto.getKontoStand()));
                        }
                    }
                };
            }
        });
    }

    public void setSparkontenListe(List<SparKonto> sparkonten) {
        sparkontenListe.clear();
        sparkontenListe.addAll(sparkonten);
    }
}
