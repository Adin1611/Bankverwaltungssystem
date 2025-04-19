package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.models.KundenService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.List;

public class VorhandeneKundeController {
    @FXML
    private ListView<Kunde> lvKunde;
    @FXML
    private Button btnOK;

    private KundenService kundenService;


    public void setKundenListe(List<Kunde> kunden) {
        kundenService = new KundenService();
        kundenService.initialisiereListView(lvKunde);
        kundenService.setKundenListe(kunden);
    }

    @FXML
    private void kundeAuswaehlen(ActionEvent event) throws IOException {
        Kunde selectedKunde = lvKunde.getSelectionModel().getSelectedItem();
        if (selectedKunde != null) {
            FensterManager.oeffneFenster("/bankverwaltungssystem_javafx/kontoAnlegenVK.fxml", "Konto anlegen", event);
        }
    }
} 