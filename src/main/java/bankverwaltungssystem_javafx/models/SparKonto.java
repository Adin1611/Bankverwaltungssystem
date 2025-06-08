package bankverwaltungssystem_javafx.models;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.KontoObserver;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Repräsentiert ein Sparkonto in der Bankanwendung.
 * <p>
 * Ein Sparkonto ist ein spezielles Konto mit ausschließlich positiven Zinsen.
 * Es bietet Methoden zum Ein- und Auszahlen, zur Zinsberechnung sowie zur Überweisung.
 * Änderungen am Kontostand oder an Transaktionen benachrichtigen automatisch registrierte Listener
 * mittels des {@link KontoObserver}.
 */
public class SparKonto extends Konto{

    /**
     * Der Zinssatz, der auf Guthaben angewendet wird.
     * Dieser Wert ist immer positiv, da es bei .
     */
    private double zinssatz;

    /**
     * Konstruktor zur Erstellung eines Sparkontos mit Startwerten.
     *
     * @param kontoNr   Die eindeutige Kontonummer.
     * @param kontoStand Der Startkontostand.
     * @param zinssatz  Der positive Zinssatz des Kontos.
     */
    public SparKonto(String kontoNr, double kontoStand, double zinssatz){
        super(kontoNr,kontoStand);

        this.zinssatz = zinssatz;

        summeEinzahlungen = 0.0;
        summeAuszahlungen = 0.0;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public void setSummeEinzahlungen(double summeEinzahlungen){
        this.summeEinzahlungen = summeEinzahlungen;
        KontoObserver.benachrichtigeListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSummeAuszahlungen(double summeAuszahlungen){
        this.summeAuszahlungen = summeAuszahlungen;
        KontoObserver.benachrichtigeListeners();
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
    public void auszahlen(double betrag) throws SQLException {
        Connection con = DBManager.getConnection();
        if (isKontoAktiv()) {
            if (betrag >= kontoStand) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Fehler bei Auszahlung");
                        alert.setHeaderText("Kontostand zu niedrig");
                        alert.setContentText("Die Auszahlung kann nicht durchgeführt werden, da der Kontostand zu niedrig ist");
                        alert.showAndWait();
                    }
                });
            } else {
                kontoStand -= betrag;
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

                // Benachrichtige Observer über die Änderung
                KontoObserver.benachrichtigeListeners();
                DBManager.closeConnection();
            }
        }else{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Fehler bei Auszahlung");
                    alert.setHeaderText("Konto inaktiv");
                    alert.setContentText("Eine Auszahlung ist nicht möglich, da das Konto inaktiv ist.");
                    alert.showAndWait();
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ueberweisen(double betrag, Konto konto, String kontoNrVersender, String kontoNrEmpfaenger) throws SQLException{
        Connection con = DBManager.getConnection();
        try {
            if (konto.isKontoAktiv() && (kontoStand >= betrag)) { // *-1 damit ich den positiven ÜberziehungsLimit auf einen negativen Überziehungs-limit verwandele -> Grund: Vergleich
                if (konto instanceof GiroKonto) {
                    konto.kontoStand += betrag;
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
                    konto.kontoStand += betrag;
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

                kontoStand -= betrag;
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

                // Benachrichtige Observer über die Änderung
                KontoObserver.benachrichtigeListeners();
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Fehler bei Überweisung");
                        alert.setHeaderText("Konto inaktiv oder Kontostand zu niedrig");
                        alert.setContentText("Eine Überweisung ist nicht möglich, da das Zielkonto inaktiv ist oder der Kontostand zu niedrig");
                        alert.showAndWait();
                    }
                });
            }
        } catch (NullPointerException e) { // NullPointerExceotion für falsche KontoNr-Eingabe bzw. Konto nicht vorhanden
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Fehler bei Überweisung");
                    alert.setHeaderText("Ungültige Kontonummer");
                    alert.setContentText("Die angegebene Empfängerkontonummer existiert nicht. Bitte überprüfen Sie die Eingabe.");
                    alert.showAndWait();
                }

            });
        } finally {
            DBManager.closeConnection();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void berechneZinsen() throws SQLException{
        Connection con = DBManager.getConnection();
        if (isKontoAktiv()) {
            kontoStand = kontoStand * (1 + (zinssatz * 0.01));

            // Update kontostand vom jeweiligen Konto nach Zinsen in der Sparkonto-Tabelle
            String updateKontostandNachPositivZinsenSparkontoSQL = "UPDATE sparkonto SET kontostand = kontostand * (1 + (? * 0.01)) WHERE kontoNr = ?";
            PreparedStatement updateKontostandNachPositivZinsenSparkontoStatement = con.prepareStatement(updateKontostandNachPositivZinsenSparkontoSQL);
            updateKontostandNachPositivZinsenSparkontoStatement.setDouble(1, zinssatz);
            updateKontostandNachPositivZinsenSparkontoStatement.setString(2, this.kontoNr);
            updateKontostandNachPositivZinsenSparkontoStatement.executeUpdate();
            updateKontostandNachPositivZinsenSparkontoStatement.close();

            // Benachrichtige Observer über die Änderung
            KontoObserver.benachrichtigeListeners();
            DBManager.closeConnection();
        }else{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Fehler bei Zins-Berechnung");
                    alert.setHeaderText("Konto inaktiv");
                    alert.setContentText("Eine Zins-Berechnung ist nicht möglich, da das Konto inaktiv ist");
                    alert.showAndWait();
                }
            });
        }
    }

    /**
     * Führt eine monatliche automatische Einzahlung auf das Sparkonto durch.
     *
     * @param betrag Der Betrag, der monatlich eingezahlt wird.
     * @throws SQLException wenn ein Fehler beim Datenbankzugriff auftritt.
     */
    public void monatlicheEinzahlung(double betrag) throws SQLException{
        Connection con = DBManager.getConnection();
        if (isKontoAktiv()) {
            kontoStand += betrag;
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

            // Benachrichtige Observer über die Änderung
            KontoObserver.benachrichtigeListeners();
            DBManager.closeConnection();
        }else{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Fehler bei Einzahlung");
                    alert.setHeaderText("Konto inaktiv");
                    alert.setContentText("Eine Einzahlung ist nicht möglich, da das Konto inaktiv ist.");
                    alert.showAndWait();
                }
            });
        }
    }
}