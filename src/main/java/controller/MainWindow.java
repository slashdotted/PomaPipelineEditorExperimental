package controller;

import commands.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
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

    private ModuleTemplate shadowModule = null;
    private ModuleItem draggableModuleItem = null;
    public static SideBar currentSidebar; //TODO wire the visible sidebar to this static field
    public static ScrollPane mainScrollPaneStat;
    public static Group mainGroup;
    public static double posXAssign = 300;
    public static double posYAssign = 100;
    double originalScaleX = 0;
    double originalScaleY = 0;

    public static Button toggleSidebar = new Button();
    //SELECTION
    private Rectangle rect = new Rectangle(0, 0, 0, 0);

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


    @FXML
    private void initialize() {

        //AddModule one icon that will be used for the drag-drop process
        //This is added as a child to the root anchorpane so it can be visible
        //on both sides of the split pane.
        boolean isShadow = true;
        shadowModule = ModuleTemplate.getInstance();
        /// shadowModule.setName("");

        draggableModuleItem = new ModuleItem(shadowModule.getType(), isShadow);
        draggableModuleItem.setVisible(false);
        draggableModuleItem.setOpacity(0.65);
        getChildren().add(draggableModuleItem);

        //Sort all modules by name
        List<ModuleTemplate> templates = new ArrayList<>(Main.templates.values());

        //Populate left pane with all modules
        for (ModuleTemplate template : templates) {
            // System.out.println(template.getType());
            isShadow = false;
            ModuleItem item = new ModuleItem(template.getType(), isShadow);
            moduleVBox.getChildren().add(item);
            addDragDetection(item);
        }
        //Create a new group to manage all nodes in mainScrollPane
        mainGroup = new Group();
        mainGroup.setId("mainGroup");

        mainScrollPane.setContent(mainGroup);

        setMouseAndKeys();


        //TODO create true size
        mainGroup.getChildren().add(new Pane());


        buildDragHandlers();
        setButtons();
        stackedLogBar = new StackedLogBar();
        this.setBottom(stackedLogBar);
    }


    /**
     *
     */
    private void setButtons() {
        // new button settings
        newButton.setGraphic(new ImageView("images/new.png"));
        newButton.setTooltip(new Tooltip("New Pipeline"));
        newButton.setBackground(Background.EMPTY);
        newButton.disableProperty().bind(Main.dirty.not());

        // open button settings
        openButton.setGraphic(new ImageView("images/open.png"));
        openButton.setTooltip(new Tooltip("Open a Pipeline"));
        openButton.setBackground(Background.EMPTY);

        // import button settings
        importButton.setGraphic(new ImageView("images/import.png"));
        importButton.setTooltip(new Tooltip("Import items into current Pipeline"));
        importButton.setBackground(Background.EMPTY);

        // save button settings
        saveButton.setGraphic(new ImageView("images/save.png"));
        saveButton.setTooltip(new Tooltip("Save current Pipeline"));
        saveButton.setBackground(Background.EMPTY);
        saveButton.disableProperty().bind(Main.dirty.not());


        // undo button settings
        undoButton.setGraphic(new ImageView("images/undo.png"));
        undoButton.setTooltip(new Tooltip("Undo"));
        undoButton.disableProperty().bind(CareTaker.undoable.not());
        undoButton.setBackground(Background.EMPTY);

        // redo button settings
        redoButton.setGraphic(new ImageView("images/redo.png"));
        redoButton.setTooltip(new Tooltip("Redo"));
        redoButton.disableProperty().bind(CareTaker.redoable.not());
        redoButton.setBackground(Background.EMPTY);

        // copy button settings
        copyButton.setGraphic(new ImageView("images/copy.png"));
        copyButton.setTooltip(new Tooltip("Copy"));
        copyButton.setBackground(Background.EMPTY);

        // paste button settings
        pasteButton.setGraphic(new ImageView("images/paste.png"));
        pasteButton.setTooltip(new Tooltip("Paste"));
        pasteButton.setBackground(Background.EMPTY);

        newMenuItem.disableProperty().bind(Main.dirty.not());
        saveMenuItem.disableProperty().bind(Main.dirty.not());
        saveAsMenuItem.disableProperty().bind(Main.dirty.not());
        saveSelMenuItem.disableProperty().bind(Main.dirty.not());
    }


    @FXML
    private void newPipeline() {
        if (currentSidebar != null) {
            closeSidebar();
        }
        resetZoom();
        if (Main.dirty.getValue()) {
            if (!GraphicsElementsFactory.saveDialog("create new"))
                return;
        }
        CareTaker.removeAllElements();
    }

    @FXML
    private void openPipeline() {
        if (currentSidebar != null) {
            closeSidebar();
        }
        resetZoom();
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
            closeSidebar();
        }
        resetZoom();
        String windowTitle = "Import pipeline";

        if (CareTaker.size() == 0) {
            windowTitle = "Open Pipeline";
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(windowTitle);
        // fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)"));
        File pipelineFile = fileChooser.showOpenDialog(Main.root.getScene().getWindow());

        if (pipelineFile != null) {
            Command importPipeline = new Import(pipelineFile);
            boolean success = importPipeline.execute();
            CareTaker.addMemento(importPipeline);

            if(success){
                stackedLogBar.logAndSuccess("Pipeline imported");
            }else{
                stackedLogBar.logAndWarning("There was an error while importing");
            }

//            Main.modules.keySet().forEach(s -> System.out.println(Main.modules.get(s)));
//            Main.links.keySet().forEach(s -> System.out.println(Main.links.get(s)));
//            MainWindow.allDraggableModule.keySet().forEach(s -> System.out.println(MainWindow.allDraggableModule.get(s)));
//            MainWindow.allLinkView.keySet().forEach(s -> System.out.println(MainWindow.allLinkView.get(s)));
        }
        posXAssign = 300;
        posYAssign = 100;
    }


    @FXML
    public void savePipeline() {

        if (currentSidebar != null) {
            closeSidebar();
        }


        if (Main.modules.isEmpty())
            return;

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
            //if (Main.modulesClipboard.isEmpty() && Main.linksClipboard.isEmpty())
            Converter.populateClipBoards(Main.modules, Main.links);
            Command save = new Save(destination);
            if (save.execute()) {
                CareTaker.addMemento(save);
                stackedLogBar.logAndSuccess("Pipeline saved");
                Main.dirty.setValue(false);
            } else {

                //TODO launch error message
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
            closeSidebar();
        }
        CareTaker.undo();
    }

    @FXML
    private void redo() {
        if (currentSidebar != null) {
            closeSidebar();
        }
        CareTaker.redo();
    }

    @FXML
    private void copy() {
        Converter.populateClipBoards(selectedModules, selectedLinks);
        Command copyCommand = new Copy();
        copyCommand.execute();
    }


    public static void paste(MouseEvent eventContextMenu) {
        //System.out.println(eventContextMenu.getX());
        Command pasteCommand = new Paste(eventContextMenu);
        pasteCommand.execute();

    }

    @FXML
    public void exitApplication() {
        if (currentSidebar != null) {
            closeSidebar();
        }
        if (Main.dirty.getValue()) {
            if (!GraphicsElementsFactory.saveDialog("closing"))
                return;
        }
        System.exit(0);
    }


    private void setMouseAndKeys() {

        originalScaleX = mainGroup.getScaleX();
        originalScaleY = mainGroup.getScaleY();

        mainScrollPane.setOnMouseClicked(event1 -> {
            contextualMenu.getContextMenu().hide();
            if ((event1.getButton() != MouseButton.SECONDARY)) {
                if (!mainGroup.contains(event1.getX(), event1.getY())) {

                    System.out.println("Unselect all from mainScrollPane");
                    unselectAll();
                }
                if (currentSidebar != null && event1.getClickCount() < 2) {
                    closeSidebar();
                }
            } else if (!mainGroup.contains(event1.getX(), event1.getY())) {
                System.out.println("mostrooooooooooo");
                contextualMenu.setMouse(event1);
                contextualMenu.showContextMenu(event1);



            }

        });

//        Group zoomGroup = new Group();
//        zoomGroup.getChildren().add(group);
        mainScrollPane.setOnScroll(event -> {
            if (event.isControlDown()) {
                double zoomFactor = 1.05;
                double deltaY = event.getDeltaY();
                if (deltaY < 0) {
                    zoomFactor = 2.0 - zoomFactor;
                }
                System.out.println(zoomFactor);
                mainGroup.setScaleX(mainGroup.getScaleX() * zoomFactor);
                mainGroup.setScaleY(mainGroup.getScaleY() * zoomFactor);
                event.consume();
            }
        });


        mainScrollPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            System.out.println("key released " + event.getCode());


            if (ProgramUtils.resetZoomCombination.match(event)) {
                stackedLogBar.displayMessage("Zoom reset");
                resetZoom();
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
               stackedLogBar.logAndWarning("Deleting selected elements...");
                deleteSelected();
            }
            if (ProgramUtils.copyCombination.match(event)) {
                stackedLogBar.displayMessage("Selected elements copied");
               // System.out.println("saving....");
                copy();
            }
//            if (ProgramUtils.pasteCombination.match(event)) {
//                System.out.println("....");
//                paste();
//            }

        });


    }

//    private ContextMenu createContextMenu() {
//        MenuItem paste=new MenuItem("Paste");
//        paste.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent e) {
//              /*  Clipboard clipboard = Clipboard.getSystemClipboard();
//                ClipboardContent content = new ClipboardContent();
//                content.putImage(pic.getImage());
//                clipboard.setContent(content);*/
//
//                System.out.println("Arrivo a paste");
//            }
//        });
//        final ContextMenu contextMenu = new ContextMenu();
//        contextMenu.getItems().add(paste);
//
//
//        MainWindow.mainScrollPaneStat.addEventHandler(MouseEvent.MOUSE_CLICKED,
//                new EventHandler<MouseEvent>() {
//                    @Override public void handle(MouseEvent e) {
//                        if (e.getButton() == MouseButton.SECONDARY)
//                            contextMenu.show(MainWindow.mainScrollPaneStat, e.getScreenX(), e.getScreenY());
//                    }
//                });
//        return contextMenu;
//    }

    public void resetZoom() {
        System.out.println("reset zoom");
        mainGroup.setScaleX(originalScaleX);
        mainGroup.setScaleY(originalScaleY);
    }


    public static void addLinkView(Link link) {
        Group group = (Group) MainWindow.mainScrollPaneStat.getContent();
        if (!allLinkView.containsKey(link.getID())) {

            LinkView lv = new LinkView(link);
            allLinkView.put(link.getID(), lv);

            lv.bindLink(allDraggableModule.get(link.getModuleA().getName()), allDraggableModule.get(link.getModuleB().getName()));
            if (link.getChannelsAToB().size() != 0) {

                lv.bindBottonChannels("fromTo");
                lv.fromTo = true;
                lv.updateImageViews("fromTo");


            }

            if (link.getChannelsBToA().size() != 0) {

                lv.toFrom = true;
                lv.bindBottonChannels("toFrom");
                lv.updateImageViews("toFrom");

            }


            group.getChildren().add(0, lv);
        }
    }

    public static void addDraggableModule(Module mod) {

        Point2D position = mod.getPosition();
        if (position == null) {

            position = new Point2D(posXAssign, posYAssign);
            //System.out.println("pos =null");
            posXAssign += 200;
            if (posXAssign > 1400) {
                posXAssign = 300;
                posYAssign += 200;
            }

        }
        DraggableModule node = new DraggableModule(mod);

        //node.addToolTips();
        allDraggableModule.put(node.getName(), node);
        Group group = (Group) MainWindow.mainScrollPaneStat.getContent();

        group.getChildren().add(node);
        mod.setPosition(new Point2D(position.getX() - 50, position.getY() - 40));
        node.relocateToPoint(new Point2D(position.getX() - 50, position.getY() - 40));
//        unselectAll();
//        node.select();
    }

//    private static double getBiggerX(double biggerY) {
//        double bigger = 300;
//        if (!MainWindow.allDraggableModule.isEmpty())
//            for (String key : MainWindow.allDraggableModule.keySet()) {
//                if (MainWindow.allDraggableModule.get(key).getPosition().getY() < biggerY) {
//                    continue;
//                }
//                double currentX = MainWindow.allDraggableModule.get(key).getPosition().getX();
//                if (currentX > bigger)
//                    bigger = currentX;
//            }
//        return bigger;
//    }
//
//    private static double getBiggerY() {
//        double bigger = 100;
//        if (!MainWindow.allDraggableModule.isEmpty())
//            for (String key : MainWindow.allDraggableModule.keySet()) {
//                double currentY = MainWindow.allDraggableModule.get(key).getPosition().getY();
//                if (currentY > bigger)
//                    bigger = currentY;
//            }
//        return bigger;
//    }


    private void addDragDetection(ModuleItem moduleItem) {

        moduleItem.setOnDragDetected(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                //set drag respective events
                splitPane.setOnDragOver(mModuleItemOverRoot);
                mainScrollPane.setOnDragOver(mModuleItemOverMainScrollPane);
                mainScrollPane.setOnDragDropped(mModuleItemDropped);

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

            }
        });

    }

    private void buildDragHandlers() {


        //to manage the movement from left to right pane
        mModuleItemOverRoot = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                Point2D point = mainScrollPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                //controls if we are in the mainScrollPane's limits
                if (mainScrollPane.boundsInLocalProperty().get().contains(point)) {
                    event.acceptTransferModes(TransferMode.ANY);
                    draggableModuleItem.relocate(new Point2D(event.getSceneX(), event.getSceneY()));
                    return;
                }

                event.consume();
            }
        };

        mModuleItemOverMainScrollPane = event -> {

            event.acceptTransferModes(TransferMode.ANY);

            //mouse coordinates->scene coordinates ->draggableModuleItem's parent
            //but now draggableModuleItem must be in the splitPane's coordinates to work
            Point2D position = new Point2D(event.getSceneX(), event.getSceneY());

            draggableModuleItem.relocate(position);

            event.consume();
        };
        mModuleItemDropped = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
                //  System.out.println(event.getSceneX() + "--" + event.getSceneY());
                container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();

                content.put(DragContainer.AddNode, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
            }
        };
        this.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

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

                        //Main.modules.put(module.getName(), module);
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

                                // System.out.println(orientationLink);
                                String idLink;
                                Command addChannel;
                                SimpleStringProperty channel = new SimpleStringProperty("default");
                                ;
                                switch (orientationLink) {
                                    case "fromTo":

                                        idLink = fromId + "-" + toId;
                                        if (Main.links.get(idLink).getChannelsAToB().size() < 1) {
                                            addChannel = new AddChannel(channel, Main.links.get(idLink).getChannelList(orientationLink), Main.links.get(idLink), orientationLink);
                                            addChannel.execute();
                                            //TODO elemento

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
            }
        });

    }


    private boolean isContained(Point2D posLocal, Node node) {
        DraggableModule dm = (DraggableModule) node;
        System.out.println(node.getLayoutX() + "nodo" + ((node.getLayoutX() + dm.getWidth())));
        System.out.println(posLocal.getX() + "mouse" + posLocal.getY());
        System.out.println();
        if ((node.getLayoutX() < posLocal.getX()) && ((node.getLayoutX() + dm.getWidth()) > posLocal.getX())) {
            if ((node.getLayoutY() < posLocal.getY()) && ((node.getLayoutY() + dm.getHeight()) > posLocal.getX())) {

                return true;
            }

        }

        return false;
    }

    public static void openSideBar(Module module, boolean creation) {
        //Service
        Task<SideBar> builder = buildSideBar(module, toggleSidebar, creation);
        builder.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            currentSidebar = builder.getValue();
            Main.root.setRight(currentSidebar);
            toggleSidebar.fire();
        });
        new Thread(builder).start();


//        if (currentSidebar != null)
//            closeSidebar();
//        currentSidebar = new SideBar(module, toggleSidebar, 400, creation);
//        Main.root.setRight(currentSidebar);
//        toggleSidebar.fire();
        allDraggableModule.get(module.getName()).select();
    }

    private static Task<SideBar> buildSideBar(Module module, Button toggleSidebar, boolean creation) {
        return new Task<SideBar>() {
            @Override
            protected SideBar call() throws Exception {
                if (currentSidebar != null)
                    closeSidebar();
                currentSidebar = new SideBar(module, toggleSidebar, 400, creation);
                //Main.root.setRight(currentSidebar);
                //toggleSidebar.fire();
                return currentSidebar;
            }
        };
    }


    public static void closeSidebar() {
        if (currentSidebar.isVisible())
            toggleSidebar.fire();
        currentSidebar = null;
    }

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

    public static void update(String name, Module newValue) {
        //TODO
    }

    public static void updateLinkView(Link oldValue, Link newValue) {

        LinkView lv = allLinkView.get(oldValue.getID());

        //System.out.println(lv.getLine().getEndX());


        lv = new LinkView(newValue);


    }

    public static void removeLinkView(LinkView lv) {
        DraggableModule dmFrom = lv.getFrom();
        DraggableModule dmTo = lv.getTo();
        dmFrom.removeLinkView(lv);
        dmTo.removeLinkView(lv);

        allLinkView.remove(lv.getName());
        Group group = (Group) mainScrollPaneStat.getContent();
        group.getChildren().remove(lv);

    }

    public static Command removeDraggableModule(DraggableModule dm) {
        dm.unselect();
        ArrayList<Command> allCommands = new ArrayList<>();
        for (LinkView lv : dm.getLinks()) {
            Command removeLV = new RemoveLink(lv.getLink());
            allCommands.add(removeLV);
        }
        Command execAll = new ExecuteAll(allCommands);
        //execAll.execute();

        //CareTaker.addMemento(execAll);

        allDraggableModule.remove(dm.getName());
        Group group = (Group) mainScrollPaneStat.getContent();
        group.getChildren().remove(dm);
        return execAll;
    }

    public static void updateLinkView(LinkView lv, String orientation) {
        lv.updateImageViews(orientation);
        lv.bindBottonChannels(orientation);
    }


    private void selectAll() {
        unselectAll();
        allDraggableModule.keySet().forEach(key -> {
            allDraggableModule.get(key).select();
        });
    }


    public static void unselectAll() {
        System.out.println("unselecting all");
        Set<String> selected = new HashSet<>(selectedModules.keySet());
        selected.forEach(key -> {
            allDraggableModule.get(key).unselect();
        });
        Main.modulesClipboard.clear();
        selectedModules.clear();

        selected = new HashSet<>(selectedLinks.keySet());
        selected.forEach(key -> {
            allLinkView.get(key).unselectLinkView();
        });
        Main.linksClipboard.clear();
        selectedLinks.clear();


        //TODO handle links selection
    }


    @FXML
    private void deleteSelected() {
        System.out.println("unselecting all");
        Set<String> selected = new HashSet<>(selectedModules.keySet());
        ArrayList<Command> allRemMod = new ArrayList<>();
        selected.forEach(key -> {
            allDraggableModule.get(key).unselect();
            Command removeModule = new RemoveModule(Main.modules.get(key));
            allRemMod.add(removeModule);

        });


        selected = new HashSet<>(selectedLinks.keySet());
        selected.forEach(key -> {
            allLinkView.get(key).unselectLinkView();
            Command removeLink = new RemoveLink(Main.links.get(key));
            allRemMod.add(removeLink);


        });

        Command remModAll = new ExecuteAll(allRemMod);

        remModAll.execute();
        CareTaker.addMemento(remModAll);

        Main.modulesClipboard.clear();
        selectedModules.clear();
        Main.linksClipboard.clear();
        selectedLinks.clear();
    }

    public static Group getMainGroup() {
        return mainGroup;
    }


}
