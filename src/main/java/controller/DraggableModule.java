package controller;

import commands.Command;
import commands.Move;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import main.Main;
import model.Link;
import model.Module;
import model.ModuleTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 10/03/2016.
 */
public class DraggableModule extends Pane {

    //private UUID draggableModuleID;
    private String type;
    private Point2D position;
    private Point2D mDragOffset = new Point2D (0.0, 0.0);
    private Module module;

    private ArrayList <LinkView> links =new ArrayList<>();

    private final DraggableModule self;
//TODO add Label to HOST and type


    private String host;


    @FXML
    private Pane modelPane;

    @FXML
    private Pane titleBar;

    @FXML
    private Label modelItemLabel;

    @FXML
    private Pane paneItemImage;

    @FXML
    private ImageView modelItemImage;

    //handlers to drag and drop of modules
    private EventHandler<DragEvent> mModuleHandlerDrag;
    private EventHandler <DragEvent> mModuleHandlerDrop;



    //handlers and vars to create links through drag and drop
    public static LinkView mShadowLink  =new LinkView(true);
    private ScrollPane mainScrollPane=null;

    private EventHandler <MouseEvent> mLinkHandleDragDetected;
    private EventHandler <MouseEvent> mLinkHandleDropOut;
    private EventHandler <DragEvent> mLinkHandleDragDropped;
    private EventHandler <DragEvent> mContextLinkDragOver;
    private EventHandler <DragEvent> mContextLinkDragDropped;

    public DraggableModule( Module module){


        //TODO set default to create and show view

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/moduleDraggable.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        self=this;
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.module=module;
        this.type=module.getType();
    //
    //    ModuleTemplate temp=Main.templates.get(module.getType());


        System.out.println("in draggableModule");
       // System.out.println(temp.getType());

        ModuleTemplate temp=Main.templates.get(this.type);
        this.host=module.getHost();
        this.modelItemLabel.setText(module.getName());
        this.modelItemImage.setImage(new Image(temp.getImageURL()));
        position=new Point2D(0,0);


        mainScrollPane= (ScrollPane) Main.mScene.lookup("#mainScrollPane");


    }
    @FXML
    public void initialize(){


        buildNodeDragHandlers();
        buildLinkDragHandlers();

        paneItemImage.setOnDragDetected(mLinkHandleDragDetected);
        paneItemImage.setOnDragDropped(mLinkHandleDragDropped);

        boolean isShadow=true;

        mShadowLink.setVisible(false);

    }
    public void updateName(){
        this.modelItemLabel.setText(module.getName());
    }
    public void updateHost(){
        this.host=module.getHost();
    }
    public void updateType(){
        this.type=module.getType();
    }
    public String getHost() {
        return host;
    }


    private void buildLinkDragHandlers() {

        mLinkHandleDragDetected= new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                mainScrollPane.setOnDragOver(mContextLinkDragOver);
                mainScrollPane.setOnDragDropped(mContextLinkDragDropped);

                //Set up user-draggable link
                System.out.println("parent: " + getParent().getParent().getClass().getName());

                Group group = (Group) mainScrollPane.getContent();
                group.getChildren().add(0,mShadowLink);
               //   right_pane.getChildren().add(0,mDragLink);

                mShadowLink.setVisible(false);
                System.out.println(getWidth()+"***********************");
                Point2D p=new Point2D(
                        getLayoutX()+(getWidth()/2),
                        getLayoutY()+(getHeight()/2)
                );
                mShadowLink.setStart(p);
                //Drag content code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer ();

                System.out.println(module.getName()+"***********");
                container.addData("fromId", module.getName());
                content.put(DragContainer.AddLink,container);

                startDragAndDrop(TransferMode.ANY).setContent(content);

                event.consume();


            }
        };

        mLinkHandleDragDropped=new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                //get back the drag from the container.Controll if
                //is the drag that we need
                DragContainer container=(DragContainer)event.getDragboard().getContent(DragContainer.AddLink);

                if (container!=null){
                    //stop using shadowlink

                    mShadowLink.setVisible(false);



                    ClipboardContent content=new ClipboardContent();
                    //information about where finish the link
                    container.addData("toId",module.getName());
                    content.put(DragContainer.AddLink,container);
                    event.getDragboard().setContent(content);
                    event.setDropCompleted(true);
                    event.consume();

                }
                Group group = (Group) mainScrollPane.getContent();
                group.getChildren().remove(mShadowLink);

            }

        };
        mContextLinkDragOver =new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);

                //update end position of shadowLink
                if(!mShadowLink.isVisible()){
                    mShadowLink.setVisible(true);
                }
                mShadowLink.setEnd(new Point2D(event.getX(),event.getY()));
            }
        };
    //creation of link

        mContextLinkDragDropped=new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                System.out.println("link drag dropped");

                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                //remove shodow
                mShadowLink.setVisible(false);
                Group group= (Group) mainScrollPane.getContent();
                group.getChildren().remove(mShadowLink);

                group.getChildren().remove(mShadowLink);


                event.setDropCompleted(true);
                event.consume();
            }
        };
    }

    private void buildNodeDragHandlers() {
        mModuleHandlerDrag =new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                relocateToPoint(new Point2D(event.getSceneX(),event.getSceneY()));

                event.consume();
            }
        };

        //dropping of node
        mModuleHandlerDrop =new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                event.setDropCompleted(true);

                event.consume();

            }
        };
        //controller of event drag
        titleBar.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                System.out.println("parent: " + getParent().getParent().getClass().getName());

                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                mainScrollPane.setOnDragOver(mModuleHandlerDrag);
                mainScrollPane.setOnDragDropped(mModuleHandlerDrop);

                //set operations drag
                mDragOffset=new Point2D(event.getX(),event.getY());

                relocateToPoint(new Point2D(event.getSceneX(),event.getSceneY()));

                ClipboardContent content =new ClipboardContent();
                DragContainer container=new DragContainer();

                container.addData("type",type);
                content.put(DragContainer.DragNode,container);

                startDragAndDrop(TransferMode.ANY).setContent(content);

                event.consume();
            }
        });

    }

    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        System.out.println("entro wui");

        Point2D localCoords;
        Point2D oldPosition;
        oldPosition=position;
        position=p;
        localCoords = getParent().sceneToLocal(p);


        System.out.println((int) (localCoords.getX()) - mDragOffset.getX());
        System.out.println( (int) (localCoords.getY()) - mDragOffset.getY());



        Command move=new Move(this,oldPosition,new Point2D(localCoords.getX() -mDragOffset.getX(),localCoords.getY() - mDragOffset.getY()));
        move.execute();
        //TODO implements adding to memento


        System.out.println(links.size()+"-----------------");
        for(LinkView lv:links){

           lv.updateBottonChannels();
        }
    }


    public Module getModule() {
        return module;
    }

    public String getName(){

        return module.getName();
    }

    public void addLink(String id) {
        if(!links.contains(MainWindow.allLinkView.get(id))) {

            links.add(MainWindow.allLinkView.get(id));
            System.out.println("ho inserito linkview************"+MainWindow.allLinkView.get(id).getName());
        }
    }

    public LinkView getShadow() {
        return mShadowLink;
    }

    public ArrayList<LinkView> getLinks() {
        return links;
    }


    public void removeLinkView(LinkView lv) {
        int i=0;
        boolean found=false;
        for (LinkView iter:links) {
            if(iter.getName().equals(lv.getName())){
                found=true;
                break;
            }
            i++;
        }
        if(found){
            links.remove(i);
        }

    }

    public void updateModule() {
        this.module=Main.modules.get(module.getName());
    }

}
