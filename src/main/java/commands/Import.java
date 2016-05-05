package commands;

import controller.MainWindow;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import main.Main;
import model.Link;
import model.Module;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.CareTaker;
import utils.Converter;
import utils.PipelineManager;

import java.io.File;

/**
 * Created by felipe on 31/03/16.
 */
public class Import implements Command {

    private File file;
    private int importedFiles = 0;

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

            Command addModule = new AddModule(module);
            addModule.execute();
            CareTaker.addMemento(addModule);
            importedFiles++;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //TODO add memento
            // Main.modules.put(String.valueOf(key), module);
            // Main.modulesClipboard.put(String.valueOf(key), (JSONObject) jsonModules.get(key));
        });

        if (jsonArray != null)
            jsonArray.forEach(obj -> {
                JSONObject jsonLink = (JSONObject) obj;
                Link link = Converter.jsonToLink(jsonLink);
                Command addLink = new AddLink(link);
                addLink.execute();

            });
        // Main.modules.putAll(clipboard);
        return true;
    }


    @Override
    public boolean reverse() {
        for (int i = 0; i < importedFiles; i++) {
            CareTaker.undo();
        }
        PipelineManager.CURRENT_PIPELINE_PATH = null;
        Platform.runLater(() -> CareTaker.redoable.setValue(false));
//        Main.modules.keySet().forEach(s -> System.out.println(Main.modules.get(s)));
//        Main.links.keySet().forEach(s -> System.out.println(Main.links.get(s)));
//        MainWindow.allDraggableModule.keySet().forEach(s -> System.out.println(MainWindow.allDraggableModule.get(s)));
//        MainWindow.allLinkView.keySet().forEach(s -> System.out.println(MainWindow.allLinkView.get(s)));
        return true;
    }

    public int getImportedFiles(){
        return importedFiles;
    }
}