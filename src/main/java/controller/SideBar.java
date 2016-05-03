package controller;

import commands.Command;
import commands.EditModule;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import model.Module;
import model.ModuleTemplate;
import model.Value;
import utils.GraphicsElementsFactory;
import utils.ParamBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by felipe on 13/04/16.
 */
public class SideBar extends VBox {

    private static Image addImageNormal = new Image("images/plus2.png");
    private static Image addImageShadow = new Image("images/plus2_shadow.png");


    @FXML
    private Label templateLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField textField;

    @FXML
    private ImageView moduleImage;

    @FXML
    private TitledPane mandatoryParamsPane;

    @FXML
    private TitledPane optionalParamsPane;

    @FXML
    private VBox cparamsBox;

    @FXML
    private Button addCParam;

    @FXML
    private Button closeSidebar;


    private List<ParamBox> paramBoxes;
    private ObservableList<SimpleStringProperty> cparams;
    //private ArrayList<String> cparams;
    private Module module;
    private ModuleTemplate template;
    private VBox mandatoryBox;
    private VBox optionalBox;
    private Button controlButton;
    private double expandedSize;
    private static AtomicBoolean closing = new AtomicBoolean(false);


    public SideBar(Module module, Button controlButton, double expandedSize) {
        this.module = module;
        this.controlButton = controlButton;
        this.expandedSize = expandedSize;
        closing.set(false);
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

        setExpandedSize(expandedSize);
        setVisible(false);
        initPosition();
        this.template = module.getTemplate();
        //this.cparams = FXCollections.observableList(module.getCParams());
        this.cparams = module.getcParams();
        nameLabel.setText(module.getName());
        templateLabel.setText(template.getType());
        moduleImage.setImage(new Image(module.getTemplate().getImageURL()));
        paramBoxes = new ArrayList<>();
        templateLabel.setTooltip(new Tooltip(template.getType()));
        setParametersArea();
        this.cparamsBox.setMaxWidth(Double.MAX_VALUE);
        this.cparamsBox.setFillWidth(true);

        setButtons();

//        addCParam.setOnMousePressed(event -> {
//            addCParam.setGraphic(new ImageView(addImageShadow));
//        });
//
//        addCParam.setOnMouseReleased(event -> {
//            addCParam.setGraphic(new ImageView(addImageNormal));
//        });


        //addCParam.setGraphic(new ImageView("images/plus.png"));
        //addCParam.setOnAction(event -> addNewCParam(new SimpleStringProperty()));

        if (!cparams.isEmpty()) {
            for (int i = 0; i < cparams.size(); i++) {
                addNewCParam(cparams.get(i));
            }

        }
        setEvents();
        //this.setMaxHeight(Double.MAX_VALUE);
        cparamsBox.setFillWidth(true);
        cparamsBox.setMaxHeight(Double.MAX_VALUE);
        this.setVgrow(cparamsBox, Priority.ALWAYS);
        this.setVgrow(this, Priority.ALWAYS);

        //this.setFitToHeight(true);


    }

    private void setButtons() {
        //closeSidebar.setShape(new Rectangle(20,20));
        closeSidebar.setGraphic(new ImageView("images/close.png"));
        closeSidebar.setPadding(Insets.EMPTY);

        addCParam.setShape(new Circle(10));
        addCParam.setPadding(Insets.EMPTY);
        addCParam.setGraphic(new ImageView("images/plus.png"));

    }

    private void setEvents() {

        closeSidebar.setOnAction(event1 -> {
            if (closing.get()) {
                event1.consume();
            }
            closing.set(true);
            controlButton.fire();
        });

        addCParam.setOnMouseClicked(event -> {
            addNewCParam(new SimpleStringProperty(""));
        });

        nameLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editName();
            }
        });


        controlButton.setOnAction(actionEvent -> {
            if (closing.get()) {
                actionEvent.consume();
            }
            closing.set(true);


            // Create an animation to hide the panel.
            final Animation hidePanel = new Transition() {

                {
                    setCycleDuration(Duration.millis(250));
                }

                @Override
                protected void interpolate(double frac) {
                    final double size = getExpandedSize() * (1.0 - frac);
                    setPrefWidth(size);
                }
            };


            hidePanel.onFinishedProperty().set(actionEvent1 -> setVisible(false));

            // Create an animation to show the panel.
            final Animation showPanel = new Transition() {
                {
                    setCycleDuration(Duration.millis(250));
                }

                @Override
                protected void interpolate(double frac) {
                    final double size = getExpandedSize() * frac;
                    setPrefWidth(size);
                }
            };

            showPanel.onFinishedProperty().set(actionEvent1 -> editName());

            if (showPanel.statusProperty().get() == Animation.Status.STOPPED
                    && hidePanel.statusProperty().get() == Animation.Status.STOPPED) {
                if (isVisible()) {
                    hidePanel.play();


                } else {
                    setVisible(true);
                    showPanel.play();
                }
            }
        });
    }

    private void editName() {
        TextField temp = new TextField(nameLabel.getText());
        temp.selectAll();
        temp.setFont(Font.font("System", FontWeight.BOLD, 14));
        VBox infoBox = (VBox) this.lookup("#infoBox");
        nameLabel.setVisible(false);
        infoBox.getChildren().add(0, temp);
        temp.requestFocus();
        temp.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                acceptEdit(temp, infoBox);
            }
        });

        temp.setOnAction(event -> acceptEdit(temp, infoBox));

    }

    private void acceptEdit(TextField temp, VBox infoBox) {
        if (!temp.getText().equals(module.getName())) {
            System.out.println("Text changed");
            Command editName = new EditModule(module, EditModule.Type.Name, temp.getText());
            editName.execute();
            // TODO add to memento
            nameLabel.setText(module.getName());
        }

        infoBox.getChildren().remove(temp);
        nameLabel.setVisible(true);
    }


    private void setExpandedSize(double expandedSize) {
        this.expandedSize = expandedSize;
    }

    public double getExpandedSize() {
        return expandedSize;
    }

    private void initPosition() {
        setPrefWidth(0);
        setMinWidth(0);
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
            optionalBox.getChildren().add(GraphicsElementsFactory.getSeparator());
        });

    }

    private void addNewCParam(SimpleStringProperty cParam) {
        //SimpleStringProperty newString = new SimpleStringProperty(stringProperty);

        ParamBox box = new ParamBox(cParam);
        this.cparamsBox.getChildren().add(1, box);
        this.cparams.add(cParam);
        paramBoxes.add(box);
        this.cparamsBox.setVgrow(box, Priority.ALWAYS);

        box.getDelete().setOnMouseClicked(event -> {

            cparams.remove(box.paramProperty());
            paramBoxes.remove(box);
            //TODO call command
            Platform.runLater(() -> cparamsBox.getChildren().remove(box));
        });

        box.getTextField().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue)
                System.out.println(Arrays.toString(cparams.toArray()));

        });

    }


}
