package bankverwaltungssystem_javafx.application;

import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern Implementierung:
 * - KontoObserver verwaltet eine Liste von Listenern (UI-Komponenten)
 * - Wenn sich Kontowerte ändern, werden alle registrierten Listener benachrichtigt
 * - Die UI wird automatisch aktualisiert, wenn sich Kontodaten ändern
 * - Platform.runLater() stellt sicher, dass UI-Updates auf dem JavaFX-Thread ausgeführt werden
 */
public class KontoObserver {
    private static final List<Runnable> listeners = new ArrayList<>();

    public static void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public static void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    public static void notifyListeners() {
        Platform.runLater(() -> {
            for (Runnable listener : listeners) {
                listener.run();
            }
        });
    }
} 