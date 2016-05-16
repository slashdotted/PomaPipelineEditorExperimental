package controller;

import commands.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import model.Link;
import model.Module;
import model.ModuleTemplate;
import utils.*;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainWindow extends BorderPane {

    public static Map<String, DraggableModule> allDraggableModule = new HashMap<>();
    public static Map<String, LinkView> allLinkView = new HashMap<>();
    public static StackedLogBar stackedLogBar;
    public static Map<String, Link> selectedLinks = new HashMap<>();
    public static Map<String, Module> selectedModules = new HashMap<>();
    public static AnchorPane dragBoard;
    public static SelectionArea selectionArea;
    private ModuleTemplate shadowModule = null;
    private ModuleItem draggableModuleItem = null;
    public static SideBar currentSidebar;
    public static ScrollPane mainScrollPaneStat;
    public static Group mainGroup;
    public static double posXAssign = 100;
    public static double posYAssign = 0;
    double originalScaleX = 0;
    double originalScaleY = 0;
    public static double hoffset = 0;
    public static double voffset = 0;


    @FXML
    private VBox vBoxMenuAndTool;

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox moduleVBox;

    @FXML
    private Button newButton;

    @FXML
    private Button openButton;

    @FXML
    private Button importButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button undoButton;

    @FXML
    private Button redoButton;

    @FXML
    private Button copyButton;

    @FXML
    private Button pasteButton;

    @FXML
    private MenuItem newMenuItem;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private MenuItem saveAsMenuItem;
    @FXML
    private MenuItem saveSelMenuItem;
    private Effect oldEffectButton;


    private EventHandler<DragEvent> mModuleItemOverRoot = null;
    private EventHandler<DragEvent> mModuleItemDropped = null;
    private EventHandler<DragEvent> mModuleItemOverMainScrollPane = null;
    private ContextualMenu contextualMenu;

    public MainWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainWindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
        mainScrollPaneStat = mainScrollPane;

        contextualMenu = new ContextualMenu();


    }

    /*
    *  AddModule one icon that will be used for the drag-drop process
    *  This is added as a child to the root splitPane so it can be visible
    *  on both sides of the split pane.
    * */
    @FXML
    public void initialize() {


        boolean isShadow = true;
        shadowModule = ModuleTemplate.getInstance();
        draggableModuleItem = new ModuleItem(shadowModule.getType(), isShadow);
        draggableModuleItem.setVisible(false);
        draggableModuleItem.setOpacity(0.65);
        getChildren().add(draggableModuleItem);

        //Sort all modules by name
        List<ModuleTemplate> templates = new ArrayList<>(Main.templates.values());

        //Populate left pane with all modules
        for (ModuleTemplate template : templates) {
            isShadow = false;
            ModuleItem item = new ModuleItem(template.getType(), isShadow);
            moduleVBox.getChildren().add(item);
            addDragDetection(item);
        }

        //Create a new group to manage all nodes in mainScrollPane
        mainGroup = new Group();

        mainGroup.setId("mainGroup");
        dragBoard = new AnchorPane();
        dragBoard.getChildren().add(mainGroup);
        mainScrollPane.setContent(dragBoard);

        setMouseAndKeys();

        mainGroup.getChildren().add(new Pane());


        buildDragHandlers();
        setButtons();
        stackedLogBar = new StackedLogBar();
        this.setBottom(stackedLogBar);
        mainScrollPane.setPannable(false);
        selectionArea = new SelectionArea(dragBoard, mainGroup, allDraggableModule);

    }

    public void initializeSelectionArea() {
        selectionArea.initialize();
    }


    private void setButtons() {
        // new button settings
        newButton.setGraphic(new ImageView("images/new.png"));
        newButton.setTooltip(new Tooltip("New Pipeline"));
        newButton.setBackground(Background.EMPTY);
        newButton.disableProperty().bind(Main.dirty.not());

        ProgramUtils.setOnPressedButton(newButton);

        // open button settings
        openButton.setGraphic(new ImageView("images/open.png"));
        openButton.setTooltip(new Tooltip("Open a Pipeline"));
        openButton.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(openButton);
        // import button settings
        importButton.setGraphic(new ImageView("images/import.png"));
        importButton.setTooltip(new Tooltip("Import items into current Pipeline"));
        importButton.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(importButton);

        // save button settings
        saveButton.setGraphic(new ImageView("images/save.png"));
        saveButton.setTooltip(new Tooltip("Save current Pipeline"));
        saveButton.setBackground(Background.EMPTY);
        saveButton.disableProperty().bind(Main.dirty.not());
        ProgramUtils.setOnPressedButton(saveButton);


        // undo button settings
        undoButton.setGraphic(new ImageView("images/undo.png"));
        undoButton.setTooltip(new Tooltip("Undo"));
        undoButton.disableProperty().bind(CareTaker.undoable.not());
        undoButton.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(undoButton);

        // redo button settings
        redoButton.setGraphic(new ImageView("images/redo.png"));
        redoButton.setTooltip(new Tooltip("Redo"));
        redoButton.disableProperty().bind(CareTaker.redoable.not());
        redoButton.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(redoButton);

        // copy button settings
        copyButton.setGraphic(new ImageView("images/copy.png"));
        copyButton.setTooltip(new Tooltip("Copy"));
        copyButton.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(copyButton);

        // paste button settings
        pasteButton.setGraphic(new ImageView("images/paste.png"));
        pasteButton.setTooltip(new Tooltip("Paste"));
        pasteButton.setBackground(Background.EMPTY);
        ProgramUtils.setOnPressedButton(pasteButton);

        newMenuItem.disableProperty().bind(Main.dirty.not());
        saveMenuItem.disableProperty().bind(Main.dirty.not());
        saveAsMenuItem.disableProperty().bind(Main.dirty.not());
        saveSelMenuItem.disableProperty().bind(Main.dirty.not());
    }


    @FXML
    private void newPipeline() {
        if (currentSidebar != null) {
            closeSidebar(true);
        }
        if (Main.dirty.getValue()) {
            if (!GraphicsElementsFactory.saveDialog("create new"))
                return;
        }
        CareTaker.removeAllElements();
    }

    @FXML
    private void openPipeline() {
        if (currentSidebar != null) {
            closeSidebar(true);
        }
        if (Main.dirty.getValue()) {

            if (!GraphicsElementsFactory.saveDialog("open another"))
                return;
        }
        CareTaker.removeAllElements();
        importPipeline();

        Main.dirty.setValue(false);
    }


    @FXML
    private void importPipeline() {
        if (currentSidebar != null) {
            closeSidebar(true);
        }
        String windowTitle = "Import pipeline";

        if (CareTaker.size() == 0) {
            windowTitle = "Open Pipeline";
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(windowTitle);
        File pipelineFile = fileChooser.showOpenDialog(Main.root.getScene().getWindow());

        if (pipelineFile != null) {
            Command importPipeline = new Import(pipelineFile);
            boolean success = importPipeline.execute();
            CareTaker.addMemento(importPipeline);

            if (success) {
                stackedLogBar.logAndSuccess("Pipeline imported");
            } else {
                stackedLogBar.logAndWarning("There was an error while importing");
            }

        }

    }


    @FXML
    public void savePipeline() {

        if (currentSidebar != null) {
            closeSidebar(false);
        }


        if (Main.modules.isEmpty())
            return;

        if (Main.modulesClipboard.isEmpty() && Main.linksClipboard.isEmpty())
            Converter.populateClipBoards(Main.modules, Main.links);

        if (!ProgramUtils.validateModules(Main.modulesClipboard.keySet())) {
            stackedLogBar.logAndWarning("Attention, save not possible! Before saving make sure all modules are filled right.");
            return;
        }

        File destination = null;
        if (PipelineManager.CURRENT_PIPELINE_PATH != null) {
            destination = new File(PipelineManager.CURRENT_PIPELINE_PATH);
            Main.dirty.setValue(false);
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Pipeline");
            fileChooser.setInitialFileName("Pipeline.json");
            destination = fileChooser.showSaveDialog(Main.mScene.getWindow());
        }

        if (destination != null) {


            Command save = new Save(destination);
            if (save.execute()) {
                CareTaker.addMemento(save);
                stackedLogBar.logAndSuccess("Pipeline saved");
                Main.dirty.setValue(false);
            } else {

                stackedLogBar.logAndWarning("Something was wrong with saving");
            }

        }
    }

    @FXML
    private void saveSelected() {
        if (selectedModules.isEmpty() && selectedLinks.isEmpty()) {
            stackedLogBar.displayWarning("No items selected!");

            return;
        }


        String previousPath = PipelineManager.CURRENT_PIPELINE_PATH;
        Converter.populateClipBoards(selectedModules, selectedLinks);


        saveAs();
        PipelineManager.CURRENT_PIPELINE_PATH = previousPath;
    }

    @FXML
    private void saveAs() {
        PipelineManager.CURRENT_PIPELINE_PATH = null;
        savePipeline();
    }

    @FXML
    private void undo() {
        if (currentSidebar != null) {
            closeSidebar(true);
        }
        CareTaker.undo();
    }

    @FXML
    private void redo() {
        if (currentSidebar != null) {
            closeSidebar(true);
        }
        CareTaker.redo();
    }

    @FXML
    private void copy() {
        Converter.populateClipBoards(selectedModules, selectedLinks);
        Command copyCommand = new Copy();
        copyCommand.execute();
    }

    @FXML
    public void paste() {
        Command pasteCommand = new Paste(null);
        pasteCommand.execute();

    }

    @FXML
    public void exitApplication() {
        if (currentSidebar != null) {
            closeSidebar(true);
        }
        if (Main.dirty.getValue()) {
            if (!GraphicsElementsFactory.saveDialog("closing"))
                return;
        }
        System.exit(0);
    }


    @FXML
    private void about() {
        Stage stage = new Stage();
        About about = new About();
        stage.setScene(new Scene(about));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    @FXML
    public void parametersEditor() {
        closeSidebar(true);
        if (selectedModules.isEmpty()) {
            stackedLogBar.logAndWarning("Please select some modules first...");
            return;
        }

        Stage stage = new Stage();
        Scene scene = new Scene(new ParametersEditor(), 600, 350);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(Main.mScene.getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

    }


    private void setMouseAndKeys() {

        originalScaleX = mainGroup.getScaleX();
        originalScaleY = mainGroup.getScaleY();

        mainScrollPane.setOnMouseClicked(event1 -> {
            contextualMenu.getContextMenu().hide();
            if (!mainGroup.contains(event1.getX(), event1.getY())) {
                if ((event1.getButton() == MouseButton.SECONDARY)) {
                    contextualMenu.setMouse(event1);
                    contextualMenu.showContextMenu(event1);
                }
            }
        });

        mainScrollPane.setOnMousePressed(event1 -> {
            if (!mainGroup.contains(mainGroup.sceneToLocal(event1.getSceneX(), event1.getSceneY()))) {
                closeSidebar(false);
                unselectAll();
            }
        });


/*
*   Calcolate offset relatives to the cuarrent viewport
* */
        ChangeListener<Object> changeListener = (observable, oldValue, newValue) -> {
            double hmin = mainScrollPane.getHmin();
            double hmax = mainScrollPane.getHmax();
            double hvalue = mainScrollPane.getHvalue();
            double contentWidth = dragBoard.getLayoutBounds().getWidth();
            double viewportWidth = mainScrollPane.getViewportBounds().getWidth();

            hoffset =
                    Math.max(0, contentWidth - viewportWidth) * (hvalue - hmin) / (hmax - hmin);

            double vmin = mainScrollPane.getVmin();
            double vmax = mainScrollPane.getVmax();
            double vvalue = mainScrollPane.getVvalue();
            double contentHeight = dragBoard.getLayoutBounds().getHeight();
            double viewportHeight = mainScrollPane.getViewportBounds().getHeight();

            voffset =
                    Math.max(0, contentHeight - viewportHeight) * (vvalue - vmin) / (vmax - vmin);

        };


        mainScrollPane.viewportBoundsProperty().addListener(changeListener);
        mainScrollPane.hvalueProperty().addListener(changeListener);
        mainScrollPane.vvalueProperty().addListener(changeListener);


        mainScrollPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {

            if (ProgramUtils.resetZoomCombination.match(event)) {
                stackedLogBar.displayMessage("Zoom reset");
            }
            if (ProgramUtils.selectAllCombination.match(event)) {
                stackedLogBar.displayMessage("All elements selected");
                selectAll();
            }
            if (ProgramUtils.saveCombination.match(event)) {
                stackedLogBar.log("Saving pipeline");
                savePipeline();
            }
            if (event.getCode() == KeyCode.DELETE) {
                deleteSelected();

            }
            if (ProgramUtils.copyCombination.match(event)) {
                stackedLogBar.displayMessage("Selected elements copied");
                copy();
            }
            if (ProgramUtils.pasteCombination.match(event)) {
                stackedLogBar.displayMessage("Selected elements copied");
                paste();
            }

            if (ProgramUtils.undoCombination.match(event)) {
                undo();
            }
            if (ProgramUtils.redoCombination.match(event)) {
                redo();
            }

        });


    }


    /*
    *   Create a new LinkView and add it to the editor
    * */
    public static void addLinkView(Link link) {

        Group group = mainGroup;
        if (!allLinkView.containsKey(link.getID())) {

            LinkView lv = new LinkView(link);
            allLinkView.put(link.getID(), lv);

            lv.bindLink(allDraggableModule.get(link.getModuleA().getName()), allDraggableModule.get(link.getModuleB().getName()));
            if (link.getChannelsAToB().size() != 0) {

                lv.bindButtonChannels("fromTo");
                lv.fromTo = true;
                lv.updateImageViews("fromTo");


            }

            if (link.getChannelsBToA().size() != 0) {

                lv.toFrom = true;
                lv.bindButtonChannels("toFrom");
                lv.updateImageViews("toFrom");

            }


            group.getChildren().add(0, lv);
        }
    }

    /*
   *   Create a new DraggableModule and add it to the editor
   * */
    public static void addDraggableModule(Module mod) {

        Point2D position = mod.getPosition();
        DraggableModule node = new DraggableModule(mod);

        //Set position to DraggableModule without a position set
        if (position == null) {

            posYAssign = calcolateBiggerPosY();

            posXAssign += 200;
            if (posXAssign > 1400 || posYAssign <= 100) {
                posXAssign = 300;
                posYAssign += 150;
            }

            position = new Point2D(posXAssign, posYAssign);

        }
        position = position.add(hoffset, voffset);
        node.setmDragOffset(new Point2D(hoffset, voffset));
        mod.setPosition(position);

        allDraggableModule.put(node.getName(), node);

        Group group = mainGroup;
        group.getChildren().add(node);
        node.relocateToPoint(new Point2D(position.getX(), position.getY()));

        posYAssign = 100;
    }

    /*
    *  Return the biggest coordinate Y where is placed a node
    * */
    private static double calcolateBiggerPosY() {
        double maxPosY = 40;
        for (Node node : mainGroup.getChildren()) {

            if (node instanceof DraggableModule) {
                Point2D posNode = (mainGroup.localToScene(node.getLayoutX(), node.getLayoutY()));
                if (posNode.getY() > maxPosY) {

                    maxPosY = posNode.getY();
                }
            }

        }
        if (maxPosY + 150 < posYAssign) return posYAssign;


        return maxPosY;
    }

    /*
    * Handlers to manage the creation of new modules from a moduleItem,
    * Drag detect of Module item to start with Drag & drop proccess
    * */

    private void addDragDetection(ModuleItem moduleItem) {

        moduleItem.setOnDragDetected(event -> {
            //set drag respective events
            splitPane.setOnDragOver(mModuleItemOverRoot);
            mainScrollPane.setOnDragOver(mModuleItemOverMainScrollPane);

            dragBoard.setOnDragDropped(mModuleItemDropped);

            //get the item clicked
            ModuleItem itemClicked = (ModuleItem) event.getSource();

            //drag operations
            //set shadowModule of draggableModuleItem

            draggableModuleItem.setParameters(itemClicked.getTemplateType());
            draggableModuleItem.relocate(new Point2D(event.getSceneX(), event.getSceneY()));


            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();

            container.addData("TemplateType", draggableModuleItem.getTemplateType());
            content.put(DragContainer.AddNode, container);

            draggableModuleItem.startDragAndDrop(TransferMode.ANY).setContent(content);
            draggableModuleItem.setVisible(true);
            draggableModuleItem.setMouseTransparent(true);
            event.consume();

        });

    }


    private void buildDragHandlers() {

        /*
        * Handler to start event to accept Drag and Drop of files to the mainWindows
        * */

        mainScrollPane.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        /*
        * Manage the file dropped in MainWindows
        * */
        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath;
                filePath = db.getFiles().get(db.getFiles().size() - 1).getAbsolutePath();

                db.clear();

                Command importCommand = new Import(new File(filePath));
                boolean imported = importCommand.execute();
                CareTaker.addMemento(importCommand);
                if (imported) {
                    stackedLogBar.logAndSuccess("Imported file: " + filePath);
                } else {
                    stackedLogBar.logAndWarning("There was an error while importing");
                }


            }
            event.setDropCompleted(success);
            event.consume();
        });


        //to manage the movement from left to right pane
        mModuleItemOverRoot = event -> {

            Point2D point = mainScrollPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            //controls if we are in the mainScrollPane's limits
            if (mainScrollPane.boundsInLocalProperty().get().contains(point)) {
                event.acceptTransferModes(TransferMode.ANY);
                draggableModuleItem.relocate(new Point2D(event.getSceneX(), event.getSceneY()));
                return;
            }

            event.consume();

        };

        mModuleItemOverMainScrollPane = event -> {

            event.acceptTransferModes(TransferMode.ANY);

            //mouse coordinates->scene coordinates ->draggableModuleItem's parent
            //but now draggableModuleItem must be in the splitPane's coordinates to work
            Point2D position = new Point2D(event.getSceneX(), event.getSceneY());

            draggableModuleItem.relocate(position);

            event.consume();

        };

        mModuleItemDropped = event -> {
            DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
            if (container != null) {
                container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();

                content.put(DragContainer.AddNode, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
            }
        };
/*
*   The final process of Drag&Drop than will create a DraggableModule, or a LinkView
* */
        this.setOnDragDone(event -> {

            mainScrollPane.removeEventHandler(DragEvent.DRAG_OVER, mModuleItemOverMainScrollPane);
            mainScrollPane.removeEventHandler(DragEvent.DRAG_DROPPED, mModuleItemDropped);
            splitPane.removeEventHandler(DragEvent.DRAG_OVER, mModuleItemOverRoot);

            draggableModuleItem.setVisible(false);

            //Create node drag operation
            DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

            if (container != null) {
                if (container.getValue("scene_coords") != null) {
                    //creo instance of model

                    Point2D mousePoint = container.getValue("scene_coords");
                    //build module
                    ModuleTemplate template = Main.templates.get(draggableModuleItem.getTemplateType());
                    Module module = Module.getInstance(template);
                    String name = template.getNameInstance();
                    name = BadElements.checkDuplicateModules(name, 0);
                    module.setName(name);
                    module.setPosition(mousePoint);

                    Command addModule = new AddModule(module);
                    addModule.execute();
                    unselectAll();
                    allDraggableModule.get(module.getName()).select();
                    CareTaker.addMemento(addModule);
                    stackedLogBar.log(module.getName() + " created");

                    openSideBar(module, true);
                }
            }
            //AddLink drag operation
            container =
                    (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

            if (container != null) {

                //bind the ends of our link to the nodes whose id's are stored in the drag container

                String fromId = container.getValue("fromId");
                String toId = container.getValue("toId");


                if ((fromId != null && toId != null) && (!fromId.equals(toId))) {

                    DraggableModule from = allDraggableModule.get(fromId);
                    DraggableModule to = allDraggableModule.get(toId);

                    if (from != null && to != null) {
                        String orientationLink = existLink(from, to);

                        //didn't exist
                        if (orientationLink == null) {
                            Link link = new Link(Main.modules.get(from.getName()), Main.modules.get(to.getName()), "default");
                            Command addLink = new AddLink(link);
                            addLink.execute();
                            CareTaker.addMemento(addLink);

                        } else {
                            //link exists can I add default channel?

                            String idLink;
                            Command addChannel;
                            SimpleStringProperty channel = new SimpleStringProperty("default");

                            switch (orientationLink) {
                                case "fromTo":

                                    idLink = fromId + "-" + toId;
                                    if (Main.links.get(idLink).getChannelsAToB().size() < 1) {
                                        addChannel = new AddChannel(channel, Main.links.get(idLink).getChannelList(orientationLink), Main.links.get(idLink), orientationLink);
                                        addChannel.execute();


                                    }
                                    break;
                                case "toFrom":

                                    idLink = toId + "-" + fromId;
                                    if (Main.links.get(idLink).getChannelsBToA().size() < 1) {
                                        LinkView linkV = allLinkView.get(idLink);

                                        addChannel = new AddChannel(channel, Main.links.get(idLink).getChannelList(orientationLink), Main.links.get(idLink), orientationLink);
                                        addChannel.execute();
                                        CareTaker.addMemento(addChannel);

                                    }

                                    break;
                            }


                        }
                    }
                }
            }

        });

    }

/*
*   SideBar Manager
* */
    public static void openSideBar(Module module, boolean creation) {
        //Service
        allDraggableModule.get(module.getName()).select();
        Task<SideBar> builder = buildSideBar(module, creation);
        builder.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            currentSidebar = builder.getValue();
            Main.root.setRight(currentSidebar);
            currentSidebar.show();
        });
        new Thread(builder).start();

        allDraggableModule.get(module.getName()).select();
    }

    private static Task<SideBar> buildSideBar(Module module, boolean creation) {
        return new Task<SideBar>() {
            @Override
            protected SideBar call() throws Exception {
                if (currentSidebar != null)
                    closeSidebar(true);
                currentSidebar = new SideBar(module, 400, creation);
                return currentSidebar;
            }
        };
    }


    public static void closeSidebar(boolean force) {
        if (currentSidebar == null)
            return;
        if (!currentSidebar.isPinned() || force) {
            currentSidebar.hide(force);
            currentSidebar = null;
        }


    }

    /*
    * Return the orientation of an existent Link
    * */
    private String existLink(DraggableModule from, DraggableModule to) {

        String idLink = from.getName() + "-" + to.getName();
        String idLink2 = to.getName() + "-" + from.getName();

        if (Main.links.containsKey(idLink)) {
            return "fromTo";
        }
        if (Main.links.containsKey(idLink2)) {
            return "toFrom";
        }
        return null;
    }

 /*
 * Remove a linkView
 * */

    public static void removeLinkView(LinkView lv) {
        DraggableModule dmFrom = lv.getFrom();
        DraggableModule dmTo = lv.getTo();
        dmFrom.removeLinkView(lv);
        dmTo.removeLinkView(lv);
        allLinkView.remove(lv.getName());
        Group group = mainGroup;
        group.getChildren().remove(lv);

    }

    /*
    * Create commands to remove a Draggablemodule, that keep up the consistence in the pipeline
    * */
    public static Command removeDraggableModule(DraggableModule dm) {
        dm.unselect();
        ArrayList<Command> allCommands = new ArrayList<>();
        for (LinkView lv : dm.getLinks()) {
            Command removeLV = new RemoveLink(lv.getLink());
            allCommands.add(removeLV);
        }
        Command execAll = new ExecuteAll(allCommands);
        allDraggableModule.remove(dm.getName());
        Group group = mainGroup;
        group.getChildren().remove(dm);
        return execAll;
    }

    /*
    * Calls to the update channels of a relative linkView
    * */
    public static void updateLinkView(LinkView lv, String orientation) {
        lv.updateImageViews(orientation);
        lv.bindButtonChannels(orientation);
    }

/*
* Select all nodes in the mainWindows
* */
    public void selectAll() {
        unselectAll();
        allDraggableModule.keySet().forEach(key -> {
            allDraggableModule.get(key).select();
        });
    }

/*
*   Unselect all nodes in the mainWindows
* */
    public static void unselectAll() {
        Set<String> selected = new HashSet<>(selectedModules.keySet());
        selected.forEach(key -> {
            allDraggableModule.get(key).unselect();
        });
        Main.modulesClipboard.clear();
        selectedModules.clear();

        selected = new HashSet<>(selectedLinks.keySet());
        selected.forEach(key -> {
            allLinkView.get(key).unselect();
        });
        Main.linksClipboard.clear();
        selectedLinks.clear();

    }


    @FXML
    private void deleteSelected() {


        Set<String> selected = null;
        ArrayList<Command> allRemoved = new ArrayList<>();
        closeSidebar(true);

        if (!selectedModules.isEmpty()) {
            selected = new HashSet<>(selectedModules.keySet());

            for (String key : selected) {
                allDraggableModule.get(key).unselect();
                Command removeModule = new RemoveModule(Main.modules.get(key));
                allRemoved.add(removeModule);

            }

        }


        if (!selectedLinks.isEmpty()) {
            selected = new HashSet<>(selectedLinks.keySet());

            for (String key : selected) {
                allLinkView.get(key).unselect();
                Command removeLink = new RemoveLink(Main.links.get(key));
                allRemoved.add(removeLink);

            }

        }


        if (!allRemoved.isEmpty()) {
            Command remModAll = new ExecuteAll(allRemoved);
            remModAll.execute();
            CareTaker.addMemento(remModAll);
        }

        Main.modulesClipboard.clear();
        selectedModules.clear();
        Main.linksClipboard.clear();
        selectedLinks.clear();

        stackedLogBar.logAndWarning("Deleted " + allRemoved.size() + " elements");

    }


}