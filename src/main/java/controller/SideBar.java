package controller;

import commands.*;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
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
import utils.CareTaker;
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

    //private static Image addImageNormal = new Image("images/plus2.png");
    //private static Image addImageShadow = new Image("images/plus2_shadow.png");
    @FXML
    private Label templateLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField hostTextField;

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
    private List<FormBox> mandatFormBoxes;
    //private ArrayList<String> cparams;
    private Module module;
    private ModuleTemplate template;
    private VBox mandatoryBox;
    private VBox optionalBox;
    private Button controlButton;
    private double expandedSize;
    private static AtomicBoolean closing = new AtomicBoolean(false);
    private boolean creation;

    public SideBar(Module module, Button controlButton, double expandedSize, boolean creation) {
        this.module = module;
        this.controlButton = controlButton;
        this.expandedSize = expandedSize;
        closing.set(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sideBar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.creation = creation;
        try {
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.module = module;
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
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
        //setParametersArea();
        this.cparamsBox.setMaxWidth(Double.MAX_VALUE);
        this.cparamsBox.setFillWidth(true);
        hostTextField.setText(module.getHost());
        mandatFormBoxes = new ArrayList<>();
        setParametersArea();
        initializeCParams();
        setEvents();
        setButtons();


    }

    private void initializeCParams() {
        if (!cparams.isEmpty()) {
            for (int i = 0; i < cparams.size(); i++) {
                addNewCParam(cparams.get(i), false);
            }

        }
        cparamsBox.setFillWidth(true);
        cparamsBox.setMaxHeight(Double.MAX_VALUE);
        this.setVgrow(cparamsBox, Priority.ALWAYS);
        this.setVgrow(this, Priority.ALWAYS);
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
            addNewCParam(new SimpleStringProperty(""), true);
        });

        nameLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editName();
            }
        });


//        hostTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (oldValue) {
//                if (!hostTextField.getText().equals(module.getHost())) {
//                    Command editHost = new EditModule(module, EditModule.Type.Host, hostTextField.getText());
//                    editHost.execute();
//                    CareTaker.addMemento(editHost);
//                }
//            }
//        });
        hostTextField.setOnAction(event -> {
            if (!hostTextField.getText().equals(module.getHost())) {
                Command editHost = new EditModule(module, EditModule.Type.Host, hostTextField.getText());
                editHost.execute();
                CareTaker.addMemento(editHost);
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
                    setCycleDuration(Duration.millis(100));
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
                    setCycleDuration(Duration.millis(100));
                }

                @Override
                protected void interpolate(double frac) {
                    final double size = getExpandedSize() * frac;
                    setPrefWidth(size);
                }
            };

            showPanel.onFinishedProperty().set(actionEvent1 -> {
                if (creation)
                    editName();
            });

            if (showPanel.statusProperty().get() == Animation.Status.STOPPED
                    && hidePanel.statusProperty().get() == Animation.Status.STOPPED) {
                if (isVisible()) {
                    setModuleAsValid();
                    hidePanel.play();

                } else {
                    setVisible(true);
                    showPanel.play();

                }
            }
        });
    }

    private void setModuleAsValid() {

        boolean isValid = true;
        for (FormBox form : mandatFormBoxes) {
            isValid &= form.isValid();
            System.out.println("form validation: " + isValid);
        }
        System.out.println();
        module.setValid(isValid);
    }

    private void editName() {
        TextField temp = new TextField(nameLabel.getText());
        temp.selectAll();
        temp.setFont(Font.font("System", FontWeight.BOLD, 14));
        VBox infoBox = (VBox) this.lookup("#infoBox");
        //nameLabel.setVisible(false);

        infoBox.getChildren().remove(nameLabel);
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
        if (!temp.getText().equals("") && !temp.getText().equals(module.getName())) {
            System.out.println("Text changed");
            Command editName = new EditModule(module, EditModule.Type.Name, temp.getText());
            editName.execute();
            CareTaker.addMemento(editName);

            nameLabel.setText(module.getName());
        }

        infoBox.getChildren().remove(temp);
        if (!infoBox.getChildren().contains(nameLabel))
            infoBox.getChildren().add(0, nameLabel);
        //nameLabel.setVisible(true);
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


        mandatoryBox = new VBox();
        optionalBox = new VBox();

        mandatoryParamsPane.setContent(mandatoryBox);
        optionalParamsPane.setContent(optionalBox);

        template.getMandatoryParameters().keySet().forEach(key -> {

            //Value value = template.getMandatoryParameters().get(key);
            Value value = module.getParameters().get(key);


            //System.out.println("in sidebar drrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"+value);
            FormBox current = new FormBox(value);
            mandatFormBoxes.add(current);
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

    private void addNewCParam(SimpleStringProperty cParam, boolean isNew) {
        //SimpleStringProperty newString = new SimpleStringProperty(stringProperty);

        ParamBox box = new ParamBox(cParam);
        this.cparamsBox.getChildren().add(1, box);
        if (isNew) {
            Command addparam = new AddStringProperty(cParam, cparams);
            addparam.execute();
            CareTaker.addMemento(addparam);
        }
        //this.cparams.add(cParam);
        paramBoxes.add(box);
        this.cparamsBox.setVgrow(box, Priority.ALWAYS);

        box.getDelete().setOnMouseClicked(event -> {

            Command removeparam = new RemoveStringProperty(box.paramProperty(), cparams);
            removeparam.execute();
            CareTaker.addMemento(removeparam);
            //cparams.remove(box.paramProperty());
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
