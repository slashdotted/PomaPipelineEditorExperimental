package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import model.Module;
import model.ModuleTemplate;
import model.Value;
import utils.GraphicsElementsFactory;
import utils.ParamBox;

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
    private VBox cparamsBox;



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
        templateLabel.setText("New " +template.getType() + " Module");

        VBox mandatoryBox = new VBox();
        VBox optionalBox = new VBox();

        mandatoryParamsPane.setContent(mandatoryBox);
        optionalParamsPane.setContent(optionalBox);

        template.getMandatoryParameters().keySet().forEach(key -> {
            Value value= template.getMandatoryParameters().get(key);
            VBox current =  new FormBox(value);
            mandatoryBox.getChildren().add(current);
           // mandatoryBox.getChildren().add(GraphicsElementsFactory.getSeparator());
        });

        template.getOptParameters().keySet().forEach(key -> {
            Value value= template.getOptParameters().get(key);
            VBox current =  new FormBox(value);


            optionalBox.getChildren().add(current);
           // optionalBox.getChildren().add( GraphicsElementsFactory.getSeparator());
        });

        this.cparamsBox.getChildren().add(new ParamBox());


    }
}
