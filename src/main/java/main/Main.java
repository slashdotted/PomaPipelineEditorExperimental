package main;

import controller.MainWindow;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Link;
import model.Module;
import model.ModuleTemplate;
import org.json.JSONObject;
import utils.EditorConfManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javafx.scene.image.Image;

public class Main extends Application {
    public static BooleanProperty dirty = new SimpleBooleanProperty(false);
    public static Map<String, ModuleTemplate> templates = new TreeMap<>();

    public static Map<String, Module> modules = new HashMap<>();
    public static Map<String, Link> links = new HashMap<>();

    public static Map<String, JSONObject> modulesClipboard = new HashMap<>();
    public static Map<String, JSONObject> linksClipboard = new HashMap<>();
    public static String sourceClipBoard = null;
    public static Scene mScene;
    public static MainWindow root;


    @Override
    public void start(Stage primaryStage) throws Exception {

        EditorConfManager confManager = new EditorConfManager();
        File conf = new File("conf_final.json");
        confManager.load(conf);


        root = new MainWindow();


        primaryStage.setTitle("Poma Pipeline Editor");

        dirty.addListener((observable, oldValue, newValue) -> {
            if (observable.getValue()) {
                primaryStage.setTitle("*Poma Pipeline Editor");
            } else {
                primaryStage.setTitle("Poma Pipeline Editor");
            }
        });

        mScene = new Scene(root, 1000, 600);
        //primaryStage.setMaximized(true);
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(800);
        primaryStage.setScene(mScene);
        primaryStage.getIcons().add(new Image("images/poma_logo.png"));

        primaryStage.setOnCloseRequest(event -> {
            root.exitApplication();
            event.consume();
        });
        root.initializeSelectionArea();
        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);

    }


}
