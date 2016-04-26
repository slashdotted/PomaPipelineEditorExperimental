package utils;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Created by felipe on 14/04/16.
 */
public class ParamBox extends HBox {

    private StringProperty param = new SimpleStringProperty();

    private TextField textField = new TextField();
    private Button actionButton = new Button();

    public ParamBox(SimpleStringProperty param) {
        this.param = param;
        this.setSpacing(10);
        this.setPadding(new Insets(10, 5, 5, 10));
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPadding(new Insets(0,10,0,10));
        this.getChildren().addAll(textField, actionButton);

        textField.setMaxWidth(Double.MAX_VALUE);
        this.setHgrow(textField, Priority.ALWAYS);

        textField.setEditable(true);

        Bindings.bindBidirectional(textField.textProperty(), this.param);

        //textField.textProperty().bindBidirectional(this.param);
        //textField.textProperty().bind(this.param);

        textField.setOnKeyTyped(event -> {

            Platform.runLater(() -> {
                this.param.set(textField.getText());
                System.out.println(this.param);
            });
        });
        actionButton.setGraphic(new ImageView("images/minus.png"));

       // System.out.println("Created paramBox with:" + param);

    }

    public String getParam() {
        return param.get();
    }

    public StringProperty paramProperty() {
        return param;
    }

    public void setParam(String param) {
        this.param.set(param);
    }

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public Button getActionButton() {
        return actionButton;
    }

    public void setActionButton(Button actionButton) {
        this.actionButton = actionButton;
    }



}
