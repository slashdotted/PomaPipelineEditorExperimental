package controller;

import commands.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import main.Main;
import model.Link;
import model.Module;
import model.ModuleTemplate;
import model.Value;
import utils.CareTaker;
import utils.GraphicsElementsFactory;

import java.io.IOException;
import java.util.*;

public class MainWindow extends BorderPane {

    public static Map<String, DraggableModule> allDraggableModule = new HashMap<>();
    public static Map<String, LinkView> allLinkView = new HashMap<>();
    private ModuleTemplate shadowModule = null;
    private ModuleItem draggableModuleItem = null;
    public static SideBar currentSidebar; //TODO wire the visible sidebar to this static field
    public static ScrollPane mainScrollPaneStat;
    public static double posXAssign = 300;
    public static double posYAssign = 100;
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

    private EventHandler<DragEvent> mModuleItemOverRoot = null;
    private EventHandler<DragEvent> mModuleItemDropped = null;
    private EventHandler<DragEvent> mModuleItemOverMainScrollPane = null;

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

        //select
        rect.setStroke(Color.BLUE);
        rect.setStrokeWidth(1);
        rect.setStrokeLineCap(StrokeLineCap.ROUND);
        rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

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
        Group group = new Group();
        group.setId("mainGroup");

        mainScrollPane.setContent(group);

        //TODO create true size
        group.getChildren().add(new Pane());


        buildDragHandlers();
        setButtons();
    }

    /**
     *
     */
    private void setButtons() {
        // new button settings
        newButton.setGraphic(new ImageView("images/new.png"));
        newButton.setTooltip(new Tooltip("New Pipeline"));
        newButton.setOnAction(event -> {
            if (!Main.modules.isEmpty()) {

            }
        });

        // open button settings
        openButton.setGraphic(new ImageView("images/open.png"));
        openButton.setTooltip(new Tooltip("Open a Pipeline"));
        openButton.setOnAction(event -> {
            //TODO
        });


        // import button settings
        importButton.setGraphic(new ImageView("images/import.png"));
        importButton.setTooltip(new Tooltip("Import items into current Pipeline"));
        importButton.setOnAction(event -> {
            // It works!!
//            Module module = Main.modules.get("skipper");
//            System.out.println("Values before edit: ");
//            module.getParameters().values().forEach(value -> System.out.println(value.getValue()));
//
//            Command editValue = new EditValue(module.getParameters().get("skipinterval"), "5");
//
//            System.out.println(module.getParameters().get("skipinterval").getType());
//            System.out.println(editValue.execute());
//
//            System.out.println("Values after edit: ");
//            module.getParameters().values().forEach(value -> System.out.println(value.getValue()));
// It works!!
//            Module module = Main.modules.get("stats1");
//            System.out.println(module);
//            Command editName =  new EditModule(module,EditModule.Type.Template, "BlobTracker");
//            System.out.println(module.getType()+"sadlfkjaldfjlasjdfljadslf");
//            editName.execute();
//            System.out.println(module.getType()+"sadlfkjaldfjlasjdfljadslf");
//            System.out.println(module);
            // It works!!
//            from": "splitter",
//            "to": "car_color",
//            Link toDelete= Main.links.get("splitter-car_color");
//            Command removeLink=new RemoveLink(toDelete);
//            removeLink.execute();
            Module toDelete = Main.modules.get("splitter");
            Command removeModule = new RemoveModule(toDelete);
            removeModule.execute();
            //TODO
        });


        // save button settings
        saveButton.setGraphic(new ImageView("images/save.png"));
        saveButton.setTooltip(new Tooltip("Save current Pipeline"));
        saveButton.setOnAction(event -> {
            //TODO
        });

        // undo button settings
        undoButton.setGraphic(new ImageView("images/undo.png"));
        undoButton.setTooltip(new Tooltip("Undo"));
        undoButton.setOnAction(event -> {
            if (currentSidebar != null) {
                closeSidebar();
            }

            CareTaker.undo();

        });

        // redo button settings
        redoButton.setGraphic(new ImageView("images/redo.png"));
        redoButton.setTooltip(new Tooltip("Redo"));
        redoButton.setOnAction(event -> {
            if (currentSidebar != null) {
                closeSidebar();
            }
            CareTaker.redo();
        });

        // copy button settings
        copyButton.setGraphic(new ImageView("images/copy.png"));
        copyButton.setTooltip(new Tooltip("Copy"));
        copyButton.setOnAction(event -> {
            //TODO
        });


        // paste button settings
        pasteButton.setGraphic(new ImageView("images/paste.png"));
        pasteButton.setTooltip(new Tooltip("Paste"));
        pasteButton.setOnAction(event -> {
            //TODO
        });


    }

    public static void addLinkView(Link link) {
        Group group = (Group) MainWindow.mainScrollPaneStat.getContent();
        if (!allLinkView.containsKey(link.getID())) {

            LinkView lv = new LinkView(link);
            allLinkView.put(link.getID(), lv);


            if (link.getChannelsAToB().size() != 0) {
                lv.bindLink(allDraggableModule.get(link.getModuleA().getName()), allDraggableModule.get(link.getModuleB().getName()));

                lv.bindBottonChannels("fromTo");
                lv.fromTo = true;
                lv.updateImageViews("fromTo");


            }
            if (link.getChannelsBToA().size() != 0) {
                lv.bindLink(allDraggableModule.get(link.getModuleB().getName()), allDraggableModule.get(link.getModuleA().getName()));
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


        node.relocateToPoint(new Point2D(position.getX() - 50, position.getY() - 50));

    }


    public void addCircle() {

      /*  Group group = (Group) mainScrollPane.getContent();
        LinkView prova=new LinkView();
        prova.setStart(new Point2D(0,0));
        prova.setEnd(new Point2D(500,500));
        Line line=new Line();
        line.setStartX(0);
        line.setStartY(0);
        line.setEndY(500);
        line.setEndX(500);
        group.getChildren().add(line);*/
/*
        Rectangle rect = new Rectangle(200, 200, Color.RED);
        //  mainScrollPane.setContent(rect);

        Group group = new Group();
        group.getChildren().add(rect);
        group.getChildren().add(new Circle(400, 400, 100));
        mainScrollPane.setContent(group);*/


    }

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
                        module.setName(template.getNameInstance());
                        module.setPosition(mousePoint);

                        Command addModule = new AddModule(module);
                        addModule.execute();
                        CareTaker.addMemento(addModule);
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

    public static void openSideBar(Module module, boolean creation) {
        //Service
        Task<SideBar> builder = buildSideBar(module, toggleSidebar, creation);
        //currentSidebar =
        toggleSidebar.cancelButtonProperty().setValue(false);
        builder.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            currentSidebar = builder.getValue();
            Main.root.setRight(currentSidebar);
            toggleSidebar.fire();
        });
        new Thread(builder).start();

    }

    private static Task<SideBar> buildSideBar(Module module, Button toggleSidebar, boolean creation) {
        return new Task<SideBar>() {
            @Override
            protected SideBar call() throws Exception {
                if (currentSidebar != null)
                    closeSidebar();
                return new SideBar(module, toggleSidebar, 400, creation);
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

        allLinkView.remove(lv);
        Group group = (Group) mainScrollPaneStat.getContent();
        group.getChildren().remove(lv);

    }

    public static void removeDraggableModule(DraggableModule dm) {
        ArrayList<Command> allCommands = new ArrayList<>();
        for (LinkView lv : dm.getLinks()) {
            Command removeLV = new RemoveLink(lv.getLink());
            allCommands.add(removeLV);

        }
        for (Command comm : allCommands) {
            comm.execute();
        }
        Group group = (Group) mainScrollPaneStat.getContent();
        group.getChildren().remove(dm);
    }

    public static void updateLinkView(LinkView lv, String orientation) {
        lv.updateImageViews(orientation);
        lv.bindBottonChannels(orientation);
    }

    //TODO review
    public static boolean isModuleNear(DraggableModule dragModule, Point2D position) {

        ArrayList<LinkView> links = dragModule.getLinks();
        for (LinkView lv : links) {
            // System.out.println("Sto controllando*************************");
            DraggableModule sibbling = lv.getFrom();
            if (!sibbling.getName().equals(dragModule.getName())) {
                Point2D pos = lv.getFrom().getModule().getPosition();
                double dist = position.distance(pos);
                // System.out.println(dist);
                if (position.distance(pos) < 100) {
                    return false;
                }
            }

        }

        return true;
    }
}
