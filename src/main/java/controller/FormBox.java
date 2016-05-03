package controller;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Value;

import java.io.IOException;

/**
 * Created by felipe on 14/04/16.
 */
public class FormBox<T> extends VBox {

    @FXML
    private Label formLabel;

    @FXML
    private TextField formTextField;

    @FXML
    private ImageView statusIcon;

//    @FXML
//    private ImageView acceptIcon;

    @FXML
    private ImageView clearIcon;


    private ObjectProperty<T> valueProperty;
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
        formTextField.setText("");
        //acceptIcon.setImage(new Image("images/accept.png"));
        clearIcon.setImage(new Image("images/cancel.png"));
        Tooltip.install(clearIcon, new Tooltip("Clear this value"));


        if (value.isMandatory()) {
            if ((formTextField.getText().equals(""))) {
                statusIcon.setImage(new Image("images/warning.png"));
                Tooltip.install(statusIcon, new Tooltip("Mandatory parameter please fill this!"));
            } else {

            }
        }

        //Bindings.bindBidirectional(value.valueProperty(), formTextField.textProperty());
        // value.valueProperty().bind(formTextField.textProperty());
        //formTextField.textProperty().bind(value.valueProperty());

        this.setSpacing(10);
        //System.out.println("\n\n" + value + "\n");
        formLabel.setText(value.getName());

        formTextField.setPromptText("Insert here " + value.getName());
        if (value.isDefaultValue())
            formTextField.setText(value.getValue().toString());


    }


}
