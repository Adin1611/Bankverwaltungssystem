module bankverwaltungssystem_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens bankverwaltungssystem_javafx to javafx.fxml;
    exports bankverwaltungssystem_javafx.application;
    opens bankverwaltungssystem_javafx.application to javafx.fxml;
}