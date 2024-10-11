package org.example.demo1.services;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.demo1.database.DataBase;
import org.example.demo1.database.repository.IDB;
import org.example.demo1.treads.Thread;

import java.io.File;

public class System implements ChangeListener<Number> {

    private Label statusLabel;
    private ListView<String> fileListView;
    private String author, title;
    private IDB dataBase = DataBase.getInstance();
    private MediaPlayer mediaPlayer;
    private Thread thread;
    private double countUser = 0, volume = 10;
    private Slider slider;
    private ImageView playIcon;

    public void setSlider(Slider slider) {
        this.slider = slider;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public String getTitle() {
        return title;
    }

    public void addMusic(Label statusLabel, ListView<String> fileListView, String author, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть музыкальный файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Музыкальные файлы", "*.mp3", "*.wav", "*.aac", "*.flac")
        );

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            java.lang.System.out.println("Выбран файл: " + selectedFile.getAbsolutePath());
            statusLabel.setText("Выбран файл: " + selectedFile.getName());

            Media media = new Media(selectedFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            String fileName = selectedFile.getName();
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));

            String[] separationAuthorName = fileName.split(" - ");

            if (separationAuthorName.length == 2) {
                author = separationAuthorName[0].trim();
                title = separationAuthorName[1].trim();
                if (fileListView.getItems().contains(author + " - " + title)) {
                    java.lang.System.out.println("Такой трек уже есть в списке");
                    statusLabel.setText("Такой трек уже есть в списке");
                    return;
                }
                java.lang.System.out.println("Автор: " + author);
                java.lang.System.out.println("Название: " + title);
            } else {
                java.lang.System.out.println("Ошибка: Имя файла не соответствует ожидаемому формату 'Author - Title'");
                statusLabel.setText("Ошибка: Неверный формат имени файла");
            }

            String finalAuthor = author;
            String finalTitle = title;

            mediaPlayer.setOnReady(() -> {
                Duration duration = mediaPlayer.getMedia().getDuration();
                double seconds = duration.toSeconds();

                long minutes = (long) seconds / 60;
                long secs = (long) seconds % 60;
                String formattedDuration = String.format("%d:%02d", minutes, secs);

                java.lang.System.out.println("Длительность трека: " + formattedDuration + " минут");

                dataBase.save_music(finalAuthor, finalTitle, formattedDuration, selectedFile);
                fileListView.getItems().add(finalAuthor + " - " + finalTitle);
            });
        } else {
            java.lang.System.out.println("Файл не выбран");
            statusLabel.setText("Файл не выбран");
        }
    }

    @Override
    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number currentSec) {

        if (mediaPlayer != null) {
            int countSlider = (int) slider.getValue();
            countUser++;
            if (countUser + 2 < countSlider || countUser - 2 > countSlider) {
                countUser = countSlider;
                mediaPlayer.seek(Duration.seconds(currentSec.doubleValue()));


                if (thread == null) {
                    thread = new Thread(slider);
                    thread.start();
                }
                thread.setCount(currentSec.intValue());

                statusLabel.setText("Воспроизведение: " + title);
            }
        } else {
            java.lang.System.out.println("mediaPlayer не инициализирован");
        }
    }

    public void playSelectedFile(String selectedFileName, ImageView playIcon, Label statusLabel, Slider slider) {
        if (selectedFileName != null) {
            String[] separationAuthorName = selectedFileName.split(" - ");
            author = separationAuthorName[0].trim();
            title = separationAuthorName[1].trim();

            stopCurrentTrack();

            int musicId = dataBase.getMusicIdByName(title);
            File musicFile = dataBase.getMusicFromDatabase(musicId);

            if (musicFile != null) {
                Media media = new Media(musicFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                setMediaPlayer(mediaPlayer);

                mediaPlayer.setOnReady(() -> {
                    mediaPlayer.play();
                    statusLabel.setText("Воспроизведение: " + selectedFileName);
                    try {
                        playIcon.setImage(new Image(getClass().getResourceAsStream("/org/example/demo1/Img/pause_button.png")));
                    }catch (NullPointerException e){
                        java.lang.System.out.println(" Input stream must not be null");
                    }

                    slider.setMin(0);
                    slider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());

                    if (thread == null) {
                        thread = new Thread(slider);
                        thread.start();
                    }
                });

                mediaPlayer.setOnEndOfMedia(() -> {
                    mediaPlayer.seek(Duration.ZERO);
                    statusLabel.setText("Воспроизведение завершено: " + selectedFileName);
                    playIcon.setImage(new Image(getClass().getResourceAsStream("/org/example/demo1/Img/play_button.png")));
                    slider.setValue(0);
                    stopCurrentThread();
                });

                mediaPlayer.setOnError(() -> {
                    statusLabel.setText("Ошибка воспроизведения: " + mediaPlayer.getError().getMessage());
                });
            }
        }
    }

    public void stopCurrentTrack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        stopCurrentThread();
    }

    private void stopCurrentThread() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
}
