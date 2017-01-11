package commands;

import main.Main;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.PipelineManager;

import java.io.File;
import java.util.Map;
import org.json.JSONString;

/**
 * Command for writing a pipeline in a JSON file
 */
public class Save implements Command {

    private File output;
    private Map<String, JSONObject> modules;
    private Map<String, JSONObject> links;
    private String source;

    /**
     * Constructor for selected items
     *
     * @param output
     * @param modules
     * @param links
     */
    public Save(File output, Map<String, JSONObject> modules, Map<String, JSONObject> links, String source) {
        this.output = output;
        this.modules = modules;
        this.links = links;
        this.source = source;
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
        this.source = Main.sourceClipBoard;
    }

    @Override
    public boolean execute() {
        PipelineManager.CURRENT_PIPELINE_PATH = output.getPath();

        JSONObject pipelineModules = new JSONObject();
        JSONArray pipelineLinks = new JSONArray();

        modules.keySet().forEach(key -> pipelineModules.put(key, modules.get(key)));

        links.values().forEach(jsonObject -> pipelineLinks.put(jsonObject));

        PipelineManager pipelineManager = new PipelineManager();
        if(pipelineManager.save(output, pipelineModules, pipelineLinks, source)) {
            Main.dirty.setValue(false);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean reverse() {
        Main.dirty.setValue(false);
        return false;
    }

}
