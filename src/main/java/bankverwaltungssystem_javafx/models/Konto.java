package bankverwaltungssystem_javafx.models;

import javafx.beans.property.SimpleDoubleProperty;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Die abstrakte Klasse Konto repraesentiert ein Konto mit grundlegenden Methoden und Daten
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
     * Konstruktor fuer die Klasse Konto. Initialisiert die Kontodaten durch Benutzereingaben.
     *
     * @param kontoNr    Die Kontonummer des Kontos.
     * @param kontoStand Der Kontostand des jeweiligen Kontos.
     */
    public Konto(String kontoNr, double kontoStand){
        this.kontoNr = kontoNr;
        this.kontoStand = kontoStand;
        this.kontoAktiv = true;
    }

    /* Dieser Konstruktor dient nur als Test für die BankTest-Klasse
    public Konto(long kontoNr, double kontoStand){
        this.kontoNr = kontoNr;
        this.kontoStand = kontoStand;
        this.kontoAktiv = true;
    }
     */

    /**
     * Abstrakte Methode zum Abrufen der Kontonummer.
     *
     * @return Die Kontonummer des Kontos.
     */
    public abstract String getKontoNr();


    /**
     * Abstrakte Methode zum Abrufen des aktuellen Kontostands.
     *
     * @return Der aktuelle Kontostand des Kontos.
     */
    public abstract double getKontoStand();

    /**
     * Abstrakte Methode zum Ueberpruefen, ob das Konto aktiv ist.
     *
     * @return true, wenn das Konto aktiv ist; false, wenn das Konto nicht aktiv ist.
     */
    public abstract boolean isKontoAktiv();

    /**
     * Abstrakte Methode zum Abrufen der Gesamtsumme der Einzahlungen auf dem Konto.
     *
     * @return Die Gesamtsumme der Einzahlungen.
     */
    public abstract double getSummeEinzahlungen();

    /**
     * Abstrakte Methode zum Abrufen der Gesamtsumme der Auszahlungen von dem Konto.
     *
     * @return Die Gesamtsumme der Auszahlungen.
     */
    public abstract double getSummeAuszahlungen();

    public abstract void setSummeEinzahlungen(double summeEinzahlungen);
    public abstract void setSummeAuszahlungen(double summeAuszahlungen);
    //public abstract void setKontoStand(double kontostand);
    /**
     * Abstrakte Methode zum Durchfuehren einer Auszahlung von einem Konto.
     *
     * @param betrag Der Betrag, der aus dem Konto abgehoben wird.
     * @param con Die Verbindung zur Datenbank.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public abstract void auszahlen(double betrag) throws SQLException;

    /**
     * Abstrakte Methode zum Durchfuehren einer Ueberweisung von diesem Konto auf ein anderes Konto.
     *
     * @param betrag Der zu ueberweisende Betrag.
     * @param konto Das Zielkonto, auf das die Ueberweisung erfolgt.
     * @param con Die Verbindung zur Datenbank.
     * @param kontoNrVersender Die Kontonummer des versendenden Kontos.
     * @param kontoNrEmpfaenger Die Kontonummer des empfangenden Kontos.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public abstract void ueberweisen(double betrag, Konto konto, String kontoNrVersender, String kontoNrEmpfaenger) throws SQLException;

    /**
     * Abstrakte Methode zur Berechnung der jeweiligen Zinsen fuer das Konto.
     *
     * @param con Die Verbindung zur Datenbank.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public abstract void berechneZinsen() throws SQLException;
}
