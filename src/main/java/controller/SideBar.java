package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import model.Module;
import model.ModuleTemplate;

import java.io.IOException;

/**
 * Created by felipe on 13/04/16.
 */
public class SideBar extends ScrollPane {

    @FXML
    private Label templateLabel;

    @FXML
    private TitledPane mandatoryParamsPane;

    @FXML
    private TitledPane optionalParamsPane;

    @FXML
    private VBox mainBox;

    private Module module;
    private ModuleTemplate template;


    public SideBar(Module module) {
        this.module = module;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sideBar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }


        this.module = module;

    }




    @FXML
    public void initialize() {
        this.template = module.getTemplate();
        System.out.println(template.getType());
        templateLabel.setText("New " +template.getType() + " Module");

    }
}
