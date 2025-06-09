package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.models.GiroKonto;
import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.models.KundenService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Controller für die Anzeige des Kontoauszugs eines Girokontos.
 */
public class GKKontoauszugController {
    /** Label für Kundendaten */
    @FXML private Label lblName2;
    @FXML private Label lblOrt2;
    @FXML private Label lblEmail2;
    @FXML private Label lblID2;
    @FXML private Label lblKreWuerdigkeit2;

    /** Label für Kontodaten */
    @FXML private Label lblKontoNr2;
    @FXML private Label lblKontostand2;
    @FXML private Label lblKontoAktiv2;
    @FXML private Label lblEinzahlungen2;
    @FXML private Label lblAuszahlungen2;
    @FXML private Label lblUeberziehungslimit2;
    @FXML private Label lblNegativZinssatz2;
    @FXML private Label lblPositivZinssatz2;
    @FXML private Label lblSpesen2;

    /** Das angezeigte Girokonto */
    private GiroKonto giroKonto;
    /** Der zugehörige Kunde */
    private Kunde kunde;

    /**
     * Setzt das Konto und den Kunden und aktualisiert die Oberfläche.
     * @param konto Das Girokonto
     * @throws SQLException Bei Datenbankfehlern
     */
    public void setGiroKontoUndKunde(GiroKonto konto) throws SQLException {
        Connection con = DBManager.getConnection();
        this.giroKonto = konto;
        this.kunde = KundenService.getKundeById(con);
        updateUI();
        DBManager.closeConnection();
    }

    /**
     * Aktualisiert die Benutzeroberfläche mit Kunden- und Kontodaten.
     */
    private void updateUI() {
        if (giroKonto != null && kunde != null) {
            // Kundendaten
            lblName2.setText(kunde.getName());
            lblOrt2.setText(kunde.getOrt());
            lblEmail2.setText(kunde.getEmail());
            lblID2.setText(String.valueOf(kunde.getIdentifikationsNr()));
            lblKreWuerdigkeit2.setText(kunde.getKreWuerdigkeit() ? "Ja" : "Nein");
            
            // Kontodaten
            lblKontoNr2.setText(giroKonto.getKontoNr());
            lblKontostand2.setText(String.format("%.2f €", giroKonto.getKontoStand()));
            lblKontoAktiv2.setText(giroKonto.isKontoAktiv() ? "Ja" : "Nein");
            lblEinzahlungen2.setText(String.format("%.2f €", giroKonto.getSummeEinzahlungen()));
            lblAuszahlungen2.setText(String.format("%.2f €", giroKonto.getSummeAuszahlungen()));
            lblUeberziehungslimit2.setText(String.format("%.2f €", giroKonto.getUeberziehungsLimit()));
            lblNegativZinssatz2.setText(String.format("%.2f %%", giroKonto.getNegativZinssatz()));
            lblPositivZinssatz2.setText(String.format("%.2f %%", giroKonto.getPositivZinssatz()));
            lblSpesen2.setText(String.format("%.2f €", giroKonto.getSpesen()));
        }
    }
} 