package controller;

import commands.Command;
import commands.EditValue;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Value;
import utils.CareTaker;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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

    @FXML
    private Label formDescription;

    @FXML
    private ImageView clearIcon;

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
        Platform.runLater(() -> Tooltip.install(clearIcon, new Tooltip("Clear this value")));

        this.setSpacing(10);
        formLabel.setText(value.getName());
        formDescription.setText(value.getDescription());
        formDescription.setTooltip(new Tooltip(value.getDescription()));

        formTextField.setPromptText("Insert here a " + value.getType() + " value");
        formTextField.setTooltip(new Tooltip("Default value:" + value.getDefaultValue()));
        if (value.isMandatory()) {
            valid.setValue(value.isValid());
        } else {
            valid.setValue(true);
        }

        if (valid.getValue()) {
            formTextField.setText(value.getValue().toString());
        }

        clearIcon.setVisible(false);
        setHandlers();

        setInitialImage();
    }

    /*
    * Update the image near to the relative text field
    * */
    private void setInitialImage() {
        if (!valid.getValue() && value.isMandatory()) {
            //warning
            statusIcon.setImage(new Image("images/warning.png"));
        } else {
            //ok
            statusIcon.setImage(null);

        }
    }

    private void setHandlers() {
        formTextField.setOnMouseClicked(event -> {
            oldString = formTextField.getText();

        });

        /*
        * When focus is changed validate if there is a new value
         */
        formTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                if (!oldString.equals(formTextField.getText())) {

                    Command editValue = new EditValue(value, formTextField.getText());
                    valid.setValue(editValue.execute());

                    if (valid.getValue()) {
                        CareTaker.addMemento(editValue);
                    } else {
                        valid.set(value.isValid());
                    }
                    formTextField.setText(value.getValue().toString());
                    for (DraggableModule dm : MainWindow.instance().getSelectedModules()) {
                        dm.getModule().validate();
                    }
                }
            }
        });

        formTextField.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                Command editValue = new EditValue(value, formTextField.getText());
                valid.setValue(editValue.execute());

                if (valid.getValue()) {
                    CareTaker.addMemento(editValue);
                } else {
                    valid.set(value.isValid());
                }
                formTextField.setText(value.getValue().toString());
                for (DraggableModule dm : MainWindow.instance().getSelectedModules()) {
                    dm.getModule().validate();
                }
            }
        });

        valid.addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                // warning
                statusIcon.setImage(new Image("images/warning.png"));

            } else if (newValue && !oldValue) {
                // ok
                statusIcon.setImage(null);

            }
        });
    }

    public boolean isValid() {
        return value.isValid();
    }

}
