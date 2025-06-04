package bankverwaltungssystem_javafx.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class FensterManager {

    /**
     * Öffnet ein neues Fenster und schließt das aktuelle.
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
     * Öffnet ein neues Fenster, schließt das aktuelle und gibt den Controller des neuen Fensters zurück.
     * @param fxmlPfad Pfad zur FXML-Datei
     * @param titel Titel des neuen Fensters
     * @param ereignis Das ActionEvent, das den Wechsel ausgelöst hat
     * @return Der Controller des neu geladenen Fensters
     * @throws IOException falls das FXML nicht gefunden wird
     */
    public static <T> T oeffneFensterUndHoleController(String fxmlPfad, String titel, ActionEvent ereignis) throws IOException {
        FXMLLoader ladehilfe = new FXMLLoader(FensterManager.class.getResource(fxmlPfad));
        Parent wurzel = ladehilfe.load();

        Stage neuesFenster = new Stage();
        neuesFenster.setTitle(titel);
        neuesFenster.setScene(new Scene(wurzel));
        neuesFenster.show();

        Stage aktuellesFenster = (Stage) ((Node) ereignis.getSource()).getScene().getWindow();
        aktuellesFenster.close();

        return ladehilfe.getController(); // Hier bekommst du den Ziel-Controller zurück
    }
}
