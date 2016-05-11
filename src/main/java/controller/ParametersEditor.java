package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by felipe on 11/05/16.
 */
public class ParametersEditor extends VBox{


    public ParametersEditor() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/parametersEditor.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void initialize(){

    }
}
