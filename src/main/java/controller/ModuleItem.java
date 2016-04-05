package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import main.Main;
import model.ModuleTemplate;

import java.io.IOException;

/**
 * Created by felipe on 08/03/16.
 */
public class ModuleItem extends Pane{

    private String type;
    private boolean isShadow;

    @FXML
    private Label modelItemLabel;

    @FXML
    private ImageView modelItemImage;

    //TODO add a module reference with getter and setter (Maybe new constructor?)




    public ModuleItem(String type, boolean isShadow){

        this.type = type;
        this.isShadow=isShadow;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/moduleItem.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @FXML
    private void initialize() {

        if(!isShadow) {
            setParameters(this.type);
        }

    }

    public void setParameters(String type) {

        // TODO fix label with associated module not template
        this.type = type;
        ModuleTemplate template = Main.templates.get(type);
        modelItemLabel.setText(template.getType());
        modelItemImage.setImage(new Image(template.getImageURL()));
        modelItemLabel.setTooltip(new Tooltip(template.getDescription()));
    }

    public String getTemplateType() {
        return this.type;
    }


    public void relocate (Point2D p) {

        //relocates the object to the new coordinates (point2D)

        Point2D localCoords = getParent().sceneToLocal(p);

        relocate (
                (int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
                (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
        );
    }
  /*  public String getName(){
      return  modelItemLabel.getText();
    }*/
}
