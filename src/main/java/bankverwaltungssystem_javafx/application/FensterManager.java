package bankverwaltungssystem_javafx.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Klasse zur Verwaltung von Fensterwechseln in der JavaFX-Oberfläche.
 */
public class FensterManager {

    /**
     * Öffnet ein neues Fenster basierend auf einer FXML-Datei und schließt das aktuelle Fenster.
     *
     * @param fxmlPfad Pfad zur FXML-Datei.
     * @param titel Titel des neuen Fensters.
     * @param ereignis Das ActionEvent, das den Wechsel ausgelöst hat.
     * @throws IOException falls das FXML nicht geladen werden kann.
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
     * Öffnet ein neues Fenster und gibt dessen zugehörigen Controller zurück.
     * <p>
     * Verwendet einen generischen Rückgabetyp <T>, um den konkreten Controller-Typ typsicher zurückzugeben,
     * ohne explizites Casting.
     * Das heißt, man weiß zur Kompilierzeit noch nicht, welchen genauen Rückgabetyp die Methode haben wird.
     * Stattdessen gibt man an, dass sie einen beliebigen Typ T zurückgeben kann, und dieser Typ wird durch den
     * tatsächlichen Controller bestimmt, der im FXML hinterlegt ist.
     *
     * @param fxmlPfad Pfad zur FXML-Datei.
     * @param titel Titel des neuen Fensters.
     * @param ereignis Das auslösende ActionEvent.
     * @param <T> Deklaration des generischen Typs T (T = tatsaechliche Typ, den die Methode zurückgibt z.B. GKDashboardController)
     * @return Der Controller des neuen Fensters.
     * @throws IOException falls das FXML nicht geladen werden kann.
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

        return ladehilfe.getController();
    }
}

