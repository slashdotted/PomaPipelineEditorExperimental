package commands;

import javafx.scene.input.ClipboardContent;
import main.Main;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.Converter;
import utils.PipelineManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by felipe on 23/03/16.
 */
public class Save implements Command {

    private File output;
    private Map<String, JSONObject> modules;
    private Map<String, JSONObject> links;

    /**
     * Constructor for selected items
     *
     * @param output
     * @param modules
     * @param links
     */
    public Save(File output, Map<String, JSONObject> modules, Map<String, JSONObject> links) {
        this.output = output;
        this.modules = modules;
        this.links = links;
    }

    /**
     * Constructor for all items
     *
     * @param output
     */
    public Save(File output) {
        this.output = output;
        this.modules = Main.modulesClipboard;
        this.links = Main.linksClipboard;
    }

    @Override
    public boolean execute() {

        JSONObject pipelineModules = new JSONObject();
        JSONArray pipelineLinks = new JSONArray();

        pipelineModules.putAll(modules);
        pipelineLinks.addAll(links.values());

        PipelineManager pipelineManager = new PipelineManager();

        return  pipelineManager.save(output, pipelineModules, pipelineLinks);
    }

    @Override
    public boolean reverse() {
        return false;
    }

}
