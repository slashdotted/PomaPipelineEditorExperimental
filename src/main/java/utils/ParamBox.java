package utils;

import commands.Command;
import commands.EditStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

/**
 * Created by felipe on 14/04/16.
 */
public class ParamBox extends HBox {

    private SimpleStringProperty param;

    private TextField textField = new TextField();
    //private Button actionButton = new Button();
    //private ImageView accept = new ImageView("images/accept.png");
    //private ImageView delete = new ImageView("images/minus.png");
    private Button delete = new Button();
    private String oldString = "";

    public ParamBox(SimpleStringProperty param) {
        this.param = param;
        this.setSpacing(5);
        this.setPadding(new Insets(5, 15, 5, 10));
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(Double.MAX_VALUE);
        //this.setPadding(new Insets(0,15,0,10));
        //this.getChildren().addAll(textField, accept, delete);
        this.getChildren().addAll(textField, delete);
        textField.setText(param.getValue());

        textField.setMaxWidth(Double.MAX_VALUE);
        this.setHgrow(textField, Priority.ALWAYS);
        delete.setShape(new Circle(10));
        delete.setPadding(Insets.EMPTY);
        textField.setEditable(true);

        textField.setOnMouseClicked(event -> {
            oldString = textField.getText();
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue && !newValue){
                if(!textField.getText().equals(oldString)){
                    Command edit = new EditStringProperty(param, textField.getText());
                    edit.execute();
                    //TODO add to memento
                }
            }
        });

        delete.setGraphic(new ImageView("images/minus.png"));
        //delete.setTooltip(new Tooltip("Delete this cparam"));

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

//    public ImageView getAccept() {
//        return accept;
//    }
//
//    public void setAccept(ImageView accept) {
//        this.accept = accept;
//    }

    public Button getDelete() {
        return delete;
    }

    //    public Button getActionButton() {
//        return actionButton;
//    }

//    public void setActionButton(Button actionButton) {
//        this.actionButton = actionButton;
//    }



}
