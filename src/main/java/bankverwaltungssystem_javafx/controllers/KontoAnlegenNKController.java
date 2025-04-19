package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.models.GiroKonto;
import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.models.KundenService;
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

    private Kunde kunde;

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    @FXML
    private void giroKontoEroeffnen(ActionEvent event) throws IOException, SQLException {
        String kontoNr = txtGKKontoNr.getText().trim();
        String kontostand = txtGKKontostand.getText().trim();
        boolean kontoAktiv = btnGKKontoAktivJa.isSelected();
        String spesen = txtGKSpesen.getText().trim();
        String ueberziehunslimit = txtGKUeberziehungslimit.getText().trim();
        String negativZinssatz = txtGKNegativZinssatz.getText().trim();
        String positivZinssatz = txtGKPositivZinssatz.getText().trim();
        
        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        kunde.eroeffneGiroKonto(kontoNr, kontostand, kontoAktiv, spesen, ueberziehunslimit, negativZinssatz, positivZinssatz);
        FensterManager.oeffneFenster("/bankverwaltungssystem_javafx/gkDashboard.fxml", "Girokonto-Dashboard", event);
    }
}
