package utils;

import javafx.scene.input.ClipboardContent;
import main.Main;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * Created by felipe on 02/03/16.
 */
public class PipelineManager {


    private ClipboardContent clipboard = new ClipboardContent();


    public boolean save(File output, JSONObject pipelineModules, JSONArray pipelineLinks) {

        FileWriter writer = null;
        try {
            writer = new FileWriter(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.write(Converter.getPipelineString(pipelineModules, pipelineLinks));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }


    public boolean load(File input) {
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

        clipboard.put(Converter.MODULES_DATA_FORMAT, root.get("modules"));
        clipboard.put(Converter.LINKS_DATA_FORMAT, root.get("links"));

        return true;
    }

//    private Map<String, Module> getModules(JSONObject jsonModules) {
//        Map<String, Module> modules = new HashMap<>();
//        // ClipboardContent content = Main.modules;
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














