package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.models.GiroKonto;
import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.models.KundenService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller zur Anzeige und Auswahl vorhandener Girokonten.
 */
public class VorhandeneGirokontoController {

    /** ListView zur Anzeige der Girokonten */
    @FXML private ListView<GiroKonto> lvGirokonto;

    /** Der zugehörige Kunde */
    private Kunde kunde;

    /**
     * Setzt die Girokontenliste und initialisiert die Anzeige.
     * @param girokonten Liste der Girokonten
     * @throws SQLException Bei Datenbankfehlern
     */
    public void setGirokontenListe(List<GiroKonto> girokonten) throws SQLException {
        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        kunde.initialisiereListViewGK(lvGirokonto);
        kunde.setGirokontenListe(girokonten);
        DBManager.closeConnection();
    }

    /**
     * Öffnet das Dashboard für das ausgewählte Girokonto.
     * @param event Das auslösende Ereignis
     * @throws IOException Wenn das Fenster nicht geladen werden kann
     * @throws SQLException Bei Datenbankfehlern
     */
    @FXML
    private void girokontoAuswaehlen(ActionEvent event) throws IOException, SQLException {
        GiroKonto selectedGK = lvGirokonto.getSelectionModel().getSelectedItem();
        if (selectedGK != null) {
            GKDashboardController controller = FensterManager.oeffneFensterUndHoleController(
                    "/bankverwaltungssystem_javafx/gkDashboard.fxml", "Girokonto-Dashboard", event);
            controller.setGiroKonto(selectedGK);
        }
    }
}
