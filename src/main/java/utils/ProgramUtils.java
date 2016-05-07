package utils;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
    //public static KeyCombination pasteCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);
    public static KeyCombination undoCombination = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_ANY);
    public static KeyCombination redoCombination = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_ANY, KeyCombination.SHIFT_ANY);

    public static boolean validateModules(Set<String> keys){
        for(String key :keys){
            if(!Main.modules.get(key).isValid()){
                return false;
            }
        }
        return true;
    }




}
