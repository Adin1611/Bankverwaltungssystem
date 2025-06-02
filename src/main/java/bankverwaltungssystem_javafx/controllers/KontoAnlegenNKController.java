package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.models.GiroKonto;
import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.models.KundenService;
import bankverwaltungssystem_javafx.models.SparKonto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class KontoAnlegenNKController {
    @FXML
    private TextField txtGKKontoNr;
    @FXML
    private TextField txtGKKontostand;
    @FXML
    private RadioButton btnGKKontoAktivJa;
    @FXML
    private TextField txtGKSpesen;
    @FXML
    private TextField txtGKUeberziehungslimit;
    @FXML
    private TextField txtGKNegativZinssatz;
    @FXML
    private TextField txtGKPositivZinssatz;
    @FXML
    private Button btnGKAusfuehren;
    @FXML
    private TextField txtSKKontoNr;
    @FXML
    private TextField txtSKKontostand;
    @FXML
    private RadioButton btnSKKontoAktivJa;
    @FXML
    private TextField txtSKZinssatz;
    @FXML
    private Button btnSKAusfuehren;

    private Kunde kunde;

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    @FXML
    private void giroKontoEroeffnen(ActionEvent event) throws IOException, SQLException {
        String kontoNr = txtGKKontoNr.getText().toUpperCase().trim();
        String kontostand = txtGKKontostand.getText().trim();
        boolean kontoAktiv = btnGKKontoAktivJa.isSelected();
        String spesen = txtGKSpesen.getText().trim();
        String ueberziehunslimit = txtGKUeberziehungslimit.getText().trim();
        String negativZinssatz = txtGKNegativZinssatz.getText().trim();
        String positivZinssatz = txtGKPositivZinssatz.getText().trim();
        
        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        GiroKonto giroKonto = kunde.eroeffneGiroKonto(kontoNr, kontostand, kontoAktiv, spesen, ueberziehunslimit, negativZinssatz, positivZinssatz);
        GKDashboardController controller = FensterManager.oeffneFensterUndHoleController(
                "/bankverwaltungssystem_javafx/gkDashboard.fxml", "Girokonto-Dashboard", event);
        controller.setGiroKonto(giroKonto);
    }

    @FXML
    private void sparKontoEroeffnen(ActionEvent event) throws IOException, SQLException {
        String kontoNr = txtSKKontoNr.getText().toUpperCase().trim();
        String kontostand = txtSKKontostand.getText().trim();
        boolean kontoAktiv = btnSKKontoAktivJa.isSelected();
        String zinssatz = txtSKZinssatz.getText().trim();

        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        SparKonto sparKonto = kunde.eroeffneSparKonto(kontoNr, kontostand, kontoAktiv, zinssatz);
        SKDashboardController controller = FensterManager.oeffneFensterUndHoleController(
                "/bankverwaltungssystem_javafx/skDashboard.fxml", "Sparkonto-Dashboard", event);
        controller.setSparKonto(sparKonto);
    }
}
