package commands;

import controller.MainWindow;
import javafx.geometry.Point2D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import main.Main;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.CareTaker;
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
    private String source;
    private Point2D position;

    public Paste(Point2D position) {
        this.position = position;

    }

    @Override
    public boolean execute() {

        // Paste from internal data
        if (!Main.modulesClipboard.isEmpty()) {

            jsonModules = Main.modulesClipboard;
            jsonLinks = Main.linksClipboard;
            source = Main.sourceClipBoard;
            Command importElements = new Import(getModules(), getLinks(), source, position);
            importElements.execute();
            CareTaker.addMemento(importElements);
            return true;
        }


        // Paste from external data
        Clipboard systemClipboard = Clipboard.getSystemClipboard();
        String externalJSON = systemClipboard.getString();
        if (externalJSON != null) {
            clipboard = PipelineManager.getClipboard(externalJSON);

            JSONObject jsonModules = (JSONObject) clipboard.get(Converter.MODULES_DATA_FORMAT);
            JSONArray jsonLinks = (JSONArray) clipboard.get(Converter.LINKS_DATA_FORMAT);
            String source = (String) clipboard.get(Converter.SOURCE_DATA_FORMAT);
            
            Command importElements = null;
            if (jsonModules != null) {
                importElements = new Import(jsonModules, jsonLinks, source, position);
            } else if (systemClipboard.getFiles().size()>0) {
                File file = systemClipboard.getFiles().get(0);

                System.out.println(systemClipboard.getFiles());
                importElements = new Import(file);
            }

            if (importElements != null)
                importElements.execute();
            else
                MainWindow.stackedLogBar.displayWarning("No pipeline found in clipboard!");
        }


        return true;
    }

    private JSONObject getModules() {
        JSONObject modules = new JSONObject();

        jsonModules.keySet().forEach(key -> modules.put(key, jsonModules.get(key)));

        return modules;
    }

    private JSONArray getLinks() {

        if (jsonLinks.isEmpty())
            return null;

        JSONArray links = new JSONArray();

        jsonLinks.values().forEach(jsonObject -> links.put(jsonObject));

        return links;
    }


    @Override
    public boolean reverse() {
        return false;
    }

}
