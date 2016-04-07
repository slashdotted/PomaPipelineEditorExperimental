package commands;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import main.Main;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 23/03/16.
 */
public class Copy implements Command {

    public Map<String, JSONObject> modules = new HashMap<>();
    public Map<String, JSONObject> links = new HashMap<>();


    /**
     * Constructor for all items
     */
    public Copy() {
        this.modules = Main.modulesClipboard;
        this.links = Main.linksClipboard;
    }

    /**
     * Constructor for selected items
     *
     * @param modules
     * @param links
     */
    public Copy(Map<String, JSONObject> modules, Map<String, JSONObject> links) {
        this.modules = modules;
        this.links = links;
    }

    @Override
    public boolean execute() {
        ClipboardContent clipboardContent = new ClipboardContent();

        JSONObject modulesObject = new JSONObject();
        modulesObject.putAll(modules);

        JSONArray linksArray = new JSONArray();
        linksArray.addAll(links.values());

        clipboardContent.putString(Converter.getPipelineString(modulesObject, linksArray));

        Clipboard systemClipboard = Clipboard.getSystemClipboard();

        // systemClipBoard.setContent(clipboard);

        return false;
    }

    @Override
    public boolean reverse() {
        return false;
    }

}
