package main;

import commands.Command;
import commands.Import;
import commands.Save;
import controller.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Link;
import model.Module;
import model.ModuleTemplate;
import org.json.simple.JSONObject;
import utils.Converter;
import utils.EditorConfManager;
import utils.PersistenceManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Main extends Application {


    public static Map<String, ModuleTemplate> templates = new TreeMap<>();

    public static Map<String, Module> modules = new HashMap<>();
    public static Map<String, Link> links = new HashMap<>();

    public static Map<String, JSONObject> modulesClipboard = new HashMap<>();
    public static Map<String, JSONObject> linksClipboard = new HashMap<>();

    //public static String currentJSON = "";

    @Override
    public void start(Stage primaryStage) throws Exception {

        PersistenceManager confManager = new EditorConfManager();
        File conf = new File("conf.json");
        confManager.load(conf);


        // TODO remove this test
        conf = new File("pipeline_test.json");

        Command importCommand = new Import(conf);
        importCommand.execute();
        System.out.println("Imported modules:" + modules.size());

//        links.values().forEach(link -> {
//            System.out.println(link.getID());
//        });


//        modules.keySet().forEach(key -> {
//            System.out.println(key + ":" + Converter.moduleToJSON(modules.get(key)).toJSONString());
//        });

        System.out.println("\nImported links:" + links.size());
        System.out.println("\nClipBoarded links:" + linksClipboard.size());
        linksClipboard.keySet().forEach(key->{
            System.out.println(key + " : " + linksClipboard.get(key));
        });

//        linksClipboard.values().forEach(value ->{
//            System.out.println(value.toJSONString());
//        });

//        links.keySet().forEach(key -> {
//            System.out.println(key + " = " + Converter.linkToJSON(links.get(key)).toJSONString());
//        });

        Command save = new Save(null, modulesClipboard, linksClipboard);
        save.execute();


        // long startTime = System.currentTimeMillis();
        BorderPane root = new MainWindow();
        // System.out.println(System.currentTimeMillis()-startTime);

        primaryStage.setTitle("PoorMans Pipeline Editor");
        primaryStage.setScene(new Scene(root, 600, 600));


        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);

    }
}
