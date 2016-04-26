package main;

import commands.Command;
import commands.Import;
import commands.Save;
import controller.MainWindow;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Link;
import model.Module;
import model.ModuleTemplate;
import org.json.simple.JSONObject;
import utils.Converter;
import utils.EditorConfManager;

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
    public static Scene mScene;
    public static BorderPane root;

    //public static String currentJSON = "";

    @Override
    public void start(Stage primaryStage) throws Exception {

        EditorConfManager confManager = new EditorConfManager();
        File conf = new File("conf.json");
        confManager.load(conf);


        // TODO remove this test
        conf = new File("pipeline_test.json");

        Command importCommand = new Import(conf);
        importCommand.execute();


        File saveFile = new File("pipeline_saved_test.json");

        Command save = new Save(saveFile, modulesClipboard, linksClipboard);
        save.execute();

        importCommand = new Import(saveFile);
        importCommand.execute();

        System.out.println("Imported modules:" + modules.size());
        modules.keySet().forEach(key -> {
            System.out.println(key + ":" + Converter.moduleToJSON(modules.get(key)).toJSONString());
       });


       System.out.println("\nImported links:" + links.size());
       System.out.println("ClipBoarded links:" + linksClipboard.size());
        linksClipboard.keySet().forEach(key -> {
            System.out.println(key + " : " + linksClipboard.get(key));
        });


        // long startTime = System.currentTimeMillis();
        root = new MainWindow();
        // System.out.println(System.currentTimeMillis()-startTime);

        primaryStage.setTitle("PoorMans Pipeline Editor");
        mScene=new Scene(root, 600, 600);
        primaryStage.setScene(mScene);


        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);

    }

    private static void clearData() {
        modules.clear();
        modulesClipboard.clear();

        links.clear();
        linksClipboard.clear();
    }


    // TODO remove this method
    public static void testPane(Parent node){
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(mScene.getWindow());
        stage.setTitle("Test Pane");
        stage.setScene(new Scene(node));
        //stage.setHeight(500);
        //stage.setWidth(300);
        stage.show();
    }


}
