package commands;

import controller.MainWindow;
import javafx.application.Platform;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
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

/**
 * Created by felipe on 31/03/16.
 */
public class Import implements Command {

    private File file = null;
    private int importedElements = 0;
    Map<String, String> nameMappings = new HashMap<>();
    private ArrayList<Command> allCommands = new ArrayList<>();
    private Command executeAll;
    private JSONObject jsonModules = null;
    private JSONArray jsonArray = null;
    private MouseEvent mousePos = null;

    public Import(File file) {
        this.file = file;
    }

    public Import(JSONObject jsonModules, JSONArray jsonArray, MouseEvent mousePos) {
        this.jsonModules = jsonModules;
        this.jsonArray = jsonArray;
    }


    @Override
    public boolean execute() {
//        ClipboardContent clipboard = Main.modules;
//        clipboard.clear();
        if (file != null) {
            PipelineManager pipelineLoader = new PipelineManager();
            pipelineLoader.load(file);
            ClipboardContent clipboard = pipelineLoader.getClipboard();
            jsonModules = (org.json.JSONObject) clipboard.get(Converter.MODULES_DATA_FORMAT);
            jsonArray = (org.json.JSONArray) clipboard.get(Converter.LINKS_DATA_FORMAT);
        }

        if (jsonModules == null)
            return false;


        jsonModules.keySet().forEach(key -> {
            Module module = Converter.jsonToModule(String.valueOf(key), (org.json.JSONObject) jsonModules.get(key));

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
            //jsonArray.forEach(obj -> {
            System.out.println(jsonArray.get(0));
            for (int i =0; i<jsonArray.length(); i++) {


                JSONObject jsonLink = jsonArray.getJSONObject(i);
                String from = checkName(jsonLink, "from");
                System.out.println("-------------------From: " + from);
                String to = checkName(jsonLink, "to");
                System.out.println("-------------------To: " + to);
                Main.modules.keySet().forEach(s -> System.out.println(s));
                if ((Main.modules.get(from) != null && Main.modules.get(to) != null)) {
                    Link link = Converter.jsonToLink(jsonLink, from, to);
                    System.out.println(link.getID());
                    Command addLink = new AddLink(link);
                    addLink.execute();
                } else {
                    success = false;

                    MainWindow.stackedLogBar.logAndWarning("One or more modules not found for link: from " + from + " to " + to);
                }
            }
            //CareTaker.addMemento(addLink);
            //importedElements++;
            //  });
            // Main.modules.putAll(clipboard);
        }
        CareTaker.addMemento(executeAll);
        return success;
    }

    private String checkName(JSONObject jsonLink, String key) {
        String oldName = (String) jsonLink.get(key);
        String mapped = nameMappings.get(oldName);
        //System.out.println("Key " + key + " old " +oldName + " new " + mapped + " channel " + jsonLink.get("channel"));

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