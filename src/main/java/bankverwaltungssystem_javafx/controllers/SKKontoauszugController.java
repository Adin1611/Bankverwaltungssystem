package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.models.GiroKonto;
import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.models.KundenService;
import bankverwaltungssystem_javafx.models.SparKonto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.SQLException;

public class SKKontoauszugController {
    @FXML
    private Label lblName2;
    @FXML
    private Label lblOrt2;
    @FXML
    private Label lblEmail2;
    @FXML
    private Label lblID2;
    @FXML
    private Label lblKreWuerdigkeit2;
    @FXML
    private Label lblKontoNr2;
    @FXML
    private Label lblKontostand2;
    @FXML
    private Label lblKontoAktiv2;
    @FXML
    private Label lblEinzahlungen2;
    @FXML
    private Label lblAuszahlungen2;
    @FXML
    private Label lblZinssatz2;

    private SparKonto sparKonto;
    private Kunde kunde;

    public void setSparKontoUndKunde(SparKonto konto) throws SQLException {
        Connection con = DBManager.getConnection();
        this.sparKonto = konto;
        this.kunde = KundenService.getKundeById(con);
        updateUI();
        DBManager.closeConnection();
    }

    private void updateUI() {
        if (sparKonto != null && kunde != null) {
            // Kundendaten
            lblName2.setText(kunde.getName());
            lblOrt2.setText(kunde.getOrt());
            lblEmail2.setText(kunde.getEmail());
            lblID2.setText(String.valueOf(kunde.getIdentifikationsNr()));
            lblKreWuerdigkeit2.setText(kunde.getKreWuerdigkeit() ? "Ja" : "Nein");

            // Kontodaten
            lblKontoNr2.setText(sparKonto.getKontoNr());
            lblKontostand2.setText(String.format("%.2f €", sparKonto.getKontoStand()));
            lblKontoAktiv2.setText(sparKonto.isKontoAktiv() ? "Ja" : "Nein");
            lblEinzahlungen2.setText(String.format("%.2f €", sparKonto.getSummeEinzahlungen()));
            lblAuszahlungen2.setText(String.format("%.2f €", sparKonto.getSummeAuszahlungen()));
            lblZinssatz2.setText(String.format("%.2f %%", sparKonto.getZinssatz()));
        }
    }
}
