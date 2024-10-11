module org.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.media;
    requires java.sql;

    opens org.example.demo1 to javafx.fxml;

    exports org.example.demo1.database;
    opens org.example.demo1.database to javafx.fxml;
    exports org.example.demo1.database.repository;
    opens org.example.demo1.database.repository to javafx.fxml;
    exports org.example.demo1;
    exports org.example.demo1.treads;
    opens org.example.demo1.treads to javafx.fxml;
    exports org.example.demo1.services;
    opens org.example.demo1.services to javafx.fxml;
    exports org.example.demo1.view;
    opens org.example.demo1.view to javafx.fxml;
    exports org.example.demo1.model;
    opens org.example.demo1.model to javafx.fxml;
}
