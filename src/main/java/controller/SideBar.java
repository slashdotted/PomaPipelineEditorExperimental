package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Module;
import model.ModuleTemplate;
import model.Value;
import utils.GraphicsElementsFactory;
import utils.ParamBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    private Button addCParamButton;


    private List<ParamBox> paramBoxes;
    private ObservableList<SimpleStringProperty> cparams;
    //private ArrayList<String> cparams;
    private Module module;
    private ModuleTemplate template;
    private VBox mandatoryBox;
    private VBox optionalBox;


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
        //this.cparams = FXCollections.observableList(module.getCParams());
        this.cparams = module.getcParams();
        templateLabel.setText("New " + template.getType() + " Module");
        paramBoxes = new ArrayList<>();

        setParametersArea();
        this.cparamsBox.setMaxWidth(Double.MAX_VALUE);
        this.cparamsBox.setFillWidth(true);


        addCParamButton.setGraphic(new ImageView("images/plus.png"));
        addCParamButton.setOnAction(event -> addNewCParam(new SimpleStringProperty()));

        if (!cparams.isEmpty()) {
            for (int i = 0; i < cparams.size(); i++) {
                addNewCParam(cparams.get(i));
            }

        }
    }

    private void setParametersArea() {

        //TODO initialize present fields


        mandatoryBox = new VBox();
        optionalBox = new VBox();

        mandatoryParamsPane.setContent(mandatoryBox);
        optionalParamsPane.setContent(optionalBox);

        template.getMandatoryParameters().keySet().forEach(key -> {

            //Value value = template.getMandatoryParameters().get(key);
            Value value = module.getParameters().get(key);
            VBox current = new FormBox(value);
            mandatoryBox.getChildren().add(current);
            //Value value = module.getParameters().get(key);
             mandatoryBox.getChildren().add(GraphicsElementsFactory.getSeparator());
        });

        template.getOptParameters().keySet().forEach(key -> {
            //Value value = template.getOptParameters().get(key);
            Value value = module.getParameters().get(key);
            VBox current = new FormBox(value);
            optionalBox.getChildren().add(current);
            // optionalBox.getChildren().add( GraphicsElementsFactory.getSeparator());
        });

    }

    private void addNewCParam(SimpleStringProperty newString) {
        //SimpleStringProperty newString = new SimpleStringProperty(stringProperty);

        ParamBox box = new ParamBox(newString);
        this.cparamsBox.getChildren().add(1, box);

        paramBoxes.add(box);
        this.cparamsBox.setVgrow(box, Priority.ALWAYS);


        box.getActionButton().setOnAction(event -> {
            cparams.remove(box.paramProperty());
            paramBoxes.remove(box);

            Platform.runLater(() -> cparamsBox.getChildren().remove(box));
        });

//
//        box.getTextField().setOnAction(event -> {
//            // TODO remove this
//            //System.out.println();
//            module.getCParams().forEach(s -> System.out.println(s));
//        });
    }


}
