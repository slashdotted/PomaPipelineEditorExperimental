package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import utils.ProgramUtils;

import java.io.IOException;

/**
 * Created by felipe on 16/05/16.
 */
public class About extends VBox {

    @FXML
    private Button closeButton;

    public About() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/about.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @FXML
    public void initialize() {
        closeButton.setGraphic(ProgramUtils.getCloseImage2(20));
        closeButton.setBackground(Background.EMPTY);
        closeButton.setOnAction(event -> {
            this.getScene().getWindow().hide();
        });
        //this.setStyle("-fx-background-color: gre");
    }
}
