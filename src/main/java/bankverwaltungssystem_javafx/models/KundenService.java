package bankverwaltungssystem_javafx.models;

import bankverwaltungssystem_javafx.application.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KundenService {
    private ObservableList<Kunde> kundenListe;

    public KundenService() {
        this.kundenListe = FXCollections.observableArrayList();
    }

    public Kunde erstelleKunde(String name, String ort, String email, String id, boolean kreditwuerdig) throws SQLException {
        Connection con = DBManager.getConnection();

        String checkSQL = "SELECT * FROM kunde WHERE identifikationsNr = ?";
        PreparedStatement checkStmt = con.prepareStatement(checkSQL);
        checkStmt.setString(1, id);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            String abgerufenerName = rs.getString("name");
            String abgerufenerOrt = rs.getString("ort");
            String abgerufeneEmail = rs.getString("email");
            String abgerufeneId = rs.getString("identifikationsNr");
            boolean abgerufeneKreditw = rs.getBoolean("kreWuerdigkeit");

            rs.close();
            checkStmt.close();

            return new Kunde(abgerufenerName, abgerufenerOrt, abgerufeneEmail, abgerufeneId, abgerufeneKreditw);
        } else {
            String insertSQL = "INSERT INTO kunde (name, ort, email, identifikationsNr, kreWuerdigkeit) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = con.prepareStatement(insertSQL);
            insertStmt.setString(1, name);
            insertStmt.setString(2, ort);
            insertStmt.setString(3, email);
            insertStmt.setString(4, id);
            insertStmt.setBoolean(5, kreditwuerdig);
            insertStmt.executeUpdate();
            insertStmt.close();

            rs.close();
            checkStmt.close();
            return new Kunde(name, ort, email, id, kreditwuerdig);
        }
    }

    public List<Kunde> sucheKunde(String name) throws SQLException {
        List<Kunde> kunden = new ArrayList<>();
        Connection con = DBManager.getConnection();

        String sql = "SELECT * FROM kunde WHERE name LIKE ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, "%" + name + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String kundenName = rs.getString("name");
            String ort = rs.getString("ort");
            String email = rs.getString("email");
            String id = rs.getString("identifikationsNr");
            boolean kreditwuerdig = rs.getBoolean("kreWuerdigkeit");

            kunden.add(new Kunde(kundenName, ort, email, id, kreditwuerdig));
        }

        rs.close();
        stmt.close();
        return kunden;
    }
    
    public void initialisiereListView(ListView<Kunde> listView) {
        listView.setItems(kundenListe);
        
        // Setze den CellFactory für die ListView
        listView.setCellFactory(new Callback<ListView<Kunde>, ListCell<Kunde>>() {
            @Override
            public ListCell<Kunde> call(ListView<Kunde> param) {
                return new ListCell<Kunde>() {
                    @Override
                    protected void updateItem(Kunde kunde, boolean empty) {
                        super.updateItem(kunde, empty);
                        if (empty || kunde == null) {
                            setText(null);
                        } else {
                            setText(String.format("%s (ID: %s, Ort: %s)", 
                                kunde.getName(), 
                                kunde.getIdentifikationsNr(), 
                                kunde.getOrt()));
                        }
                    }
                };
            }
        });
    }

    public void setKundenListe(List<Kunde> kunden) {
        kundenListe.clear();
        kundenListe.addAll(kunden);
    }
}

