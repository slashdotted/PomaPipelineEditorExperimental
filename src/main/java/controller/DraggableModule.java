package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.Main;
import model.Module;
import model.ModuleTemplate;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Marco on 10/03/2016.
 */
public class DraggableModule extends Pane {

    //private UUID draggableModuleID;
    private String type;
    private Point2D mDragOffset = new Point2D (0.0, 0.0);
    private Module module;

    private final DraggableModule self;


    @FXML
    private Pane modelPane;

    @FXML
    private Label modelItemLabel;

    @FXML
    private ImageView modelItemImage;

    public DraggableModule(String type ){

        this.type = type;

        //TODO set default to create and show view

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/moduleDraggable.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        self=this;
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ModuleTemplate temp=Main.templates.get(this.type);
        this.module =Module.getInstance(temp);


        System.out.println("in draggableModule");
        System.out.println(temp.getType());

        this.modelItemLabel.setText(temp.getType());
        this.modelItemImage.setImage(new Image(temp.getImageURL()));
    }

    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        System.out.println("entro wui");


        System.out.println("parent: " + getParent().getClass().getName());
        Group theparent = (Group) getParent();
        Point2D localCoords;
        localCoords = getParent().sceneToLocal(p);


        System.out.println((int) (localCoords.getX()) - mDragOffset.getX());
        System.out.println( (int) (localCoords.getY()) - mDragOffset.getY());



        relocate (
                (int) (localCoords.getX() - mDragOffset.getX()),
                (int) (localCoords.getY() - mDragOffset.getY())
        );


    }


    public Module getModule() {
        return module;
    }


}
