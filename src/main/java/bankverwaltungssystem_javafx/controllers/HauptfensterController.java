package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.models.KundenService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class HauptfensterController {

    @FXML
    private TextField txtNKName;
    @FXML
    private TextField txtNKOrt;
    @FXML
    private TextField txtNKEmail;
    @FXML
    private TextField txtNKID;
    @FXML
    private RadioButton btnNKKredWuerdigkeitJa;
    @FXML
    private TextField txtVKName;

    private KundenService kundenService;

    @FXML
    private void neuenKundenErstellen(ActionEvent event) throws SQLException, IOException {
        String name = txtNKName.getText().toLowerCase().trim();
        String ort = txtNKOrt.getText().toLowerCase().trim();
        String email = txtNKEmail.getText().toLowerCase().trim();
        String id = txtNKID.getText().trim();
        boolean kreWuerdigkeit = btnNKKredWuerdigkeitJa.isSelected();

        kundenService = new KundenService();
        kundenService.erstelleKunde(name, ort, email, id, kreWuerdigkeit);
        FensterManager.oeffneFenster("/bankverwaltungssystem_javafx/kontoAnlegenNK.fxml", "Konto anlegen", event);
    }

    @FXML
    private void kundenSuchen(ActionEvent event) throws IOException, SQLException {
        String name = txtVKName.getText().toLowerCase().trim();
        if (!name.isEmpty()) {
            kundenService = new KundenService();
            List<Kunde> gefundeneKunden = kundenService.sucheKunde(name);

            // Öffne das vorhandeneKunde Fenster
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bankverwaltungssystem_javafx/vorhandeneKunde.fxml"));
            Parent root = loader.load();
            
            // Setze die gefundenen Kunden in die ListView
            VorhandeneKundeController controller = loader.getController();
            controller.setKundenListe(gefundeneKunden);

            Stage stage = new Stage();
            stage.setTitle("Gefundene Kunde(n)");
            stage.setScene(new Scene(root));
            stage.show();

            // Braucht man sonst schließen sich die Fenster nicht richtig
            Stage aktuellesFenster = (Stage) ((Node) event.getSource()).getScene().getWindow();
            aktuellesFenster.close();

        }
    }
}
