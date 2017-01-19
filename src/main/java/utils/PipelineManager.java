package utils;

import controller.MainWindow;
import javafx.scene.input.ClipboardContent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import model.Module;
import org.json.JSONString;

/**
 * Class for manage the persistence of a pipeline
 */
public class PipelineManager {

    public static String CURRENT_PIPELINE_PATH = null;

    private static ClipboardContent clipboard = new ClipboardContent();


    public boolean save(File output, JSONObject pipelineModules, JSONArray pipelineLinks, String source) {

        FileWriter writer = null;
        try {
            writer = new FileWriter(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.write(Converter.getPipelineString(pipelineModules, pipelineLinks, source));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    public boolean load(File input) {
        CURRENT_PIPELINE_PATH = input.getPath();
        FileReader fileReader = null;
        JSONObject root;

        try {
            fileReader = new FileReader(input);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JSONTokener parser = null;
        try {
            parser= new JSONTokener(fileReader);
            root = new JSONObject(parser);
            extractPipeline(root.toString());
        }catch (JSONException e){
            StackedLogBar.instance().logAndWarning("No modules here");
            return false;
        }

        return true;
    }

    public static void extractPipeline(String jsonString) {



        JSONObject pipeline = null;

        try {
            pipeline = new org.json.JSONObject(jsonString);
        }catch (JSONException e){
            StackedLogBar.instance().logAndWarning("No modules here");
        }

        if(pipeline == null)
            return;

        JSONObject jsonModules = null;
        JSONArray jsonLinks = null;
        String jsonSource = Module.getSource();

        if (pipeline.has("source")) {
            jsonSource = (String) pipeline.get("source");
        }
        if (pipeline.has("modules"))
            jsonModules = (JSONObject) pipeline.get("modules");
            if (jsonSource == null && !jsonModules.keySet().isEmpty()) {
                jsonSource = jsonModules.names().getString(0);
            }
        if (pipeline.has("links"))
            jsonLinks = (JSONArray) pipeline.get("links");
        

        clipboard.put(Converter.MODULES_DATA_FORMAT, jsonModules);
        clipboard.put(Converter.LINKS_DATA_FORMAT, jsonLinks);
        clipboard.put(Converter.SOURCE_DATA_FORMAT, jsonSource);
    }

    public static ClipboardContent getClipboard(String jsonString) {
        extractPipeline(jsonString);
        return clipboard;
    }

    public static ClipboardContent getClipboard() {
        return clipboard;
    }
}














