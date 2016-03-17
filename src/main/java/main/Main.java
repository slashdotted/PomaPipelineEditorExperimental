package main;

import controller.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.ModuleTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Main extends Application {

    public static Map<UUID, ModuleTemplate> templates = new TreeMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception{

        for (int i = 0; i<10; i++){
            ModuleTemplate tmp = ModuleTemplate.getInstance();
            //System.out.println(tmp.getId());

            tmp.setName("tmp" + i);
            tmp.setImageURL("moduleImage.png");
            tmp.setDescription("Description " + i);
            tmp.setType("Type " + i);

            templates.put(tmp.getId(), tmp);
        }

        BorderPane root = new MainWindow();

        primaryStage.setTitle("PoorMans Pipeline Editor");
        primaryStage.setScene(new Scene(root, 600, 600));



        primaryStage.show();






            }


    public static void main(String[] args) {
        launch(args);

    }
}
