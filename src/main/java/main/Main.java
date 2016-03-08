package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    //TODO
    //variabile generale dovvremo trovare il suo posto giusto;
    static RootLayout editor;

    @Override
    public void start(Stage primaryStage) throws Exception{

        BorderPane root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

        //TODO
        //variabile generale dovvremo trovare il suo posto giusto;
        editor=new RootLayout();


//
    }


    public static void main(String[] args) {
        launch(args);

    }
}
