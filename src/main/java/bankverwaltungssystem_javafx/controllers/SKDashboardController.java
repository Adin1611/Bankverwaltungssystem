package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.application.KontoObserver;
import bankverwaltungssystem_javafx.models.Konto;
import bankverwaltungssystem_javafx.models.KundenService;
import bankverwaltungssystem_javafx.models.SparKonto;
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
 * Controller für das Sparkonto-Dashboard.
 * Verwaltet UI-Elemente und Aktionen für Einzahlungen, Auszahlungen, Überweisungen, Zinsberechnung usw.
 */
public class SKDashboardController implements Initializable {

    /** Anzeige der Kontonummer */
    @FXML private Label lblSKKontoNr2;
    /** Anzeige des Kontoaktivitätsstatus */
    @FXML private Label lblSKKontoAktiv2;
    /** Anzeige der Summe der Einzahlungen */
    @FXML private Label lblSKEinzahlungen2;
    /** Anzeige der Summe der Auszahlungen */
    @FXML private Label lblSKAuszahlungen2;
    /** Anzeige des Zinssatzes */
    @FXML private Label lblSKZinssatz2;
    /** Anzeige des aktuellen Kontostands */
    @FXML private Label lblSKKontostand;

    /** Eingabefeld für Einzahlung */
    @FXML private TextField txtSKEinzahlungBetrag;
    /** Eingabefeld für Auszahlung */
    @FXML private TextField txtSKAuszahlungBetrag;
    /** Eingabefeld für Überweisungsbetrag */
    @FXML private TextField txtSKUeberweisungBetrag;
    /** Eingabefeld für Empfängerkontonummer */
    @FXML private TextField txtSKUeberweisungKontoNr;

    /** Das aktive Sparkonto */
    private SparKonto sparKonto;

    /** Initialisiert den Controller und registriert UI-Update-Listener. */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Füge einen Listener hinzu, der die UI aktualisiert, wenn sich die Kontodaten ändern
        KontoObserver.addListener(this::updateUI);
    }

    /**
     * Setzt das anzuzeigende Sparkonto.
     * @param konto Das Sparkonto-Objekt
     */
    public void setSparKonto(SparKonto konto) {
        this.sparKonto = konto;
        updateUI();
    }

    /** Aktualisiert die UI-Elemente mit den aktuellen Kontodaten. */
    private void updateUI() {
        if (sparKonto != null) {
            lblSKKontostand.setText(String.format("%.2f", sparKonto.getKontoStand()));
            lblSKKontoNr2.setText(sparKonto.getKontoNr());
            lblSKKontoAktiv2.setText(sparKonto.isKontoAktiv() ? "Ja" : "Nein");
            lblSKEinzahlungen2.setText(String.format("%.2f", sparKonto.getSummeEinzahlungen()));
            lblSKAuszahlungen2.setText(String.format("%.2f", sparKonto.getSummeAuszahlungen()));
            lblSKZinssatz2.setText(String.format("%.2f", sparKonto.getZinssatz()) + " %");
        }
    }

    /**
     * Führt eine monatliche Einzahlung durch.
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void monatlichEinzahlenSK() throws SQLException {
        double betrag = Double.parseDouble(txtSKEinzahlungBetrag.getText());
        sparKonto.monatlicheEinzahlung(betrag);
        txtSKEinzahlungBetrag.clear();
    }

    /**
     * Führt eine Auszahlung durch.
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void auszahlenSK() throws SQLException {
        double betrag = Double.parseDouble(txtSKAuszahlungBetrag.getText());
        sparKonto.auszahlen(betrag);
        txtSKAuszahlungBetrag.clear();
    }

    /**
     * Führt eine Überweisung an ein anderes Konto durch.
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void ueberweisenSK() throws SQLException {
        double betrag = Double.parseDouble(txtSKUeberweisungBetrag.getText());
        String kontoNr = txtSKUeberweisungKontoNr.getText();
        Connection con = DBManager.getConnection();
        Konto konto = KundenService.getKontoByKontoNr(con, kontoNr);
        sparKonto.ueberweisen(betrag, konto, sparKonto.getKontoNr(), kontoNr);
        txtSKUeberweisungBetrag.clear();
        txtSKUeberweisungKontoNr.clear();
        DBManager.closeConnection();
    }

    /**
     * Führt die Zinsberechnung für das Sparkonto durch.
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void berechneZinsenSK() throws SQLException {
        sparKonto.berechneZinsen();
    }

    /**
     * Öffnet das Fenster zur Anzeige des Kontoauszugs.
     * @param event Das auslösende Ereignis
     * @throws SQLException Bei Datenbankfehlern
     * @throws IOException Wenn das Fenster nicht geladen werden kann
     */
    @FXML
    private void kontoauszugDrucken(ActionEvent event) throws SQLException, IOException {
        SKKontoauszugController controller = FensterManager.oeffneFensterUndHoleController(
                "/bankverwaltungssystem_javafx/skKontoauszug.fxml", "Kontoauszug", event);
        controller.setSparKontoUndKunde(sparKonto);
    }
}
