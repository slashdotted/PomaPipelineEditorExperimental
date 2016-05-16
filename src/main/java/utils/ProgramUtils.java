package utils;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
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
     *  Method used for the application of dropshadow effect to a button, when pressed.
     *  Useful for give a pressed feedback to the user
     * @param button
     */
    public static void setOnPressedButton(Button button) {
        Effect oldEffect = button.getEffect();
        Effect dropShadow = new DropShadow(10, Color.BLACK);
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> button.setEffect(dropShadow));
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> button.setEffect(oldEffect));
    }


    public static String checkDuplicateModules(String name, int counter) {

        for (String nameMod: Main.modules.keySet() ) {


            if(nameMod.equals(name)){
                if (counter==0){
                    name+= "_";
                }
                String[] split =name.toString().split("_");
                name = split[0] + "_" + counter++;
                return checkDuplicateModules(name.toString(), counter);
            }

        }
        return name;
    }
}
