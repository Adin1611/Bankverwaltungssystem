package bankverwaltungssystem_javafx.models;

import bankverwaltungssystem_javafx.application.DBManager;
import bankverwaltungssystem_javafx.application.KontoObserver;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.beans.property.SimpleDoubleProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Die Klasse GiroKonto erbt von der abstrakten Klasse Konto und repraesentiert ein Girokonto mit spezifischen Methoden.
 */
public class GiroKonto extends Konto {

    /**
     * Die maximale Hoehe, bis zu der das Girokonto ueberzogen werden kann.
     */
    private double ueberziehungsLimit;

    /**
     * Der Zinssatz, der auf einen negativen Kontostand angewendet wird.
     */
    private double negativZinssatz;

    /**
     * Der Zinssatz, der auf einen positiven Kontostand angewendet wird.
     */
    private double positivZinssatz;

    /**
     * Die Spesen, die mit dem Girokonto verbunden sind.
     */
    private double spesen;


    /**
     * Erzeugt ein GiroKonto-Objekt und initialisiert es mit Benutzereingaben.
     *
     * @param kontoStand Der Kontostand des Girokontos
     * @param ueberziehungsLimit Das Ueberziehungslimit des Girokontos
     * @param negativZinssatz Der Negativ-Zinssatz des Girokontos
     * @param positivZinssatz Der Positiv-Zinssatz des Girokontos
     * @param spesen Die Spesen des Girokontos
     */
    public GiroKonto(String kontoNr, double kontoStand, double ueberziehungsLimit, double negativZinssatz, double positivZinssatz,
                     double spesen){
        super(kontoNr,kontoStand);

        this.ueberziehungsLimit = ueberziehungsLimit;
        this.negativZinssatz = negativZinssatz;
        this.positivZinssatz = positivZinssatz;
        this.spesen = spesen;

        summeEinzahlungen = 0.0;
        summeAuszahlungen = 0.0;
    }

    /* Dieser Konstruktor dient nur als Test für die BankTest-Klasse
    public GiroKonto(long kontoNr, double kontoStand, double spesen, double ueberziehungsLimit, double negativZinssatz, double positivZinssatz) {
        super(kontoNr,kontoStand);
        this.spesen = spesen;
        this.ueberziehungsLimit = ueberziehungsLimit;
        this.negativZinssatz = negativZinssatz;
        this.positivZinssatz = positivZinssatz;
        this.summeEinzahlungen = 0.0;
        this.summeAuszahlungen = 0.0;
    }
     */

    /*
    public String getKontoNr(Connection con) throws SQLException {
        String kontoNrHolen = "SELECT kontoNr FROM girokonto INNER JOIN kunde ON girokonto.kid = kunde.kid WHERE kontoNr = ?";
        PreparedStatement psKontoNrHolen = con.prepareStatement(kontoNrHolen);
        psKontoNrHolen.setString(1, th);
        ResultSet rsKontoNrHolen = psKontoNrHolen.executeQuery();

        String kontoNr = "";
        if (rsKontoNrHolen.next()) {
            kontoNr = rsKontoNrHolen.getString("kontoNr");
        }

        psKontoNrHolen.close();
        rsKontoNrHolen.close();
        return kontoNr;
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

    @Override
    public void setSummeEinzahlungen(double summeEinzahlungen){
        this.summeEinzahlungen = summeEinzahlungen;
        KontoObserver.notifyListeners();
    }

    @Override
    public void setSummeAuszahlungen(double summeAuszahlungen){
        this.summeAuszahlungen = summeAuszahlungen;
        KontoObserver.notifyListeners();
    }

    /*
    @Override
    public void setKontoStand(double kontoStand) {
        this.kontoStand = kontoStand;
        KontoObserver.notifyListeners();
    }
     */

    /**
     * Gibt das Ueberziehungslimit des Girokontos zurueck.
     *
     * @return Das Ueberziehungslimit des Girokontos.
     */
    public double getUeberziehungsLimit() {
        return ueberziehungsLimit;
    }

    /**
     * Gibt den Negativ-Zinssatz des Girokontos zurueck.
     *
     * @return Der Negativ-Zinssatz des Girokontos.
     */
    public double getNegativZinssatz() {
        return negativZinssatz;
    }

    /**
     * Gibt den Positiv-Zinssatz des Girokontos zurueck.
     *
     * @return Der Positiv-Zinssatz des Girokontos.
     */
    public double getPositivZinssatz() {
        return positivZinssatz;
    }

    /**
     * Gibt die Spesen des Girokontos zurueck.
     *
     * @return Die Spesen des Girokontos.
     */
    public double getSpesen() {
        return spesen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void auszahlen(double betrag) throws SQLException {
        Connection con = DBManager.getConnection();
        if (isKontoAktiv()) {
            if (betrag > (kontoStand + this.ueberziehungsLimit)) {
                // Platform.runLater() wird benötigt, weil:
                // 1. JavaFX erfordert, dass alle UI-Operationen auf dem JavaFX Application Thread ausgeführt werden
                // 2. Die auszahlen()-Methode wird möglicherweise von einem anderen Thread aufgerufen
                // 3. Ohne Platform.runLater() würde eine IllegalStateException mit "Not on FX application thread" geworfen werden
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Fehler bei Auszahlung");
                        alert.setHeaderText("Überziehungslimit überschritten");
                        alert.setContentText("Die Auszahlung kann nicht durchgeführt werden, da das Überziehungslimit überschritten würde.");
                        alert.showAndWait();
                    }
                });
            } else {
                kontoStand -= betrag;
                summeAuszahlungen += betrag;

                // Update kontoStand vom jeweiligen Kunden nach Auszahlung in der Girokonto-Tabelle
                String updateKontostandNachAuszahlungGirokontoSQL = "UPDATE girokonto SET kontoStand = kontoStand - ? WHERE kontoNr = ? ";
                PreparedStatement updateKontostandNachAuszahlungGirokontoStatement = con.prepareStatement(updateKontostandNachAuszahlungGirokontoSQL);
                updateKontostandNachAuszahlungGirokontoStatement.setDouble(1, betrag);
                updateKontostandNachAuszahlungGirokontoStatement.setString(2, this.kontoNr);
                updateKontostandNachAuszahlungGirokontoStatement.executeUpdate();
                updateKontostandNachAuszahlungGirokontoStatement.close();

                // Update summeAuszahlungen nach Auszahlung in der Girokonto-Tabelle
                String updateSummeAuszahlungenGirokontoSQL = "UPDATE girokonto SET summeAuszahlungen = summeAuszahlungen + ? WHERE kontoNr = ?";
                PreparedStatement updateSummeAuszahlungenGirokontoStatement = con.prepareStatement(updateSummeAuszahlungenGirokontoSQL);
                updateSummeAuszahlungenGirokontoStatement.setDouble(1, betrag);
                updateSummeAuszahlungenGirokontoStatement.setString(2, this.kontoNr);
                updateSummeAuszahlungenGirokontoStatement.executeUpdate();
                updateSummeAuszahlungenGirokontoStatement.close();

                // Benachrichtige Observer über die Änderung
                KontoObserver.notifyListeners();
                DBManager.closeConnection();
            }
        } else {
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
        if (konto.isKontoAktiv() && ((kontoStand + ueberziehungsLimit) >= betrag)) { // *-1 damit ich den positiven ÜberziehungsLimit auf einen negativen Überziehungs-limit verwandele -> Grund: Vergleich

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
            // Update kontostand vom Versenderkonto in der Girokonto-Tabelle
            String updateKontostandVersenderkontoGirokontoSQL = "UPDATE girokonto SET kontostand = kontostand - ? WHERE kontoNr = ?";
            PreparedStatement updateKontostandVersenderkontoGirokontoStatement = con.prepareStatement(updateKontostandVersenderkontoGirokontoSQL);
            updateKontostandVersenderkontoGirokontoStatement.setDouble(1, betrag);
            updateKontostandVersenderkontoGirokontoStatement.setString(2, kontoNrVersender);
            updateKontostandVersenderkontoGirokontoStatement.executeUpdate();
            updateKontostandVersenderkontoGirokontoStatement.close();
            // Update summeAuszahlungen vom Versenderkonto in der Girokonto-Tabelle
            String updateSummeAuszahlungenVersenderkontoGirokontoSQL = "UPDATE girokonto SET summeAuszahlungen = summeAuszahlungen + ? WHERE kontoNr = ?";
            PreparedStatement updateSummeAuszahlungenVersenderkontoGirokontoStatement = con.prepareStatement(updateSummeAuszahlungenVersenderkontoGirokontoSQL);
            updateSummeAuszahlungenVersenderkontoGirokontoStatement.setDouble(1, betrag);
            updateSummeAuszahlungenVersenderkontoGirokontoStatement.setString(2, kontoNrVersender);
            updateSummeAuszahlungenVersenderkontoGirokontoStatement.executeUpdate();
            updateSummeAuszahlungenVersenderkontoGirokontoStatement.close();

            // Benachrichtige Observer über die Änderung
            KontoObserver.notifyListeners();
            DBManager.closeConnection();
        }else{
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void berechneZinsen() throws SQLException {
        Connection con = DBManager.getConnection();
        if (isKontoAktiv()) {
            if (kontoStand < 0) {
                kontoStand = kontoStand * (1 + (negativZinssatz * 0.01)); // * 0.01 damit wenn ich z.b 20% eingebe, es sich in 0.2 umwandelt (-> Zinsrechnen)
                // Update kontostand vom jeweiligen Konto nach Negativ-Zinsen in der Girokonto-Tabelle
                String updateKontostandNachNegativZinsenGirokontoSQL = "UPDATE girokonto SET kontostand = kontostand * (1 + (? * 0.01)) WHERE kontoNr = ?";
                PreparedStatement updateKontostandNachNegativZinsenGirokontoStatement = con.prepareStatement(updateKontostandNachNegativZinsenGirokontoSQL);
                updateKontostandNachNegativZinsenGirokontoStatement.setDouble(1, negativZinssatz);
                updateKontostandNachNegativZinsenGirokontoStatement.setString(2, this.kontoNr);
                updateKontostandNachNegativZinsenGirokontoStatement.executeUpdate();
                updateKontostandNachNegativZinsenGirokontoStatement.close();
            } else if (kontoStand > 0) {
                kontoStand = kontoStand * (1 + (positivZinssatz * 0.01));
                // Update kontostand vom jeweiligen Konto nach Negativ-Zinsen in der Girokonto-Tabelle
                String updateKontostandNachPositivZinsenGirokontoSQL = "UPDATE girokonto SET kontostand = kontostand * (1 + (? * 0.01)) WHERE kontoNr = ?";
                PreparedStatement updateKontostandNachPositivZinsenGirokontoStatement = con.prepareStatement(updateKontostandNachPositivZinsenGirokontoSQL);
                updateKontostandNachPositivZinsenGirokontoStatement.setDouble(1, positivZinssatz);
                updateKontostandNachPositivZinsenGirokontoStatement.setString(2, this.kontoNr);
                updateKontostandNachPositivZinsenGirokontoStatement.executeUpdate();
                updateKontostandNachPositivZinsenGirokontoStatement.close();
            }

            // Benachrichtige Observer über die Änderung
            KontoObserver.notifyListeners();
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
     * Fuehrt eine Einzahlung auf das Girokonto durch.
     *
     * @param betrag Der einzuzahlende Betrag.
     * @param con Die Verbindung zur Datenbank
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public void einzahlen(double betrag) throws SQLException{
        Connection con = DBManager.getConnection();
        if (isKontoAktiv()) {
            kontoStand += betrag;
            summeEinzahlungen += betrag;

            // Update kontostand vom jeweiligen Konto nach Einzahlung in der Girokonto-Tabelle
            String updateKontostandNachEinzahlungGirokontoSQL = "UPDATE girokonto SET kontostand = kontostand + ? WHERE kontoNr = ?";
            PreparedStatement updateKontostandNachEinzahlungGirokontoStatement = con.prepareStatement(updateKontostandNachEinzahlungGirokontoSQL);
            updateKontostandNachEinzahlungGirokontoStatement.setDouble(1, betrag);
            updateKontostandNachEinzahlungGirokontoStatement.setString(2, this.kontoNr);
            updateKontostandNachEinzahlungGirokontoStatement.executeUpdate();
            updateKontostandNachEinzahlungGirokontoStatement.close();

            // Update summeEinzahlungen vom jeweiligen Konto nach Einzahlung in der Girokonto-Tabelle
            String updateSummeEinzahlungenNachEinzahlungGirokontoSQL = "UPDATE girokonto SET summeEinzahlungen = summeEinzahlungen + ? WHERE kontoNr = ?";
            PreparedStatement updateSummeEinzahlungenNachEinzahlungGirokontoStatement = con.prepareStatement(updateSummeEinzahlungenNachEinzahlungGirokontoSQL);
            updateSummeEinzahlungenNachEinzahlungGirokontoStatement.setDouble(1, betrag);
            updateSummeEinzahlungenNachEinzahlungGirokontoStatement.setString(2, this.kontoNr);
            updateSummeEinzahlungenNachEinzahlungGirokontoStatement.executeUpdate();
            updateSummeEinzahlungenNachEinzahlungGirokontoStatement.close();

            // Benachrichtige Observer über die Änderung
            KontoObserver.notifyListeners();
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

    /**
     * Zieht die Spesen von dem Girokonto ab.
     *
     * @param con Die Verbindung zur Datenbank
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public void spesenAbziehen() throws SQLException {
        Connection con = DBManager.getConnection();
        if (kontoStand <= 0){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Fehler bei Spesen-Abzug");
                    alert.setHeaderText("Kontostand zu niedrig");
                    alert.setContentText("Ein Spesen-Abzug ist nicht möglich, da der Kontostand zu niedrig ist");
                    alert.showAndWait();
                }
            });
        }else {
            System.out.println("Kontostand nach Abzug der Spesen: " + (kontoStand -= this.spesen));
            // Update kontostand vom jeweiligen Konto nach Abzug der Spesen in der Girokonto-Tabelle
            String updateKontostandNachAbzugSpesenGirokontoSQL = "UPDATE girokonto SET kontostand = kontostand - ? WHERE kontoNr = ?";
            PreparedStatement updateKontostandNachAbzugSpesenGirokontoStatement = con.prepareStatement(updateKontostandNachAbzugSpesenGirokontoSQL);
            updateKontostandNachAbzugSpesenGirokontoStatement.setDouble(1, this.spesen);
            updateKontostandNachAbzugSpesenGirokontoStatement.setString(2, this.kontoNr);
            updateKontostandNachAbzugSpesenGirokontoStatement.executeUpdate();
            updateKontostandNachAbzugSpesenGirokontoStatement.close();

            // Benachrichtige Observer über die Änderung
            KontoObserver.notifyListeners();
            DBManager.closeConnection();
        }
    }
}
