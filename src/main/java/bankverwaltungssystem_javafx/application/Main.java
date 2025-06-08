package bankverwaltungssystem_javafx.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Einstiegspunkt der JavaFX-Anwendung.
 * Lädt die FXML-Datei für das Hauptfenster und zeigt es an.
 */
public class Main extends Application {

    /**
     * Startet die JavaFX-Anwendung und lädt die Hauptoberfläche.
     *
     * @param stage Das Hauptfenster der Anwendung.
     * @throws IOException wenn die FXML-Datei nicht geladen werden kann.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/bankverwaltungssystem_javafx/hauptfenster.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 550);
        stage.setTitle("Bankverwaltungssystem");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Einstiegspunkt der JavaFX-Anwendung.
     *
     * @param args Kommandozeilenargumente (nicht verwendet).
     */
    public static void main(String[] args) {
        launch();
    }
}

