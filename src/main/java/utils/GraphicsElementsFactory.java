package utils;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import model.Module;
import model.ModuleTemplate;
import model.Value;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Created by felipe on 05/04/16.
 */
public class GraphicsElementsFactory {






    /**
     * Generates a box with an ok and a cancel Buttons
     * to add onAction on buttons just get them with lookup on this box
     *
     * @return
     */
    public static HBox getOkCancelBox() {
        HBox box = new HBox(10);
        box.setFillHeight(true);
        box.setStyle("-fx-padding: 10;");
        Button okButton = new Button("ok");
        Button cancelButton = new Button("cancel");
        okButton.setId("okButton"); // id for lookup
        cancelButton.setId("cancelButton"); // id for lookup

        okButton.setTooltip(new Tooltip("Accept and save"));
        box.getChildren().addAll(okButton, cancelButton);
        return box;
    }


    /**
     * Generates a box with a Label and a textField for data insertion
     *
     * @param name
     * @return
     */
    public static VBox getFormBox(String name) {
        VBox box = new VBox(10);
        box.setFillWidth(true);
        box.setStyle("-fx-background-color: cornsilk");
        box.setSpacing(5);
        Label label = new Label(name);
        TextField textField = new TextField();
        textField.setPromptText("insert here " + name + "...");
        label.setId(name + "Label");
        textField.setId(name + "TextField");
        box.getChildren().addAll(label, textField);

        return box;
    }


//    public static VBox moduleConfBox(Module module) {
//        VBox mainBox = new VBox();
//        mainBox.getChildren().add(new Label("Template test"));
//        Map<String, Value> params = module.getParameters();
//        ArrayList<String> cParams = module.getCParams();
//
//        ModuleTemplate template = module.getTemplate();
//
//        template.getMandatoryParameters().keySet().forEach(key -> {
//            VBox current = getFormBox(key);
//            mainBox.getChildren().add(current);
//            mainBox.getChildren().add(getSeparator());
//        });
//
//        template.getOptParameters().keySet().forEach(key -> {
//            VBox current = getFormBox(key);
//            mainBox.getChildren().add(current);
//            mainBox.getChildren().add(getSeparator());
//        });
//
//
//        mainBox.getChildren().add(getOkCancelBox());
//
//        return mainBox;
//    }


//    public static VBox moduleConfBox(Module module) {
//        VBox mainBox = new VBox();
//        mainBox.getChildren().add(new Label("Template test"));
//        Map<String, Value> params = module.getParameters();
//        ArrayList<String> cParams = module.getCParams();
//
//        ModuleTemplate template = module.getTemplate();
//
//        template.getMandatoryParameters().keySet().forEach(key -> {
//            VBox current = getFormBox(key);
//            mainBox.getChildren().add(current);
//            mainBox.getChildren().add(getSeparator());
//        });
//
//        template.getOptParameters().keySet().forEach(key -> {
//            VBox current = getFormBox(key);
//            mainBox.getChildren().add(current);
//            mainBox.getChildren().add(getSeparator());
//        });
//
//
//        mainBox.getChildren().add(getOkCancelBox());
//
//        return mainBox;
//    }

    public static Separator getSeparator() {
        Insets insets = new Insets(2, 5, 2, 5);
        Separator separator = new Separator();
        separator.setPadding(insets);
        return separator;
    }

    public static boolean showWarning(String title,String message) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        //alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();


        return (result.get()==ButtonType.OK);
    }

    public static boolean saveDialog(String action) {
        Alert saveAlert = new Alert(Alert.AlertType.WARNING);
        saveAlert.setWidth(400);

        saveAlert.setTitle("Pipeline not saved");
        saveAlert.setHeaderText(null);
        saveAlert.setContentText("Do you want to save before " + action + "?");
        ButtonType buttonTypeSave = new ButtonType("Save");
        ButtonType buttonTypeNotSave = new ButtonType("Don't save");
        ButtonType buttonTypeCancel = new ButtonType("Cancel");
        saveAlert.getButtonTypes().setAll(buttonTypeSave, buttonTypeNotSave, buttonTypeCancel);
        Optional<ButtonType> result = saveAlert.showAndWait();

        if(result.get() == buttonTypeSave){
            Main.root.savePipeline();
            return true;
        }else if(result.get() == buttonTypeNotSave){
            return true;
        }

        return false;
    }



}
