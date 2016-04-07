package utils;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Module;
import model.Value;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by felipe on 05/04/16.
 */
public class GraphicsElementsFactory {


    /**
     * Generates a box with an ok and a cancel Buttons
     * to add onAction on buttons just get them with lookup on this box
     * @return
     */
    public static HBox okCancelBox(){
        HBox box = new HBox(20);
        Button okButton = new Button("ok");
        Button cancelButton = new Button("cancel");
        okButton.setId("okButton"); // id for lookup
        cancelButton.setId("cancelButton"); // id for lookup
        box.getChildren().addAll(okButton, cancelButton);
        return box;
    }


    /**
     * Generates a box with a Label and a textField for data insertion
     * @param name
     * @return
     */
    public static VBox fieldBox(String name){
        VBox box = new VBox(20);
        Label label = new Label(name);
        TextField textField = new TextField();
        textField.setPromptText("insert here " + name + "...");
        label.setId(name + "Label");
        textField.setId(name + "TextField");
        box.getChildren().addAll(label, textField);
        return box;
    }

    public static VBox moduleConfBox(Module module){
        VBox mainBox = new VBox();

        Map<String, Value> params = module.getParameters();
        ArrayList<String> cParams = module.getCparams();

        params.keySet().forEach(key ->{
            VBox current = fieldBox(key);

        });

        return mainBox;
    }
}
