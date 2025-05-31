package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.KontoObserver;
import bankverwaltungssystem_javafx.models.GiroKonto;
import bankverwaltungssystem_javafx.models.Konto;
import bankverwaltungssystem_javafx.models.KundenService;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class GKDashboardController implements Initializable {
    @FXML
    private Label lblGKKontoNr2;
    @FXML
    private Label lblGKKontoAktiv2;
    @FXML
    private Label lblGKEinzahlungen2;
    @FXML
    private Label lblGKAuszahlungen2;
    @FXML
    private Label lblGKUeberziehungslimit2;
    @FXML
    private Label lblGKNegativZinssatz2;
    @FXML
    private Label lblGKPositivZinssatz2;
    @FXML
    private Label lblGKSpesen2;
    @FXML
    private Label lblGKKontostand;
    @FXML
    private TextField txtGKEinzahlungBetrag;
    @FXML
    private TextField txtGKAuszahlungBetrag;
    @FXML
    private TextField txtGKUeberweisungBetrag;
    @FXML
    private TextField txtGKUeberweisungKontoNr;

    private GiroKonto giroKonto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Füge einen Listener hinzu, der die UI aktualisiert, wenn sich die Kontodaten ändern
        KontoObserver.addListener(this::updateUI);
    }

    public void setGiroKonto(GiroKonto konto) {
        this.giroKonto = konto;
        updateUI();
    }

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

    @FXML
    private void einzahlenGK() throws SQLException {
        double betrag = Double.parseDouble(txtGKEinzahlungBetrag.getText());
        giroKonto.einzahlen(betrag);
        txtGKEinzahlungBetrag.clear();
    }

    @FXML
    private void auszahlenGK() throws SQLException {
        double betrag = Double.parseDouble(txtGKAuszahlungBetrag.getText());
        giroKonto.auszahlen(betrag);
        txtGKAuszahlungBetrag.clear();
    }

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

    @FXML
    private void berechneZinsenGK() throws SQLException {
        giroKonto.berechneZinsen();
    }

    @FXML
    private void spesenAbziehenGK() throws SQLException {
        giroKonto.spesenAbziehen();
    }
}
