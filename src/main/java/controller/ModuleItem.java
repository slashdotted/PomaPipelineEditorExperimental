package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import main.Main;
import model.ModuleTemplate;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by felipe on 08/03/16.
 */
public class ModuleItem extends Pane{

    private UUID templateId;

    @FXML
    private Label modelItemLabel;

    @FXML
    private ImageView modelItemImage;


    public ModuleItem(UUID templateId){

        this.templateId = templateId;

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
        ModuleTemplate template = Main.templates.get(this.templateId);

        //System.out.println(this.templateId);

        modelItemLabel.setText(template.getName());
        modelItemImage.setImage(new Image(template.getImageURL()));
        modelItemLabel.setTooltip(new Tooltip(template.getDescription()));

    }




}
