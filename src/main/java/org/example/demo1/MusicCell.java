package org.example.demo1;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import org.example.demo1.DataBase.DataBase;
import org.example.demo1.Interface.IDB;


public class MusicCell extends ListCell<String> {

    private HBox content;
    private Text musicName;
    private ImageView deleteIcon;
    private IDB dataBase = DataBase.getInstance();
    private final Region spacer = new Region();

    public MusicCell(ListView<String> listView) {
        super();
        musicName = new Text();
        deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("Img/delete_icon.png")));



        deleteIcon.setFitHeight(17);
        deleteIcon.setFitWidth(17);
        deleteIcon.setOnMouseClicked(event -> {
            String selectedItem = getItem();
            listView.getItems().remove(selectedItem);
            int musicId = dataBase.getMusicIdByName(selectedItem.split(" - ")[1]);
            dataBase.delete(musicId);
        });


        content = new HBox(musicName, spacer, deleteIcon);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        setGraphic(null);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            musicName.setText(item);
            setGraphic(content);
        }
    }
}
