package bankverwaltungssystem_javafx.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class FensterManager {

    /**
     * Öffnet ein neues Fenster und schließt das aktuelle.
     * @param fxmlPfad Pfad zur FXML-Datei
     * @param titel Titel des neuen Fensters
     * @param ereignis Das Event, das den Wechsel ausgelöst hat
     * @throws IOException falls das FXML nicht gefunden wird
     */
    public static void oeffneFenster(String fxmlPfad, String titel, ActionEvent ereignis) throws IOException {
        FXMLLoader ladehilfe = new FXMLLoader(FensterManager.class.getResource(fxmlPfad));
        Parent wurzel = ladehilfe.load();

        Stage neuesFenster = new Stage();
        neuesFenster.setTitle(titel);
        neuesFenster.setScene(new Scene(wurzel));
        neuesFenster.show();

        Stage aktuellesFenster = (Stage) ((Node) ereignis.getSource()).getScene().getWindow();
        aktuellesFenster.close();
    }

    /**
     * Öffnet ein modales (blockierendes) Fenster.
     * @param fxmlPfad Pfad zur FXML-Datei
     * @param titel Titel des Fensters
     * @throws IOException falls das FXML nicht gefunden wird
     */
    public static void oeffneModalesFenster(String fxmlPfad, String titel) throws IOException {
        FXMLLoader ladehilfe = new FXMLLoader(FensterManager.class.getResource(fxmlPfad));
        Parent wurzel = ladehilfe.load();

        Stage modalesFenster = new Stage();
        modalesFenster.initModality(Modality.APPLICATION_MODAL);
        modalesFenster.setTitle(titel);
        modalesFenster.setScene(new Scene(wurzel));
        modalesFenster.showAndWait();
    }

    /**
     * Wechselt nur die Szene innerhalb des aktuellen Fensters.
     * @param fxmlPfad Pfad zur FXML-Datei
     * @param ereignis Event vom Button oder ähnlichem
     * @throws IOException falls das FXML nicht gefunden wird
     */
    public static void wechsleSzene(String fxmlPfad, ActionEvent ereignis) throws IOException {
        Parent wurzel = FXMLLoader.load(FensterManager.class.getResource(fxmlPfad));
        Scene neueSzene = new Scene(wurzel);

        Stage fenster = (Stage) ((Node) ereignis.getSource()).getScene().getWindow();
        fenster.setScene(neueSzene);
    }
}

