module com.memory
{
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.media;

    // Drivers DB
    requires java.sql;

    // SQLite
    requires org.xerial.sqlitejdbc;

    // FontAwesomeFX
    requires de.jensd.fx.glyphs.fontawesome;

    // Ouvrir les packages aux loaders FXML
    opens com.memory to javafx.fxml;
    opens com.memory to javafx.media;

    // Exporter ton package principal
    exports com.memory;
}
