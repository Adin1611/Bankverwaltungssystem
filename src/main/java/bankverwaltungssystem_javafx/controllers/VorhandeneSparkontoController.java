package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.models.GiroKonto;
import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.models.KundenService;
import bankverwaltungssystem_javafx.models.SparKonto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class VorhandeneSparkontoController {
    @FXML
    private ListView<SparKonto> lvSparkonto;
    @FXML
    private Button btnOK;

    private Kunde kunde;


    public void setSparkontenListe(List<SparKonto> sparkonten) throws SQLException {
        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        kunde.initialisiereListViewSK(lvSparkonto);
        kunde.setSparkontenListe(sparkonten);
    }

    @FXML
    private void sparkontoAuswaehlen(ActionEvent event) throws IOException, SQLException {
        SparKonto selectedSK = lvSparkonto.getSelectionModel().getSelectedItem();
        if (selectedSK != null) {
            FensterManager.oeffneFenster("/bankverwaltungssystem_javafx/skDashboard.fxml", "Sparkonto-Dashboard", event);
        }

        // Zum Testen
        Connection con = DBManager.getConnection();
        kunde.druckeKontoauszugSparKonto(selectedSK, kunde.getName(), kunde.getOrt(), kunde.getKundeID(con));
    }
}
