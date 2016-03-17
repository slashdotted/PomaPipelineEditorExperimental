package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import main.Main;
import model.ModuleTemplate;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Marco on 10/03/2016.
 */
public class DraggableModule extends Pane {

    private UUID draggableModuleID;
    private Point2D mDragOffset = new Point2D (0.0, 0.0);


    @FXML
    private Pane modelPane;
    @FXML
    private Label modelItemLabel;

    @FXML
    private ImageView modelItemImage;

    public DraggableModule(UUID idModelModule ){

        this.draggableModuleID = idModelModule;

        //TODO set default to create and show view

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/moduleDraggable.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ModuleTemplate temp=Main.templates.get(idModelModule);
        System.out.println("in draggableModule");
        System.out.println(temp.getName());
        this.modelItemLabel.setText(temp.getName());
        this.modelItemImage.setImage(new Image(temp.getImageURL()));
    }

    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);

        relocate (
                (int) (localCoords.getX() - mDragOffset.getX()),
                (int) (localCoords.getY() - mDragOffset.getY())
        );
    }


}
