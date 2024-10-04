package org.example.demo1;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.demo1.DataBase.DataBase;
import org.example.demo1.Interface.IDB;
import org.example.demo1.Treads.Thread;

import java.io.File;

public class HelloController implements ChangeListener<Number> {
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
    private MediaPlayer mediaPlayer;
    private IDB dataBase = DataBase.getInstance();
    private DataBase dataBaseLoad = DataBase.getInstance();
    private String author, title;
    private boolean isPlaying = false, isStart = false, isTrackPlayed = true;
    private Thread thread;
    private double countUser = 0, volume = 10;

    public void initialize() {
        fileListView.setCellFactory(listView -> new MusicCell(fileListView));
    }

    public void start() {
        if (!fileListView.getItems().isEmpty()) {
            String selectedFile = fileListView.getItems().get(0);
            slider.valueProperty().addListener(this);
            slider.valueProperty().setValue(0);
            countUser = 0;
            isPlaying = true;
            thread = new Thread(slider);
            playSelectedFile(selectedFile);
            isStart  = true;
        } else {
            statusLabel.setText("Список треков пуст.");
        }
        if (isStart){
            startIcon.setImage(new Image(getClass().getResourceAsStream("Img/play_start.png")));
            startButton.setDisable(true);
        }
    }


        public void addMusic() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть музыкальный файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Музыкальные файлы", "*.mp3", "*.wav", "*.aac", "*.flac")
        );

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        Media media = new Media(selectedFile.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        if (selectedFile != null) {
            System.out.println("Выбран файл: " + selectedFile.getAbsolutePath());
            statusLabel.setText("Выбран файл: " + selectedFile.getName());

            String fileName = selectedFile.getName();
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));

            String[] separationAuthorName = fileName.split(" - ");

            if (separationAuthorName.length == 2) {
                author = separationAuthorName[0].trim();
                title = separationAuthorName[1].trim();
                if (fileListView.getItems().contains(author + " - " + title)) {
                    System.out.println("Такой трек уже есть в списке");
                    statusLabel.setText("Такой трек уже есть в списке");
                    return;
                }
                System.out.println("Автор: " + author);
                System.out.println("Название: " + title);

            } else {
                System.out.println("Ошибка: Имя файла не соответствует ожидаемому формату 'Author - Title'");
                statusLabel.setText("Ошибка: Неверный формат имени файла");
            }
        } else {
            System.out.println("Файл не выбран");
            statusLabel.setText("Файл не выбран");
        }


        mediaPlayer.setOnReady(() -> {
            Duration duration = mediaPlayer.getMedia().getDuration();
            double seconds = duration.toSeconds();

            long minutes = (long) seconds / 60;
            long secs = (long) seconds % 60;
            String formattedDuration = String.format("%d:%02d", minutes, secs);

            System.out.println("Длительность трека: " + formattedDuration + " минут");

            dataBase.save_music(author, title, formattedDuration, selectedFile);
            fileListView.getItems().add(author + " - " + title);

        });

    }

    private void playSelectedFile(String selectedFileName) {
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

                mediaPlayer.setOnReady(() -> {
                    mediaPlayer.play();
                    statusLabel.setText("Воспроизведение: " + selectedFileName);
                    playIcon.setImage(new Image(getClass().getResourceAsStream("Img/pause_button.png")));
                    slider.setMin(0);
                    slider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());

                    stopCurrentThread();
                    thread = new org.example.demo1.Treads.Thread(slider);
                    thread.start();
                    isPlaying = true;
                });

                mediaPlayer.setOnEndOfMedia(() -> {
                    mediaPlayer.seek(Duration.ZERO);
                    statusLabel.setText("Воспроизведение завершено: " + selectedFileName);
                    playIcon.setImage(new Image(getClass().getResourceAsStream("Img/play-button.png")));
                    slider.setValue(0);
                    stopCurrentThread();
                    isPlaying = false;



                });

                mediaPlayer.setOnError(() -> {
                    statusLabel.setText("Ошибка воспроизведения: " + mediaPlayer.getError().getMessage());
                });
            }
        }
    }




    private void stopCurrentTrack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        stopCurrentThread();
    }

    private void stopCurrentThread() {
        if (thread != null) {
            thread.setStop(false);
            thread.interrupt();
            thread = null;
        }
    }


    public void playButtonFunc() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playIcon.setImage(new Image(getClass().getResourceAsStream("Img/play-button.png")));
            isPlaying = false;
            thread.setStop(false);
            thread.interrupt();
            statusLabel.setText("Пауза: " + title);
        } else {
            playIcon.setImage(new Image(getClass().getResourceAsStream("Img/pause_button.png")));
            isPlaying = true;
            mediaPlayer.play();
            thread = new Thread(slider, (int) countUser);
            thread.start();
            statusLabel.setText("Воспроизведение: " + title);
        }
    }

    public void sliderClick() {
        System.out.println("user click");
    }


    @Override
    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number currentSec) {
        int countSlider = (int) slider.getValue();
        countUser++;
        if (countUser + 2 < countSlider || countUser - 2 > countSlider) {
            countUser = countSlider;
            mediaPlayer.seek(Duration.seconds(currentSec.doubleValue()));
            thread.setCount(currentSec.intValue());
            statusLabel.setText("Воспроизведение: " + title);


        }

    }

    @FXML
    private void playPreviousTrack() {
        System.out.println("playPreviousTrack");
        try {
            int currentIndex = fileListView.getSelectionModel().getSelectedIndex();
            if (currentIndex < fileListView.getItems().size() + 1) {
                fileListView.getSelectionModel().select(currentIndex - 1);
                dataBaseLoad.setMusicId(currentIndex - 1);
                playSelectedFile(fileListView.getItems().get(currentIndex - 1));
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
        if (currentIndex < fileListView.getItems().size() - 1) {
            fileListView.getSelectionModel().select(currentIndex + 1);
            dataBaseLoad.setMusicId(currentIndex + 1);
            playSelectedFile(fileListView.getItems().get(currentIndex + 1));
        } else {
            statusLabel.setText("Достигнут конец списка треков.");
        }
    }

    public void scrollbarVolume() {
        volume = scrollbarVolume.getValue() / 100.0;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
        System.out.println("volume: " + volume);
    }


}



