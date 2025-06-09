package bankverwaltungssystem_javafx.controllers;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.FensterManager;
import bankverwaltungssystem_javafx.models.GiroKonto;
import bankverwaltungssystem_javafx.models.Kunde;
import bankverwaltungssystem_javafx.models.KundenService;
import bankverwaltungssystem_javafx.models.SparKonto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller zur Kontoeröffnung und -verwaltung für bereits vorhandene Kunden.
 */
public class KontoAnlegenVKController {

    /** GUI-Komponenten für das Girokonto */
    @FXML private TextField txtGKKontoNr;
    @FXML private TextField txtGKKontostand;
    @FXML private RadioButton btnGKKontoAktivJa;
    @FXML private TextField txtGKSpesen;
    @FXML private TextField txtGKUeberziehungslimit;
    @FXML private TextField txtGKNegativZinssatz;
    @FXML private TextField txtGKPositivZinssatz;

    /** GUI-Komponenten für das Sparkonto */
    @FXML private TextField txtSKKontoNr;
    @FXML private TextField txtSKKontostand;
    @FXML private RadioButton btnSKKontoAktivJa;
    @FXML private TextField txtSKZinssatz;

    /** Der zu verwaltende Kunde */
    private Kunde kunde;

    /**
     * Setzt den Kunden.
     *
     * @param kunde Der ausgewählte Kunde
     */
    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    /** Siehe NK-Version: Girokonto eröffnen */
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
        GKDashboardController controller = FensterManager.oeffneFensterUndHoleController( // damit die Kontodaten auf das GKDashboard übertragen werden
                    "/bankverwaltungssystem_javafx/gkDashboard.fxml", "Girokonto-Dashboard", event);
        controller.setGiroKonto(giroKonto);
        DBManager.closeConnection();
    }

    /** Siehe NK-Version: Sparkonto eröffnen */
    @FXML
    private void sparKontoEroeffnen(ActionEvent event) throws IOException, SQLException {
        String kontoNr = txtSKKontoNr.getText().toUpperCase().trim();
        String kontostand = txtSKKontostand.getText().trim();
        boolean kontoAktiv = btnSKKontoAktivJa.isSelected();
        String zinssatz = txtSKZinssatz.getText().trim();

        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        SparKonto sparKonto = kunde.eroeffneSparKonto(kontoNr, kontostand, kontoAktiv, zinssatz);
        SKDashboardController controller = FensterManager.oeffneFensterUndHoleController( // damit die Kontodaten auf das SKDashboard übertragen werden
                "/bankverwaltungssystem_javafx/skDashboard.fxml", "Sparkonto-Dashboard", event);
        controller.setSparKonto(sparKonto);
        DBManager.closeConnection();
    }

    /**
     * Zeigt vorhandene Girokonten des Kunden an.
     */
    @FXML
    private void girokontenSuchen(ActionEvent event) throws IOException, SQLException {
        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        List<GiroKonto> gefundeneGK = kunde.sucheGiroKonto(kunde.getKundeID(con));

        // Öffne das vorhandeneGirokonto Fenster
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bankverwaltungssystem_javafx/vorhandeneGirokonto.fxml"));
        Parent root = loader.load();

        // Setze die gefundenen Girokonten in die ListView
        VorhandeneGirokontoController controller = loader.getController();
        controller.setGirokontenListe(gefundeneGK);

        Stage stage = new Stage();
        stage.setTitle("Gefundene Konten");
        stage.setScene(new Scene(root));
        stage.show();

        // Braucht man sonst schließt sich die Fenster nicht richtig
        Stage aktuellesFenster = (Stage) ((Node) event.getSource()).getScene().getWindow();
        aktuellesFenster.close();

        DBManager.closeConnection();
    }

    /**
     * Zeigt vorhandene Sparkonten des Kunden an.
     */
    @FXML
    private void sparkontenSuchen(ActionEvent event) throws IOException, SQLException {
        Connection con = DBManager.getConnection();
        kunde = KundenService.getKundeById(con);
        List<SparKonto> gefundeneSK = kunde.sucheSparKonto(kunde.getKundeID(con));

        // Öffne das vorhandeneGirokonto Fenster
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bankverwaltungssystem_javafx/vorhandeneSparkonto.fxml"));
        Parent root = loader.load();

        // Setze die gefundenen Girokonten in die ListView
        VorhandeneSparkontoController controller = loader.getController();
        controller.setSparkontenListe(gefundeneSK);

        Stage stage = new Stage();
        stage.setTitle("Gefundene Konten");
        stage.setScene(new Scene(root));
        stage.show();

        // Braucht man sonst schließt sich die Fenster nicht richtig
        Stage aktuellesFenster = (Stage) ((Node) event.getSource()).getScene().getWindow();
        aktuellesFenster.close();

        DBManager.closeConnection();
    }
}
