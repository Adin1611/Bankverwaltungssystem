package bankverwaltungssystem_javafx.application;

import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementiert das Observer-Pattern für Kontoänderungen.
 *
 * Diese Klasse verwaltet eine Liste von Listeners (z.B. Methoden wie updateUI()),
 * die automatisch aufgerufen werden, wenn sich bestimmte Kontodaten ändern
 * – z.B. bei Einzahlungen, Auszahlungen oder Zinsberechnungen.
 *
 * Warum wird es verwendet?
 * Damit sich die Benutzeroberfläche (z.B. Labels für kontostand, summeEinzahlungen, summeAuszahlungen)
 * automatisch aktualisiert, sobald sich die Daten im Hintergrund ändern.
 *
 * Wie funktioniert es?
 * - Wenn z.B. eine Methode wie einzahlen() aufgerufen wird,
 *   werden intern Konto-Werte verändert (z.B. kontoStand, summeEinzahlungen).
 * - Danach wird KontoObserver.benachrichtigeListeners() aufgerufen.
 * - Dadurch wird die Methode updateUI() im zugehörigen Controller ausgeführt,
 *   die die Labels auf der Oberfläche mit den neuen Werten aktualisiert.
 */
public class KontoObserver {

    /**
     * Liste aller registrierten Listener, die bei Änderungen an Kontodaten benachrichtigt werden sollen.
     * Jeder Listener ist ein Runnable, der typischerweise UI-Komponenten aktualisiert.
     */
    private static final List<Runnable> listeners = new ArrayList<>();

    /**
     * Fügt einen Listener (updateUI()) hinzu, der automatisch aufgerufen wird, wenn sich Kontodaten ändern.
     *
     * @param listener Ein Runnable – die Methode, die die UI aktualisiert (updateUI()).
     */
    public static void addListener(Runnable listener) {
        listeners.add(listener);
    }

    /**
     * Benachrichtigt alle registrierten Listener, dass sich die Kontodaten geändert haben.
     * Die Methode, die mit addListener() registriert wurde (updateUI()), wird aufgerufen.
     *
     * Platform.runLater()
     *  -> JavaFX erlaubt UI-Updates nur im sogenannten „UI-Thread“.
     *  -> falls z.B. einzahlen() von einem anderen Thread kommt, sorgt runLater dafür, dass das UI-Update korrekt im
     *     richtigen Thread passiert.
     */
    public static void benachrichtigeListeners() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Runnable listener : listeners) {
                    listener.run(); // updateUI()
                }
            }
        });
    }
}