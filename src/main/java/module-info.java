module bankverwaltungssystem_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens bankverwaltungssystem_javafx.controllers to javafx.fxml;

    exports bankverwaltungssystem_javafx.application;
}
