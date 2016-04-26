package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;

/**
 * Created by Marco on 14/04/2016.
 */
public class ChannelView extends HBox {

    @FXML
    HBox hboxChannelView;
    @FXML
    ImageView editChannel;
    @FXML
    ImageView removeChannel;

    @FXML

    TextField textChannel;


    public ChannelView(String channel){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ChannelView.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        editChannel.setImage(new Image("/Edit.png"));

  //      removeChannel.setImage(new Image("/images/Delete.png"));
        textChannel=new TextField();
        textChannel.setText(channel);
        textChannel.setEditable(false);

    }

    public String getName() {
        return textChannel.getText();
    }

    public ImageView getEditChannel() {
        return editChannel;
    }

    public ImageView getRemoveChannel() {
        return removeChannel;
    }

    public TextField getTextChannel() {
        return textChannel;
    }

}
