package commands;

import javafx.application.Platform;
import javafx.scene.input.ClipboardContent;
import model.Link;
import model.Module;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.CareTaker;
import utils.Converter;
import utils.PipelineManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 31/03/16.
 */
public class Import implements Command {

    private File file;
    private int importedElements = 0;
    Map<String, String> nameMappings = new HashMap<>();
    private ArrayList<Command> allCommands = new ArrayList<>();
    private Command executeAll;

    public Import(File file) {
        this.file = file;
    }


    @Override
    public boolean execute() {
//        ClipboardContent clipboard = Main.modules;
//        clipboard.clear();
        PipelineManager pipelineLoader = new PipelineManager();

        pipelineLoader.load(file);

        ClipboardContent clipboard = pipelineLoader.getClipboard();
        JSONObject jsonModules = (JSONObject) clipboard.get(Converter.MODULES_DATA_FORMAT);
        JSONArray jsonArray = (JSONArray) clipboard.get(Converter.LINKS_DATA_FORMAT);

        jsonModules.keySet().forEach(key -> {
            Module module = Converter.jsonToModule(String.valueOf(key), (JSONObject) jsonModules.get(key));

            if (!String.valueOf(key).equals(module.getName())) {
                nameMappings.put(String.valueOf(key), module.getName());
            }


            Command addModule = new AddModule(module);
            allCommands.add(addModule);
            //addModule.execute();
            //CareTaker.addMemento(addModule);
            importedElements++;
        });

        executeAll = new ExecuteAll(allCommands);
        boolean success = executeAll.execute();

        if (jsonArray != null) {
            jsonArray.forEach(obj -> {

                JSONObject jsonLink = (JSONObject) obj;
                String from = checkName(jsonLink, "from");
                String to = checkName(jsonLink, "to");
                Link link = Converter.jsonToLink(jsonLink, from, to);

                Command addLink = new AddLink(link);
                addLink.execute();
                //CareTaker.addMemento(addLink);
                //importedElements++;
            });
            // Main.modules.putAll(clipboard);
        }
        CareTaker.addMemento(executeAll);
        return success;
    }

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
//        for (int i = 0; i < importedElements; i++) {
//            CareTaker.undo();
//        }

        PipelineManager.CURRENT_PIPELINE_PATH = null;
        Platform.runLater(() -> CareTaker.redoable.setValue(false));
//        Main.modules.keySet().forEach(s -> System.out.println(Main.modules.get(s)));
//        Main.links.keySet().forEach(s -> System.out.println(Main.links.get(s)));
//        MainWindow.allDraggableModule.keySet().forEach(s -> System.out.println(MainWindow.allDraggableModule.get(s)));
//        MainWindow.allLinkView.keySet().forEach(s -> System.out.println(MainWindow.allLinkView.get(s)));
        return executeAll.reverse();
    }

    public int getImportedElements() {
        return importedElements;
    }
}