module bankverwaltungssystem.bankverwaltungssystem_javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens bankverwaltungssystem.bankverwaltungssystem_javafx to javafx.fxml;
    exports bankverwaltungssystem.bankverwaltungssystem_javafx;
}