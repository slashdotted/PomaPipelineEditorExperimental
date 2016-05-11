package commands;

import main.Main;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.PipelineManager;

import java.io.File;
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
        PipelineManager.CURRENT_PIPELINE_PATH = output.getPath();

        //debug("Modules size:  " + modules.size() + " links size: " + links.size());
        JSONObject pipelineModules = new JSONObject();
        JSONArray pipelineLinks = new JSONArray();

        modules.keySet().forEach(key -> {
            pipelineModules.put(key, modules.get(key));
        });

        links.values().forEach(jsonObject -> {
            pipelineLinks.put(jsonObject);
        });

        PipelineManager pipelineManager = new PipelineManager();
        if(pipelineManager.save(output, pipelineModules, pipelineLinks)) {
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
