package controller;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Value;

import java.io.IOException;

/**
 * Created by felipe on 14/04/16.
 */
public class FormBox extends VBox{

    @FXML
    private Label formLabel;

    @FXML
    private TextField formTextField;

    private Value value;

    public FormBox(Value value) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/formBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.value = value;

        try {
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @FXML
    public void initialize() {
        this.setSpacing(5);
        //System.out.println("\n\n" + value + "\n");
        formLabel.setText(value.getName());

        formTextField.setPromptText("Insert here " + value.getName());
        if(value.isDefaultValue())
            formTextField.setText(value.getValue().toString());


    }
}
