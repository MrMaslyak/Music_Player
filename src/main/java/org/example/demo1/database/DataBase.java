package org.example.demo1.database;

import org.example.demo1.database.repository.IDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataBase implements IDB {

    private static DataBase dataBase;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "LaLa27418182";
    private  int musicId;

    public DataBase() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the PostgreSQL server successfully.");


            connection.close();

        } catch (Exception e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }

    public static DataBase getInstance() {
        if (dataBase == null) {
            dataBase = new DataBase();
        }
        return dataBase;
    }



    @Override
    public void save_music(String author, String name, String duration, File musicFile) {
        String checkQuery = "SELECT COUNT(*) FROM musics WHERE author = ? AND name = ?";
        String insertQuery = "INSERT INTO musics (author, name, duration, file) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
             FileInputStream fis = new FileInputStream(musicFile)) {


            checkStatement.setString(1, author);
            checkStatement.setString(2, name);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0) {
                System.out.println("Трек уже существует в базе данных: " + name);
                return;
            }


            insertStatement.setString(1, author);
            insertStatement.setString(2, name);
            insertStatement.setString(3, duration);
            insertStatement.setBinaryStream(4, fis, (int) musicFile.length());

            insertStatement.executeUpdate();
            System.out.println("Музыка добавлена: " + name);

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении музыки.");
            e.printStackTrace();
        }
    }



    @Override
    public void loadData() {
        String query = "SELECT * from musics";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.executeUpdate();


        } catch (Exception e) {
            System.out.println("Ошибка при добавлении музыки.");
            e.printStackTrace();
        }

    }



    public int getMusicIdByName(String name) {
        String query = "SELECT id FROM musics WHERE name = ?";
         musicId = -1;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                musicId = rs.getInt("id");
                System.out.println("Найден ID музыки: " + musicId);

            } else {
                System.out.println("Музыка не найдена: " + name);
            }

        } catch (Exception e) {
            System.out.println("Ошибка при получении ID музыки.");
            e.printStackTrace();
        }

        return musicId;
    }


    public File getMusicFromDatabase(int musicId) {
        String query = "SELECT file FROM musics WHERE id = ?";
        File tempFile = null;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, musicId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                InputStream inputStream = rs.getBinaryStream("file");


                tempFile = File.createTempFile("music", ".mp3");
                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Музыка извлечена из базы данных.");

            }

        } catch (Exception e) {
            System.out.println("Ошибка при извлечении музыки.");
            e.printStackTrace();
        }

        return tempFile;
    }


    @Override
    public void delete(int musicId) {
        String query = "DELETE FROM musics WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, musicId);
            statement.executeUpdate();
            System.out.println("Музыка удалена из базы данных.");

        } catch (Exception e) {
            System.out.println("Ошибка при удалении музыки.");
            e.printStackTrace();
        }

    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }
}