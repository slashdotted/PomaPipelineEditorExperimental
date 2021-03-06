package commands;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import main.Main;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Command for copy the current selection to System Clipboard
 */
public class Copy implements Command {

    public Map<String, JSONObject> modules = new HashMap<>();
    public Map<String, JSONObject> links = new HashMap<>();
    public String source = null;

    /**
     * Constructor for all items
     */
    public Copy() {
        this.modules = Main.modulesClipboard;
        this.links = Main.linksClipboard;
        this.source = Main.sourceClipBoard;
    }

    /**
     * Constructor for selected items
     *
     * @param modules
     * @param links
     */
    public Copy(Map<String, JSONObject> modules, Map<String, JSONObject> links, String source) {
        this.modules = modules;
        this.links = links;
        this.source = source;
    }

    @Override
    public boolean execute() {
        ClipboardContent clipboardContent = new ClipboardContent();

        JSONObject modulesObject = new JSONObject();

        modules.keySet().forEach(key -> {
            modulesObject.put(key, modules.get(key));
        });

        JSONArray linksArray = new JSONArray();
        links.values().forEach(link-> linksArray.put(link));

        clipboardContent.putString(Converter.getPipelineString(modulesObject, linksArray, source));

        Clipboard systemClipboard = Clipboard.getSystemClipboard();

        systemClipboard.setContent(clipboardContent);

        return false;
    }

    @Override
    public boolean reverse() {
        return false;
    }

}
