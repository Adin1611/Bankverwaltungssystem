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

/**
 * Controller zur Anzeige und Auswahl vorhandener Sparkonten.
 */
public class VorhandeneSparkontoController {

    /** ListView zur Anzeige der Sparkonten */
    @FXML private ListView<SparKonto> lvSparkonto;

    /** Der zugehörige Kunde */
    private Kunde kunde;

    /**
     * Setzt die Sparkontenliste und initialisiert die Anzeige.
     * @param sparkonten Liste der Sparkonten
     * @throws SQLException Bei Datenbankfehlern
     */
    public void setSparkontenListe(List<SparKonto> sparkonten) throws SQLException {
        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        kunde.initialisiereListViewSK(lvSparkonto);
        kunde.setSparkontenListe(sparkonten);
        DBManager.closeConnection();
    }

    /**
     * Öffnet das Dashboard für das ausgewählte Sparkonto.
     * @param event Das auslösende Ereignis
     * @throws IOException Wenn das Fenster nicht geladen werden kann
     */
    @FXML
    private void sparkontoAuswaehlen(ActionEvent event) throws IOException {
        SparKonto selectedSK = lvSparkonto.getSelectionModel().getSelectedItem();
        if (selectedSK != null) {
            SKDashboardController controller = FensterManager.oeffneFensterUndHoleController( // damit die Kontodaten auf das SKDashboard übertragen werden
                    "/bankverwaltungssystem_javafx/skDashboard.fxml", "Sparkonto-Dashboard", event);
            controller.setSparKonto(selectedSK);
        }
    }
}
