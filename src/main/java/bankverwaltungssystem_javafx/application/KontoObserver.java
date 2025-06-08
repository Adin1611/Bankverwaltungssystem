package bankverwaltungssystem_javafx.application;

import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementiert ein einfaches Observer-Pattern für Kontoänderungen.
 * Listener (z.B. UI-Komponenten) können registriert werden und werden automatisch benachrichtigt,
 * wenn sich Kontodaten ändern.
 *
 * Observer Pattern Implementierung:
 * - KontoObserver verwaltet eine Liste von Listenern (UI-Komponenten)
 * - Wenn sich Kontowerte ändern, werden alle registrierten Listener benachrichtigt
 * - Die UI wird automatisch aktualisiert, wenn sich Kontodaten ändern
 * - Platform.runLater() stellt sicher, dass UI-Updates auf dem JavaFX-Thread ausgeführt werden
 */
public class KontoObserver {
    private static final List<Runnable> listeners = new ArrayList<>();

    /**
     * Fügt einen Listener hinzu, der benachrichtigt werden soll, wenn sich Kontodaten ändern.
     *
     * @param listener Ein Runnable, das beim Benachrichtigen ausgeführt wird.
     */
    public static void addListener(Runnable listener) {
        listeners.add(listener);
    }

    /**
     * Benachrichtigt alle registrierten Listener.
     * Wird auf dem JavaFX-UI-Thread ausgeführt, um UI-Updates korrekt durchzuführen.
     */
    public static void benachrichtigeListeners() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Runnable listener : listeners) {
                    listener.run();
                }
            }
        });
    }
}