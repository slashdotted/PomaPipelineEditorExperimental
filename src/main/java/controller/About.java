package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * About Class
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
        closeButton.setOnAction(event -> {
            this.getScene().getWindow().hide();
        });
    }
}
