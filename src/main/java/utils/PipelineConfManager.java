package utils;

import main.Main;
import model.Module;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 02/03/16.
 */
public class PipelineConfManager implements PersistenceManager {


    @Override
    public void save(File output) {
        //TODO
    }

    @Override
    public void load(File input) {
        FileReader fileReader = null;
        JSONParser parser = new JSONParser();
        JSONObject root = new JSONObject();

        try {
            fileReader = new FileReader(input);
            root = (JSONObject) parser.parse(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Modules import
        final JSONObject jsonModules = (JSONObject) root.get("modules");
        Main.modules.putAll(getModules(jsonModules));

//        for (Object key : root.keySet()) {
//            //TODO check for comment parameter
//        }


    }

    private Map<String, Module> getModules(JSONObject jsonModules) {
        Map<String, Module> modules = new HashMap<>();

        jsonModules.keySet().forEach( key -> {

        });


        return modules;
    }


    private boolean checkIsComment(String key) {
        return key.charAt(0) == '#';
    }
}














