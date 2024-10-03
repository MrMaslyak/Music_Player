module org.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.media;
    requires java.sql;

    opens org.example.demo1 to javafx.fxml;
    exports org.example.demo1;
    exports org.example.demo1.DataBase;
    opens org.example.demo1.DataBase to javafx.fxml;
    exports org.example.demo1.Interface;
    opens org.example.demo1.Interface to javafx.fxml;
    exports org.example.demo1.Treads;
    opens org.example.demo1.Treads to javafx.fxml;
}
