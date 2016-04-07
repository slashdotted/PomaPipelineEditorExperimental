package commands;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import main.Main;
import model.Link;
import model.Module;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.Converter;
import utils.PipelineManager;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by felipe on 31/03/16.
 */
public class Import implements Command {

    private File file;


    public Import(File file) {
        this.file = file;
    }

    @Override
    public void execute() {
        //ClipboardContent clipboard = Main.modulesClipboard;
        //clipboard.clear();
        PipelineManager pipelineLoader = new PipelineManager();

        pipelineLoader.load(file);

        ClipboardContent clipboard = pipelineLoader.getClipboard();

        //TODO check duplicates before import

        JSONObject jsonModules = (JSONObject) clipboard.get(PipelineManager.MODULES_DATAFORMAT);
        JSONArray jsonArray = (JSONArray) clipboard.get(PipelineManager.LINKS_DATAFORMAT);


        jsonModules.keySet().forEach(key -> {
            Module module = Converter.jsonToModule(String.valueOf(key), (JSONObject) jsonModules.get(key));
            Main.modules.put(String.valueOf(key), module);
            Main.modulesClipboard.put(String.valueOf(key), (JSONObject) jsonModules.get(key));
        });

        jsonArray.forEach(obj -> {
            JSONObject jsonLink = (JSONObject) obj;
            Link link = Converter.jsonToLink(jsonLink);
            Main.links.put(link.getID(), link);
            Main.linksClipboard.put(link.getID(),(JSONObject) obj);
        });


        // Main.modulesClipboard.putAll(clipboard);
    }

    @Override
    public void revert() {

    }
}