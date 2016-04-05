package utils;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by felipe on 02/03/16.
 */
public class PipelineManager implements PersistenceManager {

    public static DataFormat MODULES_DATAFORMAT = new DataFormat("modules");
    public static DataFormat LINKS_DATAFORMAT = new DataFormat("links");


    private ClipboardContent clipboard = new ClipboardContent();

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
        //final JSONObject jsonModules = (JSONObject) root.get("modules");
        //final JSONArray jsonArray = (JSONArray) root.get("links");

        clipboard.put(MODULES_DATAFORMAT, root.get("modules"));
        clipboard.put(LINKS_DATAFORMAT, root.get("links"));


    }

//    private Map<String, Module> getModules(JSONObject jsonModules) {
//        Map<String, Module> modules = new HashMap<>();
//        // ClipboardContent content = Main.modulesClipboard;
//        jsonModules.keySet().forEach(key -> {
//            DataFormat keyDF = new DataFormat(key.toString());
//            clipboard.put(keyDF, (JSONObject) jsonModules.get(key));
//
//        });
//        JSONObject obj = new JSONObject();
//        return modules;
//    }
//
//
//    private boolean checkIsComment(String key) {
//        return key.charAt(0) == '#';
//    }

    public ClipboardContent getClipboard() {
        return clipboard;
    }
}














