package controller;

import commands.AddStringProperty;
import commands.Command;
import commands.EditModule;
import commands.RemoveStringProperty;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * SideBar Class, this component is used for edit a module from GUI
 */
public class SideBar extends VBox {

    private static ImageView pinImageView = GraphicsElementsFactory.getPinImage(20);
    private static ImageView closeImageView = GraphicsElementsFactory.getCloseImage(20);
    private static ImageView unPinImageView = GraphicsElementsFactory.getUnpinImage(20);

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

    @FXML
    private CheckBox sourceCheckBox;

    private List<ParamBox> paramBoxes;
    private ObservableList<SimpleStringProperty> cparams;
    private List<FormBox> mandatoryFormBoxes;
    private Module module;
    private ModuleTemplate template;
    private VBox mandatoryBox;
    private VBox optionalBox;

    private boolean creation;

    public SideBar(Module module, double expandedSize, boolean creation) {
        this.module = module;
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
        this.template = module.getTemplate();
        sourceCheckBox.setVisible(module.getCanBeSource());
        sourceCheckBox.setSelected(MainWindow.isSource(module.getName()));
        sourceCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            String newSource = newValue.booleanValue() ? module.getName() : null;
            Command editSource = new EditModule(module, EditModule.Type.Source, newSource);
            if (editSource.execute()) {
                CareTaker.addMemento(editSource);
                MainWindow.stackedLogBar.logAndSuccess("Source changed to " + MainWindow.getSource());
            } else {
                MainWindow.stackedLogBar.logAndWarning("Failed to set source");
            }
        });

        this.cparams = module.getcParams();
        nameLabel.setText(module.getName());
        templateLabel.setText(template.getType());
        moduleImage.setImage(new Image(module.getTemplate().getImageURL()));
        paramBoxes = new ArrayList<>();
        templateLabel.setTooltip(new Tooltip(template.getType()));

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

        closeSidebar.setGraphic(closeImageView);
        closeSidebar.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(closeSidebar);

        addCParam.setShape(new Circle(10));
        addCParam.setBackground(Background.EMPTY);
        addCParam.setGraphic(new ImageView("images/plus.png"));
        ProgramUtils.setOnPressedButton(addCParam);

        if (MainWindow.pinnedSidebar.get()) {
            pinButton.setGraphic(pinImageView);
        } else {
            pinButton.setGraphic(unPinImageView);
        }

        pinButton.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(pinButton);
    }

    private void setEvents() {

        closeSidebar.setOnAction(event -> {
            hide(true);
        });

        pinButton.setOnAction(event1 -> {
            if (MainWindow.pinnedSidebar.get()) {
                MainWindow.pinnedSidebar.set(false);
                pinButton.setGraphic(unPinImageView);
            } else {
                MainWindow.pinnedSidebar.set(true);
                pinButton.setGraphic(pinImageView);
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
        setWidth(400);
        setVisible(true);
    }

    public void hide(boolean force) {

        if (!force && isPinned()) {
            return;
        }

        setModuleAsValid();
        setWidth(0);
        setVisible(false);
    }

    private void setModuleAsValid() {

        module.validate();
    }

    private void editName() {
        TextField temp = new TextField(nameLabel.getText());
        temp.selectAll();
        temp.setFont(Font.font("System", FontWeight.BOLD, 14));
        VBox infoBox = (VBox) this.lookup("#infoBox");

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

            Command editName = new EditModule(module, EditModule.Type.Name, temp.getText());
            if (editName.execute()) {
                CareTaker.addMemento(editName);
                nameLabel.setText(module.getName());
                MainWindow.stackedLogBar.logAndSuccess("Name changed: " + module.getName());
            }

        }

        infoBox.getChildren().remove(temp);
        if (!infoBox.getChildren().contains(nameLabel)) {
            infoBox.getChildren().add(0, nameLabel);
        }

    }

    private void setParametersArea() {

        mandatoryBox = new VBox();
        optionalBox = new VBox();

        mandatoryParamsPane.setContent(mandatoryBox);
        optionalParamsPane.setContent(optionalBox);

        template.getMandatoryParameters().keySet().forEach(key -> {

            Value value = module.getParameters().get(key);

            FormBox current = new FormBox(value);

            mandatoryFormBoxes.add(current);
            mandatoryBox.getChildren().add(current);
            mandatoryBox.getChildren().add(GraphicsElementsFactory.getSeparator());
        });

        template.getOptParameters().keySet().forEach(key -> {

            Value value = module.getParameters().get(key);
            VBox current = new FormBox(value);
            optionalBox.getChildren().add(current);
            optionalBox.getChildren().add(GraphicsElementsFactory.getSeparator());
        });

    }

    private void addNewCParam(SimpleStringProperty cParam, boolean isNew) {

        ParamBox box = new ParamBox(cParam);
        this.cparamsBox.getChildren().add(box);
        if (isNew) {
            Command addParam = new AddStringProperty(cParam, cparams);
            addParam.execute();
            CareTaker.addMemento(addParam);
        }

        paramBoxes.add(box);
        this.cparamsBox.setVgrow(box, Priority.ALWAYS);

        box.getDelete().setOnMouseClicked(event -> {

            Command removeParam = new RemoveStringProperty(box.paramProperty(), cparams);
            removeParam.execute();
            CareTaker.addMemento(removeParam);

            paramBoxes.remove(box);

            Platform.runLater(() -> cparamsBox.getChildren().remove(box));
        });
    }

    public boolean isPinned() {
        return MainWindow.pinnedSidebar.get();
    }

    Module getModule() {
        return module;
    }
}
