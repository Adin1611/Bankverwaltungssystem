package bankverwaltungssystem_javafx.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

/**
 * Die Klasse Kunde repraesentiert einen Bankkunden und enthaelt Informationen wie Name, Wohnort,
 * E-Mail-Adresse, Kreditwuerdigkeit und Kunden-ID. Sie ermoeglicht auch die Eroeffnung von Girokonten
 * und Sparkonten fuer den Kunden.
 */
public class Kunde {

    /**
     * Der Name des Kunden.
     */
    private String name;

    /**
     * Der Wohnort des Kunden.
     */
    private String ort;

    /**
     * Die E-Mail-Adresse des Kunden.
     */
    private String email;

    /**
     * Die Identifikationsnummer des Kunden.
     */
    private String identifikationsNr;

    /**
     * Die Kreditwuerdigkeit des Kunden.
     */
    private boolean kreWuerdigkeit;


    /**
     * Erzeugt ein Kunde-Objekt und initialisiert es mit Benutzereingaben.
     *
     * @param name Der Name des Kunden.
     * @param ort Der Ort des Kunden.
     * @param email Die E-Mail des Kunden.
     * @param identifikationsNr Die Identifikationsnummer des Kunden.
     * @param kreWuerdigkeit Die Kreditwuerdigkeit des Kunden.
     */
    public Kunde(String name, String ort, String email, String identifikationsNr, boolean kreWuerdigkeit) {
        this.name = name;
        this.ort = ort;
        this.email = email;
        this.identifikationsNr = identifikationsNr;
        this.kreWuerdigkeit = kreWuerdigkeit;
    }

    /* Dieser Konstruktor dient nur als Test für die BankTest-Klasse
    public Kunde(String name, String ort, String email, boolean kreWuerdigkeit, int kundeID){
        this.name = name;
        this.ort = ort;
        this.email = email;
        this.kreWuerdigkeit = kreWuerdigkeit;
        this.kundeID = kundeID;
    }
     */

    /**
     * Liefert den Namen des Kunden.
     *
     * @return Der Name des Kunden.
     */
    public String getName(){
        return name;
    }

    /**
     * Liefert den Wohnort des Kunden.
     *
     * @return Der Wohnort des Kunden.
     */
    public String getOrt(){
        return ort;
    }

    /**
     * Liefert die Identifikationsnummer des Kunden.
     *
     * @return Die Identifikationsnummer des Kunden.
     */
    public String getIdentifikationsNr(){
        return identifikationsNr;
    }

    /**
     * Liefert die Kunden-ID anhand der Identifikationsnummer aus der Datenbank.
     *
     * @param con Die Verbindun zur Datenbank
     * @return Die Kunden-ID.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public int getKundeID(Connection con) throws SQLException{
        String abfrageId = "SELECT kid FROM kunde WHERE identifikationsNr = '" + identifikationsNr + "'";
        PreparedStatement psKundeID = con.prepareStatement(abfrageId);
        ResultSet rsKundeID = psKundeID.executeQuery();
        int kid = 0;

        if (rsKundeID.next()) {
            kid = rsKundeID.getInt("kid"); // getInt, um den Wert direkt als Integer abzurufen
        }

        psKundeID.close();
        rsKundeID.close();
        return kid;
    }

    /**
     * Gibt eine Zeichenkettendarstellung des Kunden-Objekts zurueck.
     *
     * @param con Die Verbindung zur Datenbank.
     * @return Eine Zeichenkettendarstellung des Kunden.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public String toString(Connection con) throws SQLException{
        return "Kunde{" +
                "Name:'" + name + '\'' +
                ", Ort:'" + ort + '\'' +
                ", Email:'" + email + '\'' +
                ", Kreditwuerdigkeit:" + kreWuerdigkeit +
                ", KundeID:" + getKundeID(con) +
                "}";
    }

    /**
     * Eroeffnet ein neues Girokonto fuer den Kunden.
     *
     * @param con Die Verbindung zur Datenbank
     * @param kid Die Kunden-ID.
     * @param isUberweisung Gibt an, ob es sich um eine Überweisung handelt.
     * @return Das neu eroeffnete Girokonto.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public GiroKonto eroeffneGiroKonto(Connection con, int kid, boolean isUberweisung) throws SQLException {
        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US);

        System.out.println("Geben Sie die KontoNr ein: ");
        String kontoNr = sc.nextLine().toUpperCase();

        // Überprüfung des Kontostands vor dem Einlesen
        double tempKontoStand;
        do {
            System.out.println("Geben Sie den Kontostand ein: ");
            tempKontoStand = sc.nextDouble();

            if (tempKontoStand < 0) {
                System.out.println("Ungültige Eingabe. Kontostand darf nicht negativ sein. Bitte versuchen Sie es erneut.");
            }
        } while (tempKontoStand < 0);

        double kontoStand = tempKontoStand;

        boolean kontoAktiv = true;

        System.out.println("Geben Sie die Spesen für dieses GiroKonto ein: ");
        double spesen = sc.nextDouble();

        System.out.println("Geben Sie das ÜberziehungsLimit für dieses GiroKonto ein: ");
        double ueberziehungsLimit = sc.nextDouble();

        System.out.println("Geben Sie den Negativ-Zinssatz für dieses GiroKonto ein: ");
        double negativZinssatz = sc.nextDouble();

        System.out.println("Gebe Sie den Positiv-Zinssatz für dieses Konto ein: ");
        double positivZinssatz = sc.nextDouble();

        // Überprüfen, ob es das Girokonto schon gibt, also ob das Girokonto schon in der Datenbank vorhanden ist
        String abfrageGiroKonto = "SELECT kontoNr FROM girokonto WHERE kontoNr = ?";
        PreparedStatement psAbfrageGiroKonto = con.prepareStatement(abfrageGiroKonto);
        psAbfrageGiroKonto.setString(1, kontoNr);
        ResultSet rsAbfrageGiroKonto = psAbfrageGiroKonto.executeQuery();

        if (rsAbfrageGiroKonto.next()) {
            if (!isUberweisung) {
                System.out.println("Das Girokonto bzw. Kontonummer des Girokontos ist schon in der Datenbank vorhanden/uebergeben.\nTippe 1 um ein neues" +
                        " Girokonto (mit einer anderen Kontonummer) zu erstellen.\nTippe 2 um mit dem bereits vorhandenen/erstellten Girokonto weiterzuarbeiten " +
                        "(Das geht nur wenn der Kunde mehrere Konten besitzt).");
                int auswahlGiroKonto = sc.nextInt();
                switch (auswahlGiroKonto) {
                    case 1:
                        return eroeffneGiroKonto(con, kid, isUberweisung);
                    case 2:
                        String giroKontoDaten = "SELECT * FROM girokonto WHERE kontoNr = ?";
                        PreparedStatement psGiroKontoDaten = con.prepareStatement(giroKontoDaten);
                        psGiroKontoDaten.setString(1, kontoNr);
                        ResultSet rsGiroKontoDaten = psGiroKontoDaten.executeQuery();

                        if (rsGiroKontoDaten.next()) { // Hole ich mir die einzelnen Daten vom Girokonto (also kontoNr, kontoStand, ueberziehungsLimit,
                            // negativZinssatz, positivZinssatz, spesen) vom ResultSet
                            String abgerufeneKontoNr = rsGiroKontoDaten.getString("kontoNr");
                            double abgerufeneKontoStand = rsGiroKontoDaten.getDouble("kontoStand");
                            double abgerufeneUeberziehungsLimit = rsGiroKontoDaten.getDouble("ueberziehungsLimit");
                            double abgerufeneNegativZinssatz = rsGiroKontoDaten.getDouble("negativZinssatz");
                            double abgerufenePositivZinssatz = rsGiroKontoDaten.getDouble("positivZinssatz");
                            double abgerufeneneSpesen = rsGiroKontoDaten.getDouble("spesen");
                            return new GiroKonto(abgerufeneKontoNr, abgerufeneKontoStand, abgerufeneUeberziehungsLimit, abgerufeneNegativZinssatz,
                                    abgerufenePositivZinssatz, abgerufeneneSpesen);
                        }
                        psGiroKontoDaten.close();
                        rsGiroKontoDaten.close();
                        break;
                }
            }
            else {
                System.out.println("Das Girokonto bzw. Kontonummer des Girokontos ist schon in der Datenbank vorhanden/uebergeben. Sie müssen ein neues" +
                        "Girokonto mit einer anderen Kontonummer erstellen");
                return eroeffneGiroKonto(con,kid,isUberweisung);
            }
        }
        else {
            // Daten in die Datenbank einfügen
            String sqlDaten = "INSERT INTO girokonto (kontoNr, kontoStand, kontoAktiv, summeEinzahlungen, summeAuszahlungen, " +
                    "ueberziehungsLimit, negativZinssatz, positivZinssatz, spesen, kid) VALUES (?,?,?,0,0,?,?,?,?,?)";
            PreparedStatement psDaten = con.prepareStatement(sqlDaten);
            psDaten.setString(1, kontoNr);
            psDaten.setDouble(2, kontoStand);
            psDaten.setBoolean(3, kontoAktiv);
            psDaten.setDouble(4, ueberziehungsLimit);
            psDaten.setDouble(5, negativZinssatz);
            psDaten.setDouble(6, positivZinssatz);
            psDaten.setDouble(7, spesen);
            psDaten.setInt(8, kid);
            psDaten.executeUpdate();
            psDaten.close();
        }
        psAbfrageGiroKonto.close();
        rsAbfrageGiroKonto.close();
        return new GiroKonto(kontoNr,kontoStand,ueberziehungsLimit,negativZinssatz,positivZinssatz,spesen);
    }

    /* Diese Methode dient nur als Test für die BankTest-Klasse
    public GiroKonto eroeffneGiroKonto(long kontoNr, double kontoStand, double spesen, double ueberziehungsLimit, double negativZinssatz, double positivZinssatz) {
        return new GiroKonto(kontoNr,kontoStand,spesen,ueberziehungsLimit,negativZinssatz,positivZinssatz);
    }
     */

    /**
     * Eroeffnet ein neues Sparkonto fuer den Kunden.
     *
     * @param con Die Verbindung zur Datenbank.
     * @param kid Die Kunden-ID.
     * @param isUeberweisung Gibt an, ob es sich um eine Überweisung handelt.
     * @return Das neu eroeffnete Sparkonto.
     * @throws SQLException Wenn ein Datenbankfehler auftritt.
     */
    public SparKonto eroeffneSparKonto(Connection con, int kid, boolean isUeberweisung) throws SQLException{
        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US);

        System.out.println("Geben Sie die KontoNr ein: ");
        String kontoNr = sc.nextLine().toUpperCase();

        // Überprüfung des Kontostands vor dem Einlesen
        double tempKontoStand;
        do {
            System.out.println("Geben Sie den Kontostand ein: ");
            tempKontoStand = sc.nextDouble();

            if (tempKontoStand < 0) {
                System.out.println("Ungültige Eingabe. Kontostand darf nicht negativ sein. Bitte versuchen Sie es erneut.");
            }
        } while (tempKontoStand < 0);

        double kontoStand = tempKontoStand;

        boolean kontoAktiv = true;

        System.out.println("Geben Sie den Zinssatz für dieses Sparkonto ein: ");
        double zinssatz = sc.nextDouble();

        // Überprüfen ob es das Sparkonto schon gibt, also ob das Sparkonto schon in der Datenbank vorhanden ist
        String abfrageSparKonto = "SELECT kontoNr FROM sparkonto WHERE kontoNr = ?";
        PreparedStatement psAbfrageSparKonto = con.prepareStatement(abfrageSparKonto);
        psAbfrageSparKonto.setString(1, kontoNr);
        ResultSet rsAbfrageSparKonto = psAbfrageSparKonto.executeQuery();

        if (rsAbfrageSparKonto.next()) {
            if (!isUeberweisung) {
                System.out.println("Das Sparkonto bzw. die Kontonummer des Sparkontos ist schon in der Datenbank vorhanden/uebergeben.\nTippe 1 um ein neues" +
                        " Sparkonto (mit einer anderen Kontonummer) zu erstellen.\nTippe 2 um mit dem bereits vorhandenen/erstellten Sparkonto weiterzuarbeiten" +
                        "(Das geht nur wenn der Kunde mehrere Konten besitzt).");
                int auswahlSparKonto = sc.nextInt();
                switch (auswahlSparKonto) {
                    case 1:
                        return eroeffneSparKonto(con, kid, isUeberweisung);
                    case 2:
                        String sparKontoDaten = "SELECT * FROM sparkonto WHERE kontoNr = ?";
                        PreparedStatement psSparKontoDaten = con.prepareStatement(sparKontoDaten);
                        psSparKontoDaten.setString(1, kontoNr);
                        ResultSet rsSparKontoDaten = psSparKontoDaten.executeQuery();

                        if (rsSparKontoDaten.next()) { // Hole ich mir die einzelnen Daten vom Sparkonto (also kontoNr, kontoStand, kid) vom ResultSet
                            String abgerufeneKontoNr = rsSparKontoDaten.getString("kontoNr");
                            double abgerufeneKontoStand = rsSparKontoDaten.getDouble("kontoStand");
                            double abgerufeneZinssatz = rsSparKontoDaten.getDouble("zinssatz");

                            return new SparKonto(abgerufeneKontoNr, abgerufeneKontoStand, abgerufeneZinssatz);
                        }
                        psSparKontoDaten.close();
                        rsSparKontoDaten.close();
                        break;
                }
            }
            else{
                System.out.println("Das Sparkonto bzw. Kontonummer des Sparkontos ist schon in der Datenbank vorhanden/uebergeben. Sie müssen ein neues" +
                        "Sparkonto mit einer anderen Kontonummer erstellen");
                return eroeffneSparKonto(con,kid,isUeberweisung);
            }
        }
        else {
            // Daten in die Datenbank einfügen
            String sqlDaten = "INSERT INTO sparkonto (kontoNr,kontoStand, kontoAktiv, summeEinzahlungen, summeAuszahlungen, zinssatz, kid) VALUES (?,?,?,0,0,?,?)";
            PreparedStatement psDaten = con.prepareStatement(sqlDaten);
            psDaten.setString(1, kontoNr);
            psDaten.setDouble(2, kontoStand);
            psDaten.setBoolean(3, kontoAktiv);
            psDaten.setDouble(4, zinssatz);
            psDaten.setInt(5, kid);
            psDaten.executeUpdate();
            psDaten.close();
        }

        psAbfrageSparKonto.close();
        rsAbfrageSparKonto.close();
        return new SparKonto(kontoNr,kontoStand,zinssatz);
    }

    /* Diese Methode dient nur als Test für die BankTest-Klasse
    public SparKonto eroeffneSparKonto(long kontoNr, double kontoStand, double positivZinssatz){
        return new SparKonto(kontoNr,kontoStand,positivZinssatz);
    }
     */

    /**
     * Druckt einen Kontoauszug fuer das angegebene Girokonto des Kunden.
     *
     * @param giroKonto Das Girokonto, fuer das ein Kontoauszug gedruckt werden soll.
     * @param name Der Name des Kunden.
     * @param ort Der Wohnort des Kunden.
     * @param kid Die Kunden-ID.
     */
    public void druckeKontoauszugGiroKonto(GiroKonto giroKonto, String name, String ort, int kid){
        System.out.println("Name: " + name + "\nOrt: " + ort + "\nEmail: " + email + "\nIdentifikationsNr: " + identifikationsNr + "\nKreditwürdigkeit (true/false): " + kreWuerdigkeit +
                "\nKontoNr: " + giroKonto.getKontoNr() + "\nKontostand: " + giroKonto.getKontoStand() + "\nKontoAktiv (true/false): " + giroKonto.isKontoAktiv() +
                "\nEinnahmen: " + giroKonto.getSummeEinzahlungen() + "\nAusgaben: " + giroKonto.getSummeAuszahlungen() + "\nÜberziehungslimit: " + giroKonto.getUeberziehungsLimit() +
                "\nNegativ-Zinssatz: " + giroKonto.getNegativZinssatz() + "\nPositiv-Zinssatz: " + giroKonto.getPositivZinssatz() + "\nSpesen: " + giroKonto.getSpesen() +
                "\nKundeID: " + kid);
    }

    /**
     * Druckt einen Kontoauszug fuer das angegebene Sparkonto des Kunden.
     *
     * @param sparKonto Das Sparkonto, fuer das ein Kontoauszug gedruckt werden soll.
     * @param name Der Name des Kunden.
     * @param ort Der Wohnort des Kunden.
     * @param kid Die Kunden-ID.
     */
    public void druckeKontoauszugSparKonto(SparKonto sparKonto, String name, String ort, int kid){
        System.out.println("Name: " + name + "\nOrt: " + ort + "\nEmail: " + email + "\nIdentifikationsNr: " + identifikationsNr + "\nKreditwürdigkeit (true/false): " + kreWuerdigkeit + "\nKontoNr: " + sparKonto.getKontoNr() +
                "\nKontostand: " + sparKonto.getKontoStand() + "\nKontoAktiv (true/false): " + sparKonto.isKontoAktiv() + "\nEinnahmen: " + sparKonto.getSummeEinzahlungen() +
                "\nAusgaben: " + sparKonto.getSummeAuszahlungen() + "\nZinssatz: " + sparKonto.getZinssatz() + "\nKundeID: " + kid);
    }
}
