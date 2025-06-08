package bankverwaltungssystem_javafx.models;

import java.sql.SQLException;

/**
 * Die abstrakte Klasse Konto bildet die Basis für verschiedene Kontotypen
 * wie {@link GiroKonto} oder {@link SparKonto}. Sie enthält allgemeine Eigenschaften
 * und abstrakte Methoden zur Verwaltung eines Kontos.
 *
 * <p>Diese Klasse folgt dem Prinzip der Vererbung und stellt eine einheitliche Schnittstelle
 * für alle Kontotypen zur Verfügung.</p>
 */
public abstract class Konto {

    /**
     * Die Kontonummer des Kontos.
     */
    String kontoNr;

    /**
     * Der aktuelle Kontostand des Kontos.
     */
    double kontoStand;

    /**
     * Ein Wert, welcher angibt, ob das Konto aktiv ist oder nicht.
     */
    boolean kontoAktiv;

    /**
     * Die Gesamtsumme der Einzahlungen auf dem Konto.
     */
    double summeEinzahlungen;

    /**
     * Die Gesamtsumme der Auszahlungen von dem Konto.
     */
    double summeAuszahlungen;

    /**
     * Konstruktor zur Initialisierung eines Kontos mit Kontonummer und Startguthaben.
     * Das Konto wird standardmäßig als aktiv gesetzt.
     *
     * @param kontoNr    Die Kontonummer
     * @param kontoStand Der Kontostand
     */
    public Konto(String kontoNr, double kontoStand){
        this.kontoNr = kontoNr;
        this.kontoStand = kontoStand;
        this.kontoAktiv = true;
    }

    /**
     * Gibt die Kontonummer zurück.
     *
     * @return die Kontonummer
     */
    public abstract String getKontoNr();


    /**
     * Gibt den aktuellen Kontostand zurück.
     *
     * @return der Kontostand
     */
    public abstract double getKontoStand();

    /**
     * Überprüft, ob das Konto aktiv ist.
     *
     * @return true, wenn das Konto aktiv ist, sonst false
     */
    public abstract boolean isKontoAktiv();

    /**
     * Gibt die gesamte Einzahlungssumme auf das Konto zurück.
     *
     * @return die Summe der Einzahlungen
     */
    public abstract double getSummeEinzahlungen();

    /**
     * Gibt die gesamte Auszahlungssumme vom Konto zurück.
     *
     * @return die Summe der Auszahlungen
     */
    public abstract double getSummeAuszahlungen();

    /**
     * Setzt die Summe der Einzahlungen und informiert ggf. Beobachter.
     *
     * @param summeEinzahlungen der neue Wert für SummeEinzahlungen
     */
    public abstract void setSummeEinzahlungen(double summeEinzahlungen);

    /**
     * Setzt die Summe der Auszahlungen und informiert ggf. Beobachter.
     *
     * @param summeAuszahlungen der neue Wert für SummeAuszahlungen
     */
    public abstract void setSummeAuszahlungen(double summeAuszahlungen);

    /**
     * Führt eine Auszahlung vom Konto durch, sofern die Bedingungen erfüllt sind.
     * <p>
     * Diese Methode aktualisiert auch die Datenbank entsprechend.
     *
     * @param betrag Der abzuhebende Betrag
     * @throws SQLException bei einem Datenbankfehler
     */
    public abstract void auszahlen(double betrag) throws SQLException;

    /**
     * Führt eine Überweisung an ein anderes Konto durch.
     *
     * @param betrag            Der zu überweisende Betrag
     * @param konto             Das Empfängerkonto
     * @param kontoNrVersender  Kontonummer des Absenders
     * @param kontoNrEmpfaenger Kontonummer des Empfängers
     * @throws SQLException bei einem Datenbankfehler
     */
    public abstract void ueberweisen(double betrag, Konto konto, String kontoNrVersender, String kontoNrEmpfaenger) throws SQLException;

    /**
     * Berechnet die Zinsen für das Konto abhängig vom Kontostand und aktualisiert diesen.
     *
     * @throws SQLException bei einem Datenbankfehler
     */
    public abstract void berechneZinsen() throws SQLException;
}
