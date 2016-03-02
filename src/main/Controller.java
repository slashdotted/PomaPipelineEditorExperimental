package main;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Controller {

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private Button redo;

    public void addCircle(){


        Rectangle rect = new Rectangle(200, 200, Color.RED);
      //  mainScrollPane.setContent(rect);


        Group group =new Group();
        group.getChildren().add(rect);
        group.getChildren().add(new Circle(400,400,100));
        mainScrollPane.setContent(group);

    }

}
