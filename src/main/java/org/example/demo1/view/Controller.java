package org.example.demo1.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollBar;
import org.example.demo1.database.DataBase;
import org.example.demo1.database.repository.IDB;
import org.example.demo1.model.MusicCell;
import org.example.demo1.services.System;
import org.example.demo1.treads.Thread;

public class Controller {


    @FXML
    private Button startButton;
    @FXML
    private Label statusLabel;
    @FXML
    private ListView<String> fileListView;
    @FXML
    private Slider slider;
    @FXML
    private ImageView playIcon, startIcon;
    @FXML
    private ScrollBar scrollbarVolume;

    private IDB dataBase = DataBase.getInstance();
    private DataBase dataBaseLoad = DataBase.getInstance();
    private String author, title;
    private boolean isStart = false, isStop = true;
    private Thread thread;
    private double countUser = 0, volume = 0;
    private System system;
    private Panel panel;

    public void initialize() {

        fileListView.setCellFactory(listView -> new MusicCell(fileListView));
        system = new System();
        panel = new Panel(fileListView, slider, playIcon,
                     scrollbarVolume, dataBaseLoad, isStart,
                     countUser, volume);
        system.setSlider(slider);
        system.setThread(thread);
        system.setStatusLabel(statusLabel);

    }

    public void start() {
        if (!fileListView.getItems().isEmpty()) {
            String selectedFile = fileListView.getItems().get(0);
            slider.valueProperty().addListener(system);
            slider.valueProperty().setValue(0);
            countUser = 0;
            thread = new Thread(slider);
            system.playSelectedFile(selectedFile, playIcon, statusLabel, slider);
            isStart = true;
        } else {
            statusLabel.setText("Список треков пуст.");
        }

        if (isStart) {
            startIcon.setImage(new Image(getClass().getResourceAsStream("/org/example/demo1/Img/play_start.png")));

            startButton.setDisable(true);
            playButtonFunc();
        }
    }

    public void addMusic() {
        system.addMusic(statusLabel, fileListView, author, title);
    }

    public void playButtonFunc() {
        panel.playButtonFunc(thread, system, statusLabel);
    }

  public void playNextTrack (){
        panel.playNextTrack(thread, system, statusLabel);
    }

    public void playPreviousTrack (){
        panel.playPreviousTrack(thread, system, statusLabel);
    }

    public void scrollbarVolume(){
        panel.scrollbarVolume(system);
    }
}



