package commands;

import controller.MainWindow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import main.Main;
import model.Module;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Converter;
import utils.PipelineManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 23/03/16.
 */
public class Paste implements Command {

    private ClipboardContent clipboard;
    private Map<String, JSONObject> jsonModules = new HashMap<>();
    private Map<String, JSONObject> jsonLinks = new HashMap<>();
    private MouseEvent mousePos;

    public Paste(MouseEvent mousePos) {
        this.mousePos = mousePos;

    }

    @Override
    public boolean execute() {

        if (!Main.modulesClipboard.isEmpty()) {

            jsonModules = Main.modulesClipboard;
            jsonLinks = Main.linksClipboard;


            Command importElements = new Import(getModules(), getLinks(), mousePos);
            importElements.execute();
//            for(String key: jsonModules.keySet()){
//                JSONObject current = jsonModules.get(key);
//                Module module = Converter.jsonToModule(key, current);
//            }

            return true;
        }


        Clipboard systemClipboard = Clipboard.getSystemClipboard();
        String externalJSON = systemClipboard.getString();
        if (externalJSON != null) {
            clipboard = PipelineManager.getClipboard(externalJSON);

            JSONObject jsonModules = (JSONObject) clipboard.get(Converter.MODULES_DATA_FORMAT);
            JSONArray jsonLinks = (JSONArray) clipboard.get(Converter.LINKS_DATA_FORMAT);

//            System.out.println(jsonModules.toString(4));
//            System.out.println(jsonLinks.toString(4));


            Command importElements = null;
            if (jsonModules != null) {
                importElements = new Import(jsonModules.getJSONObject("modules"), jsonLinks.getJSONArray(0), mousePos);
            } else if (systemClipboard.getFiles().size()>0) {
                File file = systemClipboard.getFiles().get(0);

                System.out.println(systemClipboard.getFiles());
                importElements = new Import(file);
            }

            if (importElements != null)
                importElements.execute();
            else
                MainWindow.stackedLogBar.displayWarning("No pipeline found in clipboard!");

//            if(jsonModules == null){
//                MainWindow.stackedLogBar.displayWarning("No pipeline found in clipboard!");
//
//            }else{
//                MainWindow.stackedLogBar.displaySuccess("Hurra!");
//            }


            //System.out.println(jsonModules.toString(4));
        }


        return true;
    }

    private JSONObject getModules() {
        JSONObject modules = new JSONObject();


        jsonModules.keySet().forEach(key -> {
            modules.put(key, jsonModules.get(key));
        });

        return modules;
    }

    private JSONArray getLinks() {

        if (jsonLinks.isEmpty())
            return null;

        JSONArray links = new JSONArray();

        jsonLinks.values().forEach(jsonObject -> {
            links.put(jsonObject);
        });

        return links;
    }


    @Override
    public boolean reverse() {
        return false;
    }

}
