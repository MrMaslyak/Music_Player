package org.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.ScrollBar;
import org.example.demo1.DataBase.DataBase;
import org.example.demo1.Interface.IDB;
import org.example.demo1.Treads.Thread;

public class HelloController {
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
    private SystemMusic systemMusic = new SystemMusic();

    public void initialize() {
        fileListView.setCellFactory(listView -> new MusicCell(fileListView));
        systemMusic.setSlider(slider);
        systemMusic.setThread(thread);
        systemMusic.setStatusLabel(statusLabel);

    }

    public void start() {
        if (!fileListView.getItems().isEmpty()) {
            String selectedFile = fileListView.getItems().get(0);
            slider.valueProperty().addListener(systemMusic);
            slider.valueProperty().setValue(0);
            countUser = 0;
            thread = new Thread(slider);
            systemMusic.playSelectedFile(selectedFile, playIcon, statusLabel, slider);
            isStart = true;
        } else {
            statusLabel.setText("Список треков пуст.");
        }

        if (isStart) {
            startIcon.setImage(new Image(getClass().getResourceAsStream("Img/play_start.png")));
            startButton.setDisable(true);
            playButtonFunc();
        }
    }

    public void addMusic() {
        systemMusic.addMusic(statusLabel, fileListView, author, title);
    }

    public void playButtonFunc() {
        MediaPlayer mediaPlayer = systemMusic.getMediaPlayer();
        String title = systemMusic.getTitle();
        if (mediaPlayer == null) {
            statusLabel.setText("Ошибка: Плеер не инициализирован.");
            return;
        }
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            isStop = false;
            thread.interrupt();
            thread = new Thread(slider, (int) countUser, isStop);
            thread.start();

            mediaPlayer.pause();
            playIcon.setImage(new Image(getClass().getResourceAsStream("Img/play-button.png")));
            statusLabel.setText("Пауза: " + title);
        } else {
            isStop = true;
            mediaPlayer.play();
            playIcon.setImage(new Image(getClass().getResourceAsStream("Img/pause_button.png")));
            statusLabel.setText("Воспроизведение: " + title);
        }
    }



    public void sliderClick() {
        System.out.println("user click");
    }




    @FXML
    private void playPreviousTrack() {
        System.out.println("playPreviousTrack");
        try {
            int currentIndex = fileListView.getSelectionModel().getSelectedIndex();
            thread.interrupt();
            thread = new Thread(slider, (int) countUser, isStop);
            thread.start();
            if (currentIndex < fileListView.getItems().size() + 1) {
                fileListView.getSelectionModel().select(currentIndex - 1);
                dataBaseLoad.setMusicId(currentIndex - 1);
                systemMusic.playSelectedFile(fileListView.getItems().get(currentIndex - 1), playIcon, statusLabel, slider);
                MediaPlayer mediaPlayer = systemMusic.getMediaPlayer();
                mediaPlayer.setVolume(volume);
            } else {
                statusLabel.setText("Достигнут конец списка треков.");
            }
        }catch (RuntimeException ex){
            statusLabel.setText("Достигнут вверх списка треков.");
        }

    }

    @FXML
    private void playNextTrack() {
        System.out.println("playNextTrack");
        int currentIndex = fileListView.getSelectionModel().getSelectedIndex();
        thread.interrupt();
        thread = new Thread(slider, (int) countUser, isStop);
        thread.start();
        if (currentIndex < fileListView.getItems().size() - 1) {
            fileListView.getSelectionModel().select(currentIndex + 1);
            dataBaseLoad.setMusicId(currentIndex + 1);
            systemMusic.playSelectedFile(fileListView.getItems().get(currentIndex + 1), playIcon, statusLabel, slider);
            MediaPlayer mediaPlayer = systemMusic.getMediaPlayer();
            mediaPlayer.setVolume(volume);
        } else {
            statusLabel.setText("Достигнут конец списка треков.");
        }
    }

    public void scrollbarVolume() {
        MediaPlayer mediaPlayer = systemMusic.getMediaPlayer();
        volume = scrollbarVolume.getValue() / 100.0;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
        System.out.println("volume: " + volume);

    }


}



