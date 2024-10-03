package org.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.demo1.DataBase.DataBase;
import org.example.demo1.Interface.IDB;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 750);
        stage.setTitle("Music Player");
        stage.setScene(scene);
        stage.show();
        IDB dataBase = DataBase.getInstance();


    }

    public static void main(String[] args) {
        launch();
    }
}