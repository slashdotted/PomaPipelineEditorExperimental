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
import javafx.scene.layout.Background;
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
import utils.ProgramUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by felipe on 13/04/16.
 */
public class SideBar extends VBox {

    //private static Image addImageNormal = new Image("images/plus2.png");
    //private static Image addImageShadow = new Image("images/plus2_shadow.png");
    private static ImageView pinImageView = ProgramUtils.getPinImage(20);
    private static ImageView closeImageView = ProgramUtils.getCloseImage(20);
    private static ImageView unPinImageView = ProgramUtils.getUnpinImage(20);



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

    @FXML
    private Button pinButton;


    private List<ParamBox> paramBoxes;
    private ObservableList<SimpleStringProperty> cparams;
    private List<FormBox> mandatoryFormBoxes;
    //private ArrayList<String> cparams;
    private Module module;
    private ModuleTemplate template;
    private VBox mandatoryBox;
    private VBox optionalBox;
    //private Button controlButton;
    private double expandedSize;
    private static AtomicBoolean animated = new AtomicBoolean(false);
    private AtomicBoolean pinned = new AtomicBoolean(false);
    private boolean creation;

    public SideBar(Module module, double expandedSize, boolean creation) {
        this.module = module;
        //this.controlButton = controlButton;
        this.expandedSize = expandedSize;
        animated.set(false);
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
        mandatoryFormBoxes = new ArrayList<>();
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
        closeSidebar.setGraphic(closeImageView);
        closeSidebar.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(closeSidebar);

        addCParam.setShape(new Circle(10));
        addCParam.setBackground(Background.EMPTY);
        addCParam.setGraphic(new ImageView("images/plus.png"));
        ProgramUtils.setOnPressedButton(addCParam);

        pinButton.setGraphic(pinImageView);
        pinButton.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(pinButton);
    }

    private void setEvents() {

        closeSidebar.setOnAction(event -> {
            if (animated.get()) {
                event.consume();
            } else {
                pinned.set(false);
                hide(false);
            }
        });

        pinButton.setOnAction(event1 -> {
            if(pinned.get()){
                pinned.compareAndSet(true, false);
                pinButton.setGraphic(pinImageView);
            }else{
                pinned.compareAndSet(false, true);
                pinButton.setGraphic(unPinImageView);
            }
        });


        addCParam.setOnMouseClicked(event -> {
            addNewCParam(new SimpleStringProperty(""), true);
        });

        nameLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editName();
            }
        });

        hostTextField.setOnAction(event -> {
            if (!hostTextField.getText().equals(module.getHost())) {
                Command editHost = new EditModule(module, EditModule.Type.Host, hostTextField.getText());
                editHost.execute();
                CareTaker.addMemento(editHost);
            }
        });

    }

    public void show() {
        System.out.println("Show sidebar called");
        if (animated.get())
            return;
        animated.compareAndSet(false, true);
        final Animation showPanel = showAnimation(this);

        showPanel.onFinishedProperty().set(actionEvent -> {
            if (creation)
                editName();
            animated.compareAndSet(true, false);

        });
        System.out.println("Showing");
        setVisible(true);
        showPanel.play();

    }


    public void hide(boolean force) {
        System.out.println("Hide sidebar called");
        if(force && isPinned()){
            pinned.compareAndSet(true, false);
        }
        if (animated.get() || isPinned())
            return;

        //System.out.println("Sidebar hiding->>>>>>>>>>>>>>><");

        animated.compareAndSet(false, true);
        final Animation hidePanel = hideAnimation(this);
        hidePanel.onFinishedProperty().set(actionEvent -> {
            setVisible(false);
            animated.compareAndSet(true, false);
        });

        //if (!animated.get()) {
        System.out.println("Hiding");
        //setVisible(true);
        setModuleAsValid();
        hidePanel.play();
        //}

    }

    private static Animation hideAnimation(final SideBar sideBar) {

        // Create an animated to hide the panel.
        return new Transition() {

            {
                setCycleDuration(Duration.millis(100));
            }

            @Override
            protected void interpolate(double frac) {
                final double size = sideBar.getExpandedSize() * (1.0 - frac);
                sideBar.setPrefWidth(size);
            }
        };
    }

    private static Animation showAnimation(final SideBar sideBar) {
        // Create an animated to show the panel.
        return new Transition() {
            {
                setCycleDuration(Duration.millis(100));
            }

            @Override
            protected void interpolate(double frac) {
                final double size = sideBar.getExpandedSize() * frac;
                sideBar.setPrefWidth(size);
            }
        };
    }

    private void setModuleAsValid() {

        boolean isValid = true;
        for (FormBox form : mandatoryFormBoxes) {
            isValid &= form.isValid();
            //System.out.println("form validation: " + isValid);
        }
        //System.out.println();
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
            // System.out.println("Text changed");
            Command editName = new EditModule(module, EditModule.Type.Name, temp.getText());
            if(editName.execute()){
                CareTaker.addMemento(editName);
                nameLabel.setText(module.getName());
                MainWindow.stackedLogBar.logAndSuccess("Name changed: " + module.getName());
            }


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


            FormBox current = new FormBox(value);
            mandatoryFormBoxes.add(current);
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
        this.cparamsBox.getChildren().add(box);
        if (isNew) {
            Command addParam = new AddStringProperty(cParam, cparams);
            addParam.execute();
            CareTaker.addMemento(addParam);
        }
        //this.cparams.add(cParam);
        paramBoxes.add(box);
        this.cparamsBox.setVgrow(box, Priority.ALWAYS);

        box.getDelete().setOnMouseClicked(event -> {

            Command removeParam = new RemoveStringProperty(box.paramProperty(), cparams);
            removeParam.execute();
            CareTaker.addMemento(removeParam);
            //cparams.remove(box.paramProperty());
            paramBoxes.remove(box);
            //TODO call command
            Platform.runLater(() -> cparamsBox.getChildren().remove(box));
        });
    }


    public boolean isPinned() {
        return pinned.get();
    }
}
