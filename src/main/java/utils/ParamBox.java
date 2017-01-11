package utils;

import commands.Command;
import commands.EditStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * Class for wrap a cparam value
 */
public class ParamBox extends HBox {

    private SimpleStringProperty param;

    private TextField textField = new TextField();
    private Button delete = new Button();
    private String oldString = "";

    public ParamBox(SimpleStringProperty param) {
        this.param = param;
        this.setSpacing(5);
        this.setPadding(new Insets(5, 15, 5, 10));
        this.setAlignment(Pos.TOP_LEFT);
        this.setMaxWidth(Double.MAX_VALUE);
        this.getChildren().addAll(textField, delete);
        textField.setText(param.getValue());

        textField.setMaxWidth(Double.MAX_VALUE);
        this.setHgrow(textField, Priority.ALWAYS);
        //delete.setShape(new Circle(10));
        delete.setPadding(Insets.EMPTY);
        delete.setBackground(Background.EMPTY);
        textField.setEditable(true);

        textField.setOnMouseClicked(event -> {
            oldString = textField.getText();
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue && !newValue){
                if(!textField.getText().equals(oldString)){
                    Command edit = new EditStringProperty(param, textField.getText());
                    edit.execute();
                    CareTaker.addMemento(edit);
                }
            }
        });

        delete.setGraphic(new ImageView("images/minus.png"));

        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);

    }


    public String getParam() {
        return param.get();
    }

    public SimpleStringProperty paramProperty() {
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



    public Button getDelete() {
        return delete;
    }


}
