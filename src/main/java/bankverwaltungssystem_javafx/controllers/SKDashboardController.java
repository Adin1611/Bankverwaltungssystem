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

public class SKDashboardController implements Initializable {
    @FXML
    private Label lblSKKontoNr2;
    @FXML
    private Label lblSKKontoAktiv2;
    @FXML
    private Label lblSKEinzahlungen2;
    @FXML
    private Label lblSKAuszahlungen2;
    @FXML
    private Label lblSKZinssatz2;
    @FXML
    private Label lblSKKontostand;
    @FXML
    private TextField txtSKEinzahlungBetrag;
    @FXML
    private TextField txtSKAuszahlungBetrag;
    @FXML
    private TextField txtSKUeberweisungBetrag;
    @FXML
    private TextField txtSKUeberweisungKontoNr;

    private SparKonto sparKonto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Füge einen Listener hinzu, der die UI aktualisiert, wenn sich die Kontodaten ändern
        KontoObserver.addListener(this::updateUI);
    }

    public void setSparKonto(SparKonto konto) {
        this.sparKonto = konto;
        updateUI();
    }

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

    @FXML
    private void monatlichEinzahlenSK() throws SQLException {
        double betrag = Double.parseDouble(txtSKEinzahlungBetrag.getText());
        sparKonto.monatlicheEinzahlung(betrag);
        txtSKEinzahlungBetrag.clear();
    }

    @FXML
    private void auszahlenSK() throws SQLException {
        double betrag = Double.parseDouble(txtSKAuszahlungBetrag.getText());
        sparKonto.auszahlen(betrag);
        txtSKAuszahlungBetrag.clear();
    }

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

    @FXML
    private void berechneZinsenSK() throws SQLException {
        sparKonto.berechneZinsen();
    }


    @FXML
    private void kontoauszugDrucken(ActionEvent event) throws SQLException, IOException {
        SKKontoauszugController controller = FensterManager.oeffneFensterUndHoleController(
                "/bankverwaltungssystem_javafx/skKontoauszug.fxml", "Kontoauszug", event);
        controller.setSparKontoUndKunde(sparKonto);
    }
}
