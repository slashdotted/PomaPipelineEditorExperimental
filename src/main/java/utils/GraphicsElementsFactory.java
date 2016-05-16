package utils;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.Main;

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


    /**
     * IMAGES SECTION
     */

    public static ImageView getPinImage(double size) {
        ImageView imageView = new ImageView("images/pin.png");
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        return imageView;
    }

    public static ImageView getUnpinImage(double size) {
        ImageView imageView = new ImageView("images/unpin.png");
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        return imageView;
    }

    public static ImageView getCloseImage(double size) {
        ImageView imageView = new ImageView("images/close.png");
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        return imageView;
    }

    public static ImageView getCloseImage2(double size) {
        ImageView imageView = new ImageView("images/close_red.png");
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        return imageView;
    }
}
