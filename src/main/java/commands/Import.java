package commands;

import controller.MainWindow;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.ClipboardContent;
import main.Main;
import model.Link;
import model.Module;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.CareTaker;
import utils.Converter;
import utils.PipelineManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONString;

/**
 * Command for import a pipeline from different sources.
 * It is used for import from a file or from JSON structures already in memory.
 */
public class Import implements Command {

    private File file = null;
    private int importedElements = 0;
    Map<String, String> nameMappings = new HashMap<>();
    private ArrayList<Command> allCommands = new ArrayList<>();
    private Command executeAll;
    private JSONObject jsonModules = null;
    private JSONArray jsonLinks = null;
    private String jsonSource = null;
    private Point2D position = null;

    public Import(File file) {
        this.file = file;
    }

    public Import(JSONObject jsonModules, JSONArray jsonLinks, String source, Point2D position) {
        this.jsonModules = jsonModules;
        this.jsonLinks = jsonLinks;
        this.jsonSource = source;
    }


    /**
     * The execution of this command makes use of two structures: jsonModules and jsonLinks,
     * these structures are populated from a file parsing or from a paste of copied elements.
     * @return
     */
    @Override
    public boolean execute() {
        // Data from file
        if (file != null) {
            PipelineManager pipelineLoader = new PipelineManager();
            boolean loaded = pipelineLoader.load(file);
            if (!loaded)
                return false;
            ClipboardContent clipboard = pipelineLoader.getClipboard();
            jsonModules = (org.json.JSONObject) clipboard.get(Converter.MODULES_DATA_FORMAT);
            jsonLinks = (org.json.JSONArray) clipboard.get(Converter.LINKS_DATA_FORMAT);
            jsonSource = (String) clipboard.get(Converter.SOURCE_DATA_FORMAT);
        }

        if (jsonModules == null)
            return false;

        // Import of Modules
        // Here it's produced a collection of AddModule commands, it's very important to this before the importing of links
        jsonModules.keySet().forEach(key -> {
            JSONObject jsonModule = jsonModules.getJSONObject(key);
            Module module = Converter.jsonToModule(String.valueOf(key), jsonModule);
            if (module != null) {
                Main.modules.put(module.getName(), module);

                // Here it's checked if module's name has been changed,
                // so during the link's import, new names are managed
                if (!String.valueOf(key).equals(module.getName())) {
                    nameMappings.put(String.valueOf(key), module.getName());
                    MainWindow.stackedLogBar.log(String.valueOf(key) + " already present, renamed in " + module.getName());
                }

                Command addModule = new AddModule(module);
                allCommands.add(addModule);

                importedElements++;
            } else {
                MainWindow.stackedLogBar.logAndWarning("Module " + String.valueOf(key) + " does not have a Template in conf!");
            }
        });

        // Adds all modules before, so in the next phase links can find their modules
        executeAll = new ExecuteAll(allCommands);

        boolean success = executeAll.execute();

        // Import of links
        if (jsonLinks != null) {

            for (int i = 0; i < jsonLinks.length(); i++) {

                JSONObject jsonLink = jsonLinks.getJSONObject(i);
                String from = checkName(jsonLink, "from");
                String to = checkName(jsonLink, "to");
                if ((Main.modules.get(from) != null && Main.modules.get(to) != null)) {
                    Link link = Converter.jsonToLink(jsonLink, from, to);
                    Command addLink = new AddLink(link);
                    addLink.execute();
                } else {
                    success = false;
                    MainWindow.stackedLogBar.logAndWarning("One or more modules not found for link: from " + from + " to " + to);
                }
            }

        }
        
        if (jsonSource != null) {
            Command editSource = new EditModule(Main.modules.get(jsonSource), EditModule.Type.Source, jsonSource.toString());
            if (editSource.execute()) {
                CareTaker.addMemento(editSource);
            }
        }
        return success;
    }


    /**
     * This method returns the correct module's name associated to the given link
     * @param jsonLink
     * @param key
     * @return
     */
    private String checkName(JSONObject jsonLink, String key) {
        String oldName = (String) jsonLink.get(key);
        String mapped = nameMappings.get(oldName);
        if (mapped == null)
            return oldName;
        else
            return mapped;
    }


    @Override
    public boolean reverse() {
        PipelineManager.CURRENT_PIPELINE_PATH = null;
        Platform.runLater(() -> CareTaker.redoable.setValue(false));
        return executeAll.reverse();
    }

    public int getImportedElements() {
        return importedElements;
    }
}