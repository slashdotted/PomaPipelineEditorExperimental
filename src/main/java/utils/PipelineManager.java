package utils;

import javafx.scene.input.ClipboardContent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import java.io.*;

/**
 * Created by felipe on 02/03/16.
 */
public class PipelineManager {

    public static String CURRENT_PIPELINE_PATH = null;

    private static ClipboardContent clipboard = new ClipboardContent();


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
        CURRENT_PIPELINE_PATH = input.getPath();
        //System.err.println("Current Path: "+CURRENT_PIPELINE_PATH);
        FileReader fileReader = null;
        JSONObject root;

        try {
            fileReader = new FileReader(input);


            // root = parser.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //extractPipeline(root.toJSONString());

        //Modules import
        //final JSONObject jsonModules = (JSONObject) root.get("modules");
        //final JSONArray jsonArray = (JSONArray) root.get("links");
        JSONTokener parser = null;
        try {
            parser= new JSONTokener(fileReader);
            root = new JSONObject(parser);
            extractPipeline(root.toString());
        }catch (JSONException e){
            System.err.println("No modules here");
            return false;
        }

        return true;
    }

    public static void extractPipeline(String jsonString) {



        JSONObject pipeline = null;

        try {
            pipeline = new org.json.JSONObject(jsonString);
        }catch (JSONException e){
           // System.err.println("No modules here");
        }

        if(pipeline == null)
            return;

        JSONObject jsonModules = null;
        JSONArray jsonLinks = null;
        if (pipeline.has("modules"))
            jsonModules = (JSONObject) pipeline.get("modules");
        if (pipeline.has("links"))
            jsonLinks = (JSONArray) pipeline.get("links");



        clipboard.put(Converter.MODULES_DATA_FORMAT, jsonModules);
        clipboard.put(Converter.LINKS_DATA_FORMAT, jsonLinks);
    }

    public static ClipboardContent getClipboard(String jsonString) {
        extractPipeline(jsonString);
        return clipboard;
    }

    public static ClipboardContent getClipboard() {
        return clipboard;
    }
}














