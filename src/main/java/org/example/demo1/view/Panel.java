package org.example.demo1.view;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import org.example.demo1.database.DataBase;
import org.example.demo1.services.System;
import org.example.demo1.treads.Thread;

import java.io.InputStream;


public class Panel {

    private ListView<String> fileListView;
    private Slider slider;
    private ImageView playIcon;
    private ScrollBar scrollbarVolume;
    private DataBase dataBaseLoad = DataBase.getInstance();
    private boolean isStop = true;
    private double countUser = 0, volume = 0;

    public Panel(ListView<String> fileListView, Slider slider,
                 ImageView playIcon, ScrollBar scrollbarVolume, DataBase dataBaseLoad,
                 boolean isStop, double countUser, double volume) {
        this.fileListView = fileListView;
        this.slider = slider;
        this.playIcon = playIcon;
        this.scrollbarVolume = scrollbarVolume;
        this.dataBaseLoad = dataBaseLoad;
        this.isStop = isStop;
        this.countUser = countUser;
        this.volume = volume;
    }

    public void playButtonFunc(Thread thread, System system, Label statusLabel) {
        MediaPlayer mediaPlayer = system.getMediaPlayer();
        String title = system.getTitle();
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
            playIcon.setImage(new Image(getClass().getResourceAsStream("/org/example/demo1/Img/play-button.png")));

            statusLabel.setText("Пауза: " + title);
        } else {
            isStop = true;
            mediaPlayer.play();
            playIcon.setImage(new Image(getClass().getResourceAsStream("/org/example/demo1/Img/pause_button.png")));
            statusLabel.setText("Воспроизведение: " + title);
        }
    }


    public void playNextTrack(Thread thread, System system, Label statusLabel) {
        try {
            int currentIndex = fileListView.getSelectionModel().getSelectedIndex();

            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
            system.stopCurrentTrack();

            if (currentIndex < fileListView.getItems().size() - 1) {
                fileListView.getSelectionModel().select(currentIndex + 1);
                dataBaseLoad.setMusicId(currentIndex + 1);
                system.playSelectedFile(fileListView.getItems().get(currentIndex + 1), playIcon, statusLabel, slider);

                thread = new Thread(slider);
                thread.start();
                MediaPlayer mediaPlayer = system.getMediaPlayer();
                mediaPlayer.setVolume(volume);
                slider.setValue(0);
                slider.setMax(mediaPlayer.getTotalDuration().toSeconds());

            } else {
                statusLabel.setText("Достигнут конец списка треков.");
            }
        } catch (RuntimeException ex) {
            statusLabel.setText("Ошибка переключения на следующий трек.");
        }
    }



    public void playPreviousTrack(Thread thread, System system, Label statusLabel) {
        try {
            int currentIndex = fileListView.getSelectionModel().getSelectedIndex();

            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
            system.stopCurrentTrack();

            if (currentIndex > 0) {
                fileListView.getSelectionModel().select(currentIndex - 1);
                dataBaseLoad.setMusicId(currentIndex - 1);
                system.playSelectedFile(fileListView.getItems().get(currentIndex - 1), playIcon, statusLabel, slider);

                thread = new Thread(slider);
                thread.start();
                MediaPlayer mediaPlayer = system.getMediaPlayer();
                mediaPlayer.setVolume(volume);
                slider.setValue(0);
                slider.setMax(mediaPlayer.getTotalDuration().toSeconds());

            } else {
                statusLabel.setText("Достигнут верх списка треков.");
            }
        } catch (RuntimeException ex) {
            statusLabel.setText("Ошибка переключения на предыдущий трек.");
        }
    }



    public void scrollbarVolume(System system) {
        MediaPlayer mediaPlayer = system.getMediaPlayer();
        volume = scrollbarVolume.getValue() / 100.0;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
        java.lang.System.out.println("volume: " + volume);

    }

}
