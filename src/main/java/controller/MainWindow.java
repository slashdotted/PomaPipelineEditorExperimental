package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import main.Main;
import model.ModuleTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainWindow extends BorderPane {

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private VBox moduleVBox;



    @FXML
    private Button redo;

    public MainWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainWindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initialize(){

        System.out.println(Main.templates.isEmpty());

        List<ModuleTemplate> templates = new ArrayList<>(Main.templates.values());

        Collections.sort(templates, (o1, o2) -> {
            //System.out.println(((ModuleTemplate)o2).getName());
            return o1.getName().compareTo(o2.getName());
        });

        for (ModuleTemplate template: templates) {
            System.out.println(template.getId());

            ModuleItem item = new ModuleItem(template.getId());
            moduleVBox.getChildren().add(item);
            System.out.println("ModuleItem created");
        }

    }


    public void addCircle() {

        Rectangle rect = new Rectangle(200, 200, Color.RED);
        //  mainScrollPane.setContent(rect);

        Group group = new Group();
        group.getChildren().add(rect);
        group.getChildren().add(new Circle(400, 400, 100));
        mainScrollPane.setContent(group);


    }

}
