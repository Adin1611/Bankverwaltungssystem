package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.models.KundenService;
import bankverwaltungssystem_javafx.models.SparKonto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class VorhandeneSparkontoController {
    @FXML
    private ListView<SparKonto> lvSparkonto;

    private Kunde kunde;


    public void setSparkontenListe(List<SparKonto> sparkonten) throws SQLException {
        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        kunde.initialisiereListViewSK(lvSparkonto);
        kunde.setSparkontenListe(sparkonten);
        DBManager.closeConnection();
    }

    @FXML
    private void sparkontoAuswaehlen(ActionEvent event) throws IOException {
        SparKonto selectedSK = lvSparkonto.getSelectionModel().getSelectedItem();
        if (selectedSK != null) {
            SKDashboardController controller = FensterManager.oeffneFensterUndHoleController(
                    "/bankverwaltungssystem_javafx/skDashboard.fxml", "Sparkonto-Dashboard", event);
            controller.setSparKonto(selectedSK);
        }
    }
}
