package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.Link;

import java.io.IOException;

/**
 * Created by Marco on 14/04/2016.
 */
public class ChannelView extends SplitPane {

    @FXML
    SplitPane paneChannel;
    @FXML
    ImageView editChannel;
    @FXML
    ImageView removeChannel;
    @FXML
    Label labelChannel;


    public ChannelView(String channel){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ChannelView.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        editChannel.setImage(new Image("/Edit.png"));

        removeChannel.setImage(new Image("/Delete.png"));
        labelChannel=new Label(channel);
      //  labelChannel.setText(channel);
        editChannel.setFitHeight(15);
        editChannel.setFitWidth(15);
        removeChannel.setFitWidth(15);
        removeChannel.setFitHeight(15);


    }

    public String getName() {
        return labelChannel.getText();
    }

    public ImageView getEditChannel() {
        return editChannel;
    }

    public ImageView getRemoveChannel() {
        return removeChannel;
    }
}
