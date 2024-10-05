package org.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.demo1.DataBase.DataBase;
import org.example.demo1.Interface.IDB;
import java.io.File;

public class SystemMusic {

    private Label statusLabel;
    private ListView<String> fileListView;
    private String author, title;
    private IDB dataBase = DataBase.getInstance();

    public void addMusic(Label statusLabel,ListView<String> fileListView, String author, String title) {
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


        String finalAuthor = author;
        String finalTitle = title;


        mediaPlayer.setOnReady(() -> {
            Duration duration = mediaPlayer.getMedia().getDuration();
            double seconds = duration.toSeconds();

            long minutes = (long) seconds / 60;
            long secs = (long) seconds % 60;
            String formattedDuration = String.format("%d:%02d", minutes, secs);

            System.out.println("Длительность трека: " + formattedDuration + " минут");

            dataBase.save_music(finalAuthor, finalTitle, formattedDuration, selectedFile);
            fileListView.getItems().add(finalAuthor + " - " + finalTitle);

        });

    }

}
