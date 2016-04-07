package commands;

import javafx.scene.input.ClipboardContent;
import main.Main;
import model.Link;
import model.Module;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.Converter;
import utils.PipelineManager;

import java.io.File;

/**
 * Created by felipe on 31/03/16.
 */
public class Import implements Command {

    private File file;


    public Import(File file) {
        this.file = file;
    }

    @Override
    public boolean execute() {
        //ClipboardContent clipboard = Main.modules;
        //clipboard.clear();
        PipelineManager pipelineLoader = new PipelineManager();

        pipelineLoader.load(file);

        ClipboardContent clipboard = pipelineLoader.getClipboard();

        //TODO check duplicates before import

        JSONObject jsonModules = (JSONObject) clipboard.get(Converter.MODULES_DATA_FORMAT);
        JSONArray jsonArray = (JSONArray) clipboard.get(Converter.LINKS_DATA_FORMAT);


        jsonModules.keySet().forEach(key -> {
            Module module = Converter.jsonToModule(String.valueOf(key), (JSONObject) jsonModules.get(key));

            Main.modules.put(String.valueOf(key), module);
            Main.modulesClipboard.put(String.valueOf(key), (JSONObject) jsonModules.get(key));
        });

        jsonArray.forEach(obj -> {
            JSONObject jsonLink = (JSONObject) obj;
            Link link = Converter.jsonToLink(jsonLink);
            Main.links.put(link.getID(), link);
            String channel = (String)jsonLink.get("channel");
            if(channel == null)
                channel = "default";
            Main.linksClipboard.put(link.getJsonId(channel),jsonLink);

        });


        // Main.modules.putAll(clipboard);
        return true;
    }

    @Override
    public boolean reverse() {
        return false;
    }

}