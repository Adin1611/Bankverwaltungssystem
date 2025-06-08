package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.models.KundenService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.List;

/**
 * Controller zur Anzeige und Auswahl vorhandener Kunden.
 */
public class VorhandeneKundeController {

    /** ListView zur Anzeige der Kunden */
    @FXML private ListView<Kunde> lvKunde;

    /** Kundenservice zur Verwaltung */
    private KundenService kundenService;

    /**
     * Setzt die Kundenliste und initialisiert die Anzeige.
     * @param kunden Liste der Kunden
     */
    public void setKundenListe(List<Kunde> kunden) {
        kundenService = new KundenService();
        kundenService.initialisiereListView(lvKunde);
        kundenService.setKundenListe(kunden);
    }

    /**
     * Öffnet das Fenster zur Kontoerstellung für den ausgewählten Kunden.
     * @param event Das auslösende Ereignis
     * @throws IOException Bei Fehlern beim Laden
     */
    @FXML
    private void kundeAuswaehlen(ActionEvent event) throws IOException {
        Kunde selectedKunde = lvKunde.getSelectionModel().getSelectedItem();
        if (selectedKunde != null) {
            FensterManager.oeffneFenster("/bankverwaltungssystem_javafx/kontoAnlegenVK.fxml", "Konto anlegen", event);
        }
    }
} 