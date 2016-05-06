package utils;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Created by felipe on 05/05/16.
 */
public class ProgramUtils {

    public static KeyCombination resetZoomCombination = new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.CONTROL_ANY);
    public static KeyCombination selectAllCombination = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);
    public static KeyCombination saveCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY);


//    public static Task< ClipboardContent> importAll(File file, Integer importedFiles){
//        return new Task<ClipboardContent>() {
//            @Override
//            protected  ClipboardContent call() throws Exception {
//                PipelineManager pipelineLoader = new PipelineManager();
//                int importedModules = 0;
//                pipelineLoader.load(file);
//                ClipboardContent clipboard = pipelineLoader.getClipboard();
//                //TODO check duplicates before import
//
//                JSONObject jsonModules = (JSONObject) clipboard.get(Converter.MODULES_DATA_FORMAT);
//                JSONArray jsonArray = (JSONArray) clipboard.get(Converter.LINKS_DATA_FORMAT);
//
//                for (Object key : jsonModules.keySet()) {
//                    Module module = Converter.jsonToModule(String.valueOf(key), (JSONObject) jsonModules.get(key));
//                    Command addModule = new AddModule(module);
//                    addModule.execute();
//                    CareTaker.addMemento(addModule);
//                    importedModules++;
//                }
//
//                if (jsonArray != null)
//                    jsonArray.forEach(obj -> {
//                        JSONObject jsonLink = (JSONObject) obj;
//                        Link link = Converter.jsonToLink(jsonLink);
//                        Command addLink = new AddLink(link);
//                        addLink.execute();
//                        CareTaker.addMemento(addLink);
//                    });
//                System.out.println("imported");
//                return clipboard;
//            }
//        };
//    }


}
