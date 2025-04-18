package bankverwaltungssystem_javafx.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Die Klasse SparKonto erbt von der abstrakten Klasse Konto und repraesentiert ein Sparkonto mit spezifischen Methoden.
 */
public class SparKonto extends Konto{

    /**
     * Der positive Zinssatz fuer dieses Sparkonto.
     */
    private double zinssatz; // ist immer positiv, weil es bei einem Sparkonto nur positive Zinssätze gibt

    /**
     * Erzeugt ein SparKonto-Objekt und initialisiert es mit Benutzereingaben.
     *
     * @param kontoStand Der Kontostand des Sparkontos
     * @param zinssatz Der Zinssatz des Sparkontos
     */
    public SparKonto(String kontoNr, double kontoStand, double zinssatz){
        super(kontoNr,kontoStand);

        this.zinssatz = zinssatz;

        summeEinzahlungen = 0.0;
        summeAuszahlungen = 0.0;
    }

    /* Dieser Konstruktor dient nur als Test für die BankTest-Klasse
    public SparKonto(long kontoNr, double kontoStand, double positivZinssatz){
        super(kontoNr,kontoStand);
        this.positivZinssatz = positivZinssatz;
    }
     */

    @Override
    public String getKontoNr(){
        return this.kontoNr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getKontoStand() {
        return kontoStand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isKontoAktiv() {
        return kontoAktiv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSummeEinzahlungen(){
        return summeEinzahlungen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSummeAuszahlungen(){
        return summeAuszahlungen;
    }

    /**
     * Gibt den Zinssatz des Sparkontos zurueck.
     *
     * @return Der Zinssatz.
     */
    public double getZinssatz() {
        return zinssatz;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void auszahlen(double betrag, Connection con) throws SQLException {
        if (isKontoAktiv()) {
            if (betrag > kontoStand) {
                System.out.println("Man kann nicht auszahlen, da zu wenig Geld auf dem Konto vorhanden ist");
            } else {
                System.out.println("Kontostand nach Auszahlung: " + (kontoStand -= betrag));
                summeAuszahlungen += betrag;

                // Update kontoStand vom jeweiligen Kunden nach Auszahlung in der Sparkonto-Tabelle
                String updateKontostandNachAuszahlungSparkontoSQL = "UPDATE sparkonto SET kontoStand = kontoStand - ? WHERE kontoNr = ?";
                PreparedStatement updateKontostandNachAuszahlungSparkontoStatement = con.prepareStatement(updateKontostandNachAuszahlungSparkontoSQL);
                updateKontostandNachAuszahlungSparkontoStatement.setDouble(1, betrag);
                updateKontostandNachAuszahlungSparkontoStatement.setString(2, this.kontoNr);
                updateKontostandNachAuszahlungSparkontoStatement.executeUpdate();
                updateKontostandNachAuszahlungSparkontoStatement.close();

                // Update summeAuszahlungen nach Auszahlung in der Sparkonto-Tabelle
                String updateSummeAuszahlungenSparkontoSQL = "UPDATE sparkonto SET summeAuszahlungen = summeAuszahlungen + ? WHERE kontoNr = ?";
                PreparedStatement updateSummeAuszahlungenSparkontoStatement = con.prepareStatement(updateSummeAuszahlungenSparkontoSQL);
                updateSummeAuszahlungenSparkontoStatement.setDouble(1, betrag);
                updateSummeAuszahlungenSparkontoStatement.setString(2, this.kontoNr);
                updateSummeAuszahlungenSparkontoStatement.executeUpdate();
                updateSummeAuszahlungenSparkontoStatement.close();
            }
        }else{
            System.out.println("Eine Auszahlung ist nicht möglich da das Konto inaktiv ist");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ueberweisen(double betrag, Konto konto, Connection con, String kontoNrVersender, String kontoNrEmpfaenger) throws SQLException{
        if (konto.isKontoAktiv() && (kontoStand >= betrag)) {

            if (konto instanceof GiroKonto) {
                System.out.println("Zielkonto Kontostand: " + (konto.kontoStand += betrag));
                konto.summeEinzahlungen += betrag;

                // Update kontostand vom Empfängerkonto in der Girokonto-Tabelle
                String updateKontostandEmpfaengerkontoGirokontoSQL = "UPDATE girokonto SET kontostand = kontostand + ? WHERE kontoNr = ?";
                PreparedStatement updateKontostandEmpfaengerkontoGirokontoStatement = con.prepareStatement(updateKontostandEmpfaengerkontoGirokontoSQL);
                updateKontostandEmpfaengerkontoGirokontoStatement.setDouble(1, betrag);
                updateKontostandEmpfaengerkontoGirokontoStatement.setString(2, kontoNrEmpfaenger);
                updateKontostandEmpfaengerkontoGirokontoStatement.executeUpdate();
                updateKontostandEmpfaengerkontoGirokontoStatement.close();

                // Update summeEinzahlungen vom Empfängerkonto in der Girokonto-Tabelle
                String updateSummeEinzahlungenEmpfaengerkontoGirokontoSQL = "UPDATE girokonto SET summeEinzahlungen = summeEinzahlungen + ? WHERE kontoNr = ?";
                PreparedStatement updateSummeEinzahlungenEmpfaengerkontoGirokontoStatement = con.prepareStatement(updateSummeEinzahlungenEmpfaengerkontoGirokontoSQL);
                updateSummeEinzahlungenEmpfaengerkontoGirokontoStatement.setDouble(1, betrag);
                updateSummeEinzahlungenEmpfaengerkontoGirokontoStatement.setString(2, kontoNrEmpfaenger);
                updateSummeEinzahlungenEmpfaengerkontoGirokontoStatement.executeUpdate();
                updateSummeEinzahlungenEmpfaengerkontoGirokontoStatement.close();
            }
            else if (konto instanceof SparKonto){
                System.out.println("Zielkonto Kontostand: " + (konto.kontoStand += betrag));
                konto.summeEinzahlungen += betrag;

                // Update kontostand vom Empfängerkonto in der Sparkonto-Tabelle
                String updateKontostandEmpfaengerkontoSparkontoSQL = "UPDATE sparkonto SET kontostand = kontostand + ? WHERE kontoNr = ?";
                PreparedStatement updateKontostandEmpfaengerkontoSparkontoStatement = con.prepareStatement(updateKontostandEmpfaengerkontoSparkontoSQL);
                updateKontostandEmpfaengerkontoSparkontoStatement.setDouble(1, betrag);
                updateKontostandEmpfaengerkontoSparkontoStatement.setString(2, kontoNrEmpfaenger);
                updateKontostandEmpfaengerkontoSparkontoStatement.executeUpdate();
                updateKontostandEmpfaengerkontoSparkontoStatement.close();

                // Update summeEinzahlungen vom Empfängerkonto in der Sparkonto-Tabelle
                String updateSummeEinzahlungenEmpfaengerkontoSparkontoSQL = "UPDATE sparkonto SET summeEinzahlungen = summeEinzahlungen + ? WHERE kontoNr = ?";
                PreparedStatement updateSummeEinzahlungenEmpfaengerkontoSparkontoStatement = con.prepareStatement(updateSummeEinzahlungenEmpfaengerkontoSparkontoSQL);
                updateSummeEinzahlungenEmpfaengerkontoSparkontoStatement.setDouble(1, betrag);
                updateSummeEinzahlungenEmpfaengerkontoSparkontoStatement.setString(2, kontoNrEmpfaenger);
                updateSummeEinzahlungenEmpfaengerkontoSparkontoStatement.executeUpdate();
                updateSummeEinzahlungenEmpfaengerkontoSparkontoStatement.close();
            }

            System.out.println("Versender-Konto Kontostand: " + (kontoStand -= betrag));
            summeAuszahlungen += betrag;
            // Update kontostand vom Versenderkonto in der Sparkonto-Tabelle
            String updateKontostandVersenderkontoSparkontoSQL = "UPDATE sparkonto SET kontostand = kontostand - ? WHERE kontoNr = ?";
            PreparedStatement updateKontostandVersenderkontoSparkontoStatement = con.prepareStatement(updateKontostandVersenderkontoSparkontoSQL);
            updateKontostandVersenderkontoSparkontoStatement.setDouble(1, betrag);
            updateKontostandVersenderkontoSparkontoStatement.setString(2, kontoNrVersender);
            updateKontostandVersenderkontoSparkontoStatement.executeUpdate();
            updateKontostandVersenderkontoSparkontoStatement.close();

            // Update summeAuszahlungen vom Versenderkonto in der Sparkonto-Tabelle
            String updateSummeAuszahlungenVersenderkontoSparkontoSQL = "UPDATE sparkonto SET summeAuszahlungen = summeAuszahlungen + ? WHERE kontoNr = ?";
            PreparedStatement updateSummeAuszahlungenVersenderkontoSparkontoStatement = con.prepareStatement(updateSummeAuszahlungenVersenderkontoSparkontoSQL);
            updateSummeAuszahlungenVersenderkontoSparkontoStatement.setDouble(1, betrag);
            updateSummeAuszahlungenVersenderkontoSparkontoStatement.setString(2, kontoNrVersender);
            updateSummeAuszahlungenVersenderkontoSparkontoStatement.executeUpdate();
            updateSummeAuszahlungenVersenderkontoSparkontoStatement.close();
        }else{
            System.out.println("Die Überweisung konnte nicht durchgeführt werden. Das Zielkonto ist nicht aktiv oder es ist zu wenig Geld auf dem Konto.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void berechneZinsen(Connection con) throws SQLException{
        System.out.println("Kontostand nach Berücksichtigung der Zinsen: " + String.format("%.2f", kontoStand = kontoStand * (1 + (zinssatz * 0.01))).replace(",","."));
        // Update kontostand vom jeweiligen Konto nach Zinsen in der Sparkonto-Tabelle
        String updateKontostandNachPositivZinsenSparkontoSQL = "UPDATE sparkonto SET kontostand = kontostand * (1 + (? * 0.01)) WHERE kontoNr = ?";
        PreparedStatement updateKontostandNachPositivZinsenSparkontoStatement = con.prepareStatement(updateKontostandNachPositivZinsenSparkontoSQL);
        updateKontostandNachPositivZinsenSparkontoStatement.setDouble(1, zinssatz);
        updateKontostandNachPositivZinsenSparkontoStatement.setString(2, this.kontoNr);
        updateKontostandNachPositivZinsenSparkontoStatement.executeUpdate();
        updateKontostandNachPositivZinsenSparkontoStatement.close();
    }

    /**
     * Fuehrt eine Monatliche-Einzahlung auf das Girokonto durch.
     *
     * @param betrag Der einzuzahlende Betrag.
     * @param con Die Verbindung zur Datenbank
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public void monatlicheEinzahlung(double betrag, Connection con) throws SQLException{
        System.out.println("Kontostand nach Monatliche-Einzahlung: " + (kontoStand += betrag));
        summeEinzahlungen += betrag;

        // Update kontostand vom jeweiligen Konto nach Monatlicher-Einzahlung in der Sparkonto-Tabelle
        String updateKontostandNachEinzahlungSparkontoSQL = "UPDATE sparkonto SET kontostand = kontostand + ? WHERE kontoNr = ?";
        PreparedStatement updateKontostandNachEinzahlungSparkontoStatement = con.prepareStatement(updateKontostandNachEinzahlungSparkontoSQL);
        updateKontostandNachEinzahlungSparkontoStatement.setDouble(1, betrag);
        updateKontostandNachEinzahlungSparkontoStatement.setString(2, this.kontoNr);
        updateKontostandNachEinzahlungSparkontoStatement.executeUpdate();
        updateKontostandNachEinzahlungSparkontoStatement.close();

        // Update summeEinzahlungen vom jeweiligen Konto nach Einzahlung in der Sparkonto-Tabelle
        String updateSummeEinzahlungenNachEinzahlungSparkontoSQL = "UPDATE sparkonto SET summeEinzahlungen = summeEinzahlungen + ? WHERE kontoNr = ?";
        PreparedStatement updateSummeEinzahlungenNachEinzahlungSparkontoStatement = con.prepareStatement(updateSummeEinzahlungenNachEinzahlungSparkontoSQL);
        updateSummeEinzahlungenNachEinzahlungSparkontoStatement.setDouble(1, betrag);
        updateSummeEinzahlungenNachEinzahlungSparkontoStatement.setString(2, this.kontoNr);
        updateSummeEinzahlungenNachEinzahlungSparkontoStatement.executeUpdate();
        updateSummeEinzahlungenNachEinzahlungSparkontoStatement.close();
    }
}
