package utils;

import main.Main;
import model.Link;
import model.Module;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 07/04/16.
 */
public class BadElements {

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
