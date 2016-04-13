package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import main.Main;
import model.ModuleTemplate;

import java.io.IOException;
import java.util.*;

public class MainWindow extends BorderPane {

    public static Map<String, DraggableModule> allDraggableModule= new HashMap<>();
    public static Map<String, LinkView> allLinkView= new HashMap<>();
    private ModuleTemplate shadowModule=null;
    private ModuleItem draggableModuleItem =null;


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
    private void initialize(){

         //Add one icon that will be used for the drag-drop process
        //This is added as a child to the root anchorpane so it can be visible
        //on both sides of the split pane.
        boolean isShadow=true;
        shadowModule=ModuleTemplate.getInstance();
       /// shadowModule.setName("");

        draggableModuleItem=new ModuleItem(shadowModule.getType(),isShadow);
        draggableModuleItem.setVisible(false);
        draggableModuleItem.setOpacity(0.65);
        getChildren().add(draggableModuleItem);

        //Sort all modules by name
        List<ModuleTemplate> templates = new ArrayList<>(Main.templates.values());

    //now is a treeMap
      /*  Collections.sort(templates, (o1, o2) -> {
            //System.out.println(((ModuleTemplate)o2).getName());
            return o1.getType().compareTo(o2.getType());
        });*/

       //Populate left pane with all modules
        for (ModuleTemplate template: templates) {
            System.out.println(template.getType());
            isShadow=false;
            ModuleItem item = new ModuleItem(template.getType(),isShadow);
            moduleVBox.getChildren().add(item);
            addDragDetection(item);
            System.out.println("ModuleItem created with drag detection associated");
        }
        //Create a new group to manage all nodes in mainScrollPane
        Group group=new Group();

        // mainScrollPane.setContent(group);
        mainScrollPane.setContent(group);

        //TODO create true size
        group.getChildren().add(new Pane());

        //mainScrollPane.setFitToHeight(true);
        //mainScrollPane.setFitToWidth(true);

        buildDragHandlers();



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

        moduleItem.setOnDragDetected(new EventHandler <MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                //set drag respective events
                splitPane.setOnDragOver(mModuleItemOverRoot);
                mainScrollPane.setOnDragOver(mModuleItemOverMainScrollPane);
                mainScrollPane.setOnDragDropped(mModuleItemDropped);

                //get the item clicked
                ModuleItem itemClicked =(ModuleItem) event.getSource();

                //drag operations
                    //set shadowModule of draggableModuleItem

                draggableModuleItem.setParameters(itemClicked.getTemplateType());
                draggableModuleItem.relocate(new Point2D(event.getSceneX(),event.getSceneY()));


                ClipboardContent content =new ClipboardContent();
                DragContainer container=new DragContainer();

                container.addData("TemplateType",draggableModuleItem.getTemplateType());
                content.put(DragContainer.AddNode,container);

                draggableModuleItem.startDragAndDrop(TransferMode.ANY).setContent(content);
                draggableModuleItem.setVisible(true);
                draggableModuleItem.setMouseTransparent(true);
                event.consume();

            }
        });

    }
    private void buildDragHandlers(){


        //to manage the movement from left to right pane
        mModuleItemOverRoot = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                Point2D point= mainScrollPane.sceneToLocal(event.getSceneX(),event.getSceneY());


                //controls if we are in the mainScrollPane's limits
                if(!mainScrollPane.boundsInLocalProperty().get().contains(point)){

                    event.acceptTransferModes(TransferMode.ANY);
                    draggableModuleItem.relocate(new Point2D (event.getSceneX(),event.getSceneY()));
                    return;
                }

                event.consume();
            }
        };

        mModuleItemOverMainScrollPane  =new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);

                //mouse coordinates->scene coordinates ->draggableModuleItem's parent
                //but now draggableModuleItem must be in the splitPane's coordinates to work

                draggableModuleItem.relocate(new Point2D(event.getSceneX(),event.getSceneY()));
                event.consume();
            }
        };
        mModuleItemDropped =new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                DragContainer container=(DragContainer)event.getDragboard().getContent(DragContainer.AddNode);
                System.out.println(event.getSceneX()+"--"+event.getSceneY());
                container.addData("scene_coords",new Point2D(event.getSceneX(),event.getSceneY()));

                ClipboardContent content=new ClipboardContent();

                content.put(DragContainer.AddNode,container);

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
                        System.out.println("before creating draggableModule");
                        System.out.println(draggableModuleItem.getTemplateType());
                        System.out.println(Main.templates.get(draggableModuleItem.getTemplateType()));
                        System.out.println("create a new draggable");
                        DraggableModule node = new DraggableModule(draggableModuleItem.getTemplateType());


                        Group group = (Group) mainScrollPane.getContent();
                        allDraggableModule.put(node.getName(),node);

                        group.getChildren().add(node);
                        Point2D mousePoint = container.getValue("scene_coords");
                        System.out.println(mousePoint.getX() + "--" + mousePoint.getY());


                        node.relocateToPoint(new Point2D(mousePoint.getX() - 50, mousePoint.getY() - 50));


                    }
                }
                //AddLink drag operation
                container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);


                if (container != null) {
                    System.out.println("arrivo quuiiiii ---------------------------------");
                    //bind the ends of our link to the nodes whose id's are stored in the drag container

                    String fromId = container.getValue("fromId");
                    String toId = container.getValue("toId");

                    ((Group)mainScrollPane.getContent()).getChildren().remove(DraggableModule.mShadowLink);

                    if ((fromId != null && toId != null)&&(!fromId.equals(toId))) {
                        System.out.println("tengo from e to");
                        System.out.println(container.getData());


                        DraggableModule from = allDraggableModule.get(fromId);
                        DraggableModule to = allDraggableModule.get(toId);


                        //add our link at the top of the rendering order so it's rendered first


                       /* for (Node n: group.getChildren()) {

                            if (n.getId() == null)
                                continue;

                            if (n.getId().equals(fromId))
                                from = (DraggableModule) n;

                            if (n.getId().equals(toId))
                                to = (DraggableModule) n;

                        }
*/

                        if (from != null && to != null) {
                            String orientationLink=existLink(from,to);

                            //didn't exist
                            if(orientationLink==null) {
                                LinkView linkV = new LinkView(from, to, "");
                                Group group = (Group) mainScrollPane.getContent();
                                group.getChildren().add(0, linkV);
                                allLinkView.put(linkV.getName(), linkV);
                                linkV.bindLink(from, to);
                                linkV.bindBottonChannels("fromTo");
                            }else{
                                //link exists can I add default channel?

                                System.out.println(orientationLink);
                                String idLink;
                                switch (orientationLink){
                                    case "fromTo":

                                        idLink=fromId+"-"+toId;
                                        if(!((Main.links.get(idLink).getChannelsAToB()).contains("default"))){
                                            LinkView linkV=allLinkView.get(idLink);
                                            linkV.addChannel(from,to,"default");
                                        }
                                        break;
                                    case "toFrom":

                                        idLink=toId+"-"+fromId;
                                        if(!((Main.links.get(idLink).getChannelsBToA()).contains("default"))){
                                            LinkView linkV=allLinkView.get(idLink);
                                            linkV.addChannel(from,to,"default");
                                            linkV.bindBottonChannels("toFrom");

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

    private String existLink(DraggableModule from, DraggableModule to) {

        String idLink=from.getName()+"-"+to.getName();
        String idLink2=to.getName()+"-"+from.getName();

        if(Main.links.containsKey(idLink)){
            return "fromTo";
        }
        if(Main.links.containsKey(idLink2)){
            return "toFrom";
        }
        return null;
    }

}
