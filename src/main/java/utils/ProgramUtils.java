package utils;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import main.Main;

import java.util.Set;

/**
 * Created by felipe on 05/05/16.
 */
public class ProgramUtils {

    public static KeyCombination resetZoomCombination = new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.CONTROL_ANY);
    public static KeyCombination selectAllCombination = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);
    public static KeyCombination saveCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY);
    public static KeyCombination copyCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
    public static KeyCombination pasteCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);
    public static KeyCombination undoCombination = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_ANY);
    public static KeyCombination redoCombination = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_ANY, KeyCombination.SHIFT_ANY);

    public static boolean validateModules(Set<String> keys) {
        for (String key : keys) {
            if (!Main.modules.get(key).isValid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Buttons Section
     */


    /**
     *
     * @param button
     */
    public static void setOnPressedButton(Button button) {
        Effect oldEffect = button.getEffect();
        Effect dropShadow = new DropShadow(10, Color.BLACK);
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> button.setEffect(dropShadow));
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> button.setEffect(oldEffect));
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

    public static Image getPlusImage() {
        return new Image("images/plus.png");
    }

}
