package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.application.KontoObserver;
import bankverwaltungssystem_javafx.models.GiroKonto;
import bankverwaltungssystem_javafx.models.Konto;
import bankverwaltungssystem_javafx.models.KundenService;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller für das Girokonto-Dashboard.
 * Verwaltet UI-Elemente und Aktionen für Einzahlungen, Auszahlungen, Überweisungen, Zinsberechnung, Spesen-Abzug.
 */
public class GKDashboardController implements Initializable {
    /** Label zur Anzeige der Kontonummer */
    @FXML private Label lblGKKontoNr2;
    /** Label zur Anzeige ob das Konto aktiv ist */
    @FXML private Label lblGKKontoAktiv2;
    /** Label zur Anzeige der Summe aller Einzahlungen */
    @FXML private Label lblGKEinzahlungen2;
    /** Label zur Anzeige der Summe aller Auszahlungen */
    @FXML private Label lblGKAuszahlungen2;
    /** Label zur Anzeige des Überziehungslimits */
    @FXML private Label lblGKUeberziehungslimit2;
    /** Label zur Anzeige des Negativzinssatzes */
    @FXML private Label lblGKNegativZinssatz2;
    /** Label zur Anzeige des Positivzinssatzes */
    @FXML private Label lblGKPositivZinssatz2;
    /** Label zur Anzeige der Spesen */
    @FXML private Label lblGKSpesen2;
    /** Label zur Anzeige des Kontostands */
    @FXML private Label lblGKKontostand;

    /** Textfeld für Einzahlungsbetrag */
    @FXML private TextField txtGKEinzahlungBetrag;
    /** Textfeld für Auszahlungsbetrag */
    @FXML private TextField txtGKAuszahlungBetrag;
    /** Textfeld für Überweisungsbetrag */
    @FXML private TextField txtGKUeberweisungBetrag;
    /** Textfeld für Empfängerkontonummer */
    @FXML private TextField txtGKUeberweisungKontoNr;

    /** Aktives Girokonto */
    private GiroKonto giroKonto;

    /**
     * Initialisiert den Controller.
     *
     * Registriert einen Observer (Beobachter), der auf Änderungen der Kontodaten hört.
     * Sobald sich bestimmte Kontodaten ändern, wird die Methode {@code updateUI} aufgerufen,
     * um die Benutzeroberfläche entsprechend zu aktualisieren.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        KontoObserver.addListener(this::updateUI); // = Methodenreferenz - synonym zu anonyme innere Klasse
    }

    /**
     * Setzt das Girokonto und aktualisiert die UI.
     *
     * @param konto Das Girokonto-Objekt
     */
    public void setGiroKonto(GiroKonto konto) {
        this.giroKonto = konto;
        updateUI();
    }

    /**
     * Aktualisiert die Benutzeroberfläche mit den aktuellen Kontodaten.
     */
    private void updateUI() {
        if (giroKonto != null) {
            lblGKKontostand.setText(String.format("%.2f", giroKonto.getKontoStand()));
            lblGKKontoNr2.setText(giroKonto.getKontoNr());
            lblGKKontoAktiv2.setText(giroKonto.isKontoAktiv() ? "Ja" : "Nein");
            lblGKEinzahlungen2.setText(String.format("%.2f", giroKonto.getSummeEinzahlungen()));
            lblGKAuszahlungen2.setText(String.format("%.2f", giroKonto.getSummeAuszahlungen()));
            lblGKUeberziehungslimit2.setText(String.format("%.2f", giroKonto.getUeberziehungsLimit()));
            lblGKNegativZinssatz2.setText(String.format("%.2f", giroKonto.getNegativZinssatz()) + " %");
            lblGKPositivZinssatz2.setText(String.format("%.2f", giroKonto.getPositivZinssatz()) + " %");
            lblGKSpesen2.setText(String.format("%.2f", giroKonto.getSpesen()));
        }
    }

    /**
     * Führt eine Einzahlung aus.
     *
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void einzahlenGK() throws SQLException {
        double betrag = Double.parseDouble(txtGKEinzahlungBetrag.getText());
        giroKonto.einzahlen(betrag);
        txtGKEinzahlungBetrag.clear();
    }

    /**
     * Führt eine Auszahlung aus und leert das Eingabefeld.
     *
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void auszahlenGK() throws SQLException {
        double betrag = Double.parseDouble(txtGKAuszahlungBetrag.getText());
        giroKonto.auszahlen(betrag);
        txtGKAuszahlungBetrag.clear();
    }

    /**
     * Führt eine Überweisung durch und leert die Eingabefelder.
     *
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void ueberweisenGK() throws SQLException {
        double betrag = Double.parseDouble(txtGKUeberweisungBetrag.getText());
        String kontoNr = txtGKUeberweisungKontoNr.getText();
        Connection con = DBManager.getConnection();
        Konto konto = KundenService.getKontoByKontoNr(con, kontoNr);
        giroKonto.ueberweisen(betrag, konto, giroKonto.getKontoNr(), kontoNr);
        txtGKUeberweisungBetrag.clear();
        txtGKUeberweisungKontoNr.clear();
        DBManager.closeConnection();
    }

    /**
     * Berechnet Zinsen für das Girokonto.
     *
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void berechneZinsenGK() throws SQLException {
        giroKonto.berechneZinsen();
    }

    /**
     * Führt den Spesenabzug für das Konto durch.
     *
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void spesenAbziehenGK() throws SQLException {
        giroKonto.spesenAbziehen();
    }

    /**
     * Öffnet das Fenster für den Kontoauszug.
     *
     * @param event Das auslösende Ereignis
     * @throws SQLException Bei Datenbankfehlern
     * @throws IOException  Bei Fehlern beim Laden des Fensters
     */
    @FXML
    private void kontoauszugDrucken(ActionEvent event) throws SQLException, IOException {
        GKKontoauszugController controller = FensterManager.oeffneFensterUndHoleController( // damit die Kunden- und Kontodaten auf dem GKKontoauszug übertragen werden
                "/bankverwaltungssystem_javafx/gkKontoauszug.fxml", "Kontoauszug", event);
        controller.setGiroKontoUndKunde(giroKonto);
    }
}
