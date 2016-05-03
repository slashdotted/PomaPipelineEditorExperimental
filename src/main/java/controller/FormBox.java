package controller;

import com.sun.org.apache.xpath.internal.SourceTree;
import commands.Command;
import commands.EditValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
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
    private BooleanProperty valid = new SimpleBooleanProperty(false);
    private String oldString;

    public FormBox(Value value) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/formBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.value = value;
        this.oldString = "";

        try {
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void initialize() {
        formTextField.setText("");
        clearIcon.setImage(new Image("images/cancel.png"));
        Tooltip.install(clearIcon, new Tooltip("Clear this value"));


        this.setSpacing(10);
        //System.out.println("\n\n" + value + "\n");
        formLabel.setText(value.getName());

        formTextField.setPromptText("Insert here " + value.getName());
        if(value.isMandatory())
            valid.setValue(value.isValid());


        if (valid.getValue())
            formTextField.setText(value.getValue().toString());

        setHandlers();

        setInitialImage();
    }

    private void setInitialImage() {
        if (!valid.getValue() && value.isMandatory()) {
            //warning
            statusIcon.setImage(new Image("images/warning.png"));
        } else {
            //ok
            statusIcon.setImage(new Image("images/ok.png"));

        }
    }

    private void setHandlers() {
        formTextField.setOnMouseClicked(event -> {
            oldString = formTextField.getText();
        });

        formTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                if (!oldString.equals(formTextField.getText())) {
                    System.out.println("value changed");
                    Command editValue = new EditValue(value, formTextField.getText());
                    valid.setValue(editValue.execute());

                    if (valid.getValue() || value.getDefaultValue()!=null) {
                        //TODO add to memento

                        formTextField.setText(value.getValue().toString());
                    }
                }
            }
        });

                valid.addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                // warning
                statusIcon.setImage(new Image("images/warning.png"));

            } else if (newValue && !oldValue) {
                // ok
                statusIcon.setImage(new Image("images/ok.png"));

            }
        });
    }


    public boolean isValid() {
        return value.isValid();
    }


}
