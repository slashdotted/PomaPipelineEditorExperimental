package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import main.Main;
import model.ModuleTemplate;

import java.io.IOException;

public class MainWindow extends BorderPane {

    private ModuleTemplate shadowModule = null;
    private ModuleItem draggableModuleItem = null;


    @FXML
    private VBox vBoxMenuAndTool;

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox moduleVBox;

    @FXML
    private Button redo;

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
    }


    @FXML
    private void initialize() {

        //Add one icon that will be used for the drag-drop process
        //This is added as a child to the root anchorpane so it can be visible
        //on both sides of the split pane.
        boolean isShadow = true;
        shadowModule = ModuleTemplate.getInstance();
        /// shadowModule.setName("");

        draggableModuleItem = new ModuleItem(shadowModule.getType(), isShadow);
        draggableModuleItem.setVisible(false);
        draggableModuleItem.setOpacity(0.65);
        getChildren().add(draggableModuleItem);

//        //Sort all modules by name
//        List<ModuleTemplate> templates = new ArrayList<>(Main.templates.values());
//
//
//        //Populate left pane with all modules
//        for (ModuleTemplate template : templates) {
//            //System.out.println(template.getType());
//            isShadow = false;
//            ModuleItem item = new ModuleItem(template.getType(), isShadow);
//            moduleVBox.getChildren().add(item);
//            addDragDetection(item);
//            //System.out.println("ModuleItem created with drag detection associated");
//        }


        Main.templates.values().forEach(template -> {
            ModuleItem item = new ModuleItem(template.getType(), false);
            moduleVBox.getChildren().add(item);
            addDragDetection(item);
        });



        //Create a new group to manage all nodes in mainScrollPane
        Group group = new Group();

        // mainScrollPane.setContent(group);
        mainScrollPane.setContent(group);

        //TODO create true size
        group.getChildren().add(new Pane());

       // mainScrollPane.setFitToHeight(true);
       // mainScrollPane.setFitToWidth(true);
        buildDragHandlers();




    }


    public void addCircle() {

        Group group = (Group) mainScrollPane.getContent();
        LinkView prova = new LinkView();
        prova.setStart(new Point2D(0, 0));
        prova.setEnd(new Point2D(500, 500));
        Line line = new Line();
        line.setStartX(0);
        line.setStartY(0);
        line.setEndY(500);
        line.setEndX(500);
        group.getChildren().add(line);
/*
        Rectangle rect = new Rectangle(200, 200, Color.RED);
        //  mainScrollPane.setContent(rect);

        Group group = new Group();
        group.getChildren().add(rect);
        group.getChildren().add(new Circle(400, 400, 100));
        mainScrollPane.setContent(group);*/


    }

    private void addDragDetection(ModuleItem moduleItem) {

        moduleItem.setOnDragDetected(event -> {
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

        });

    }

    private void buildDragHandlers() {


        //to manage the movement from left to right pane
        mModuleItemOverRoot = event -> {

            Point2D point = mainScrollPane.sceneToLocal(event.getSceneX(), event.getSceneY());


            //controls if we are in the mainScrollPane's limits
            if (!mainScrollPane.boundsInLocalProperty().get().contains(point)) {
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
            draggableModuleItem.relocate(new Point2D(event.getSceneX(), event.getSceneY()));
            event.consume();
        };
        mModuleItemDropped = event -> {
            DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
            System.out.println(event.getSceneX() + "--" + event.getSceneY());
            container.addData("scene_coords", new Point2D(event.getSceneX(), event.getSceneY()));

            ClipboardContent content = new ClipboardContent();

            content.put(DragContainer.AddNode, container);

            event.getDragboard().setContent(content);
            event.setDropCompleted(true);
        };
        this.setOnDragDone(event -> {

            mainScrollPane.removeEventHandler(DragEvent.DRAG_OVER, mModuleItemOverMainScrollPane);
            mainScrollPane.removeEventHandler(DragEvent.DRAG_DROPPED, mModuleItemDropped);
            splitPane.removeEventHandler(DragEvent.DRAG_OVER, mModuleItemOverRoot);

            draggableModuleItem.setVisible(false);

            //Create node drag operation
            DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

            if (container != null) {
                if (container.getValue("scene_coords") != null) {
                    //creating instance of model
                    System.out.println("before creating draggableModule");
                    System.out.println(draggableModuleItem.getTemplateType());
                    System.out.println(Main.templates.get(draggableModuleItem.getTemplateType()));
                    System.out.println("create a new draggable");
                    DraggableModule node = new DraggableModule(draggableModuleItem.getTemplateType());


                    Group group = (Group) mainScrollPane.getContent();


                    System.out.println(mainScrollPane.getWidth() + "-W-H" + mainScrollPane.getHeight());
                    group.getChildren().add(node);
                    Point2D mousePoint = container.getValue("scene_coords");
                    System.out.println(mousePoint.getX() + "--" + mousePoint.getY());


                    node.relocateToPoint(new Point2D(mousePoint.getX() - 50, mousePoint.getY() - 50));


                }
            }

        });

    }

}
