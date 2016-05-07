package controller;

import commands.Command;
import commands.Move;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import main.Main;
import model.Module;
import model.ModuleTemplate;
import utils.CareTaker;
import utils.Converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Marco on 10/03/2016.
 */
public class DraggableModule extends Pane {

    //private UUID draggableModuleID;
    private Point2D position;
    private Point2D oldPosition;

    private Point2D mDragOffset = new Point2D(0.0, 0.0);
    private Point2D mOldDragOffset = new Point2D(0.0, 0.0);
    private Point2D mOldDragOffset2= new Point2D(0.0,0.0);

    private Module module;
    private ArrayList<LinkView> links = new ArrayList<>();
    private DraggableModule selfie;
    private boolean selected;
    private SimpleBooleanProperty valid=new SimpleBooleanProperty(true);

    @FXML
    private Pane modelPane;

    @FXML
    private Pane titleBar;

    @FXML
    private Label modelItemLabel;
    @FXML
    private Label labelHost;
    @FXML
    private Label labelTemplate;

    @FXML
    private HBox hBoxDragMod;

    @FXML
    private Pane paneItemImage;
    @FXML
    private ImageView validImage;

    @FXML
    private ImageView modelItemImage;


    //handlers to drag and drop of modules
    private EventHandler<DragEvent> mModuleHandlerDrag;
    private EventHandler<DragEvent> mModuleHandlerDrop;


    //handlers and vars to create links through drag and drop
    public static LinkView mShadowLink = new LinkView(true);
    private ScrollPane mainScrollPane = null;
    private int numberOfEntries=0;

    private EventHandler<MouseEvent> mLinkHandleDragDetected;
    private EventHandler<MouseEvent> mLinkHandleDropOut;
    private EventHandler<DragEvent> mLinkHandleDragDropped;
    private EventHandler<DragEvent> mContextLinkDragOver;
    private EventHandler<DragEvent> mContextLinkDragDropped;

    public DraggableModule(Module module) {


        //TODO set default to create and show view

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/moduleDraggable.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.module = module;
        this.selfie = this;
        //
        //    ModuleTemplate temp=Main.templates.get(module.getType());


        //System.out.println("in draggableModule");
        // System.out.println(temp.getType());

        ModuleTemplate temp = Main.templates.get(module.getType());
        this.modelItemImage.setImage(new Image(temp.getImageURL()));
        position = new Point2D(0, 0);
        selected = false;


        mainScrollPane = (ScrollPane) Main.mScene.lookup("#mainScrollPane");
        addToolTips();
        setLabels();
        validImage.setImage(new Image("/images/high_Priority.png"));

        validImage.setFitHeight(30);
        validImage.setFitWidth(30);

        if(module.isValid()){
            validImage.setVisible(false);
        }else{
            validImage.setVisible(true);
        }

        module.getValid().addListener((observable, oldValue, newValue) -> {

            if(!newValue){

                validImage.setVisible(true);
            }else{

                validImage.setVisible(false);
            }
        });
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.relocateToPoint(position);
    }

    private void setLabels() {
        labelHost.setText(module.getHost());
        labelTemplate.setText(module.getType());
        modelItemLabel.setText(module.getName());

    }

    private void addToolTips() {
        Tooltip nameTooltip = new Tooltip();
        nameTooltip.textProperty().bind(modelItemLabel.textProperty());

        Tooltip templateTooltip = new Tooltip();
        templateTooltip.textProperty().bind(labelTemplate.textProperty());

        Tooltip hostTooltip = new Tooltip();
        hostTooltip.textProperty().bind(labelHost.textProperty());

        modelItemLabel.setTooltip(nameTooltip);
        labelHost.setTooltip(hostTooltip);
        labelTemplate.setTooltip(templateTooltip);
    }

    @FXML
    public void initialize() {


        buildNodeDragHandlers();
        buildLinkDragHandlers();

        paneItemImage.setOnDragDetected(mLinkHandleDragDetected);
        paneItemImage.setOnDragDropped(mLinkHandleDragDropped);

        boolean isShadow = true;

        mShadowLink.setVisible(false);

    }

    public void updateName() {
        this.modelItemLabel.setText(module.getName());

    }

    public void updateHost() {
        this.labelHost.setText(module.getHost());
    }

    public void updateType() {
        this.labelTemplate.setText(module.getType());
    }

    public String getHost() {
        return module.getHost();
    }

    public String getType() {
        return module.getType();
    }


    private void buildLinkDragHandlers() {



        titleBar.setOnMouseClicked(event -> {

            if (event.getClickCount() == 2) {
                MainWindow.openSideBar(module, false);
            }

        });

        titleBar.setOnMouseReleased(event -> {


            controllSelectAction(event);
        });

        MainWindow.mainScrollPaneStat.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Group group = (Group) mainScrollPane.getContent();
                group.getChildren().remove(mShadowLink);
                mShadowLink.setVisible(false);

            }
        });

        mLinkHandleDragDetected = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                mainScrollPane.setOnDragOver(mContextLinkDragOver);
                mainScrollPane.setOnDragDropped(mContextLinkDragDropped);

                //Set up user-draggable link
                // System.out.println("parent: " + getParent().getParent().getClass().getName());

                Group group = (Group) mainScrollPane.getContent();
                if(!group.getChildren().contains(mShadowLink)) {
                    group.getChildren().add(0, mShadowLink);
                }
                //   right_pane.getChildren().add(0,mDragLink);

                mShadowLink.setVisible(false);
                //System.out.println(getWidth() + "***********************");
                Point2D p = new Point2D(
                        getLayoutX() + (getWidth() / 2),
                        getLayoutY() + (getHeight() / 2)
                );
                mShadowLink.setStart(p);
                //Drag content code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                //System.out.println(module.getName() + "***********");
                container.addData("fromId", module.getName());
                content.put(DragContainer.AddLink, container);

                startDragAndDrop(TransferMode.ANY).setContent(content);

                event.consume();


            }
        };

        mLinkHandleDragDropped = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                //get back the drag from the container.Controll if
                //is the drag that we need
                DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

                if (container != null) {
                    //stop using shadowlink

                    mShadowLink.setVisible(false);


                    ClipboardContent content = new ClipboardContent();
                    //information about where finish the link
                    container.addData("toId", module.getName());
                    content.put(DragContainer.AddLink, container);
                    event.getDragboard().setContent(content);
                    event.setDropCompleted(true);
                    event.consume();

                }
                Group group = (Group) mainScrollPane.getContent();
                group.getChildren().remove(mShadowLink);

            }

        };
        mContextLinkDragOver = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                System.out.println("ciaooooo");
                Group group = (Group) mainScrollPane.getContent();
                if (!group.getChildren().contains(mShadowLink)) {
                    group.getChildren().add(0,mShadowLink);

                }
                //update end position of shadowLink
                if (!mShadowLink.isVisible()) {
                    mShadowLink.setVisible(true);
                }
                Group mainGroup =MainWindow.mainGroup;

                mShadowLink.setEnd(mainGroup.sceneToLocal(event.getSceneX(), event.getSceneY()));
            }
        };
        //creation of link

        mContextLinkDragDropped = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                // System.out.println("link drag dropped");

                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                //remove shodow
                mShadowLink.setVisible(false);
                Group group = (Group) mainScrollPane.getContent();
                group.getChildren().remove(mShadowLink);

                group.getChildren().remove(mShadowLink);


                event.setDropCompleted(true);
                event.consume();
            }
        };


    }

    private void controllSelectAction(MouseEvent event) {
        Map<String, Module> selectedModules = MainWindow.selectedModules;
        if (!event.isControlDown()) {

            if (selectedModules.size()>1) {
                MainWindow.unselectAll();
                select();
            }else{
                MainWindow.unselectAll();
                if(selected){
                    unselect();
                }else{
                    select();
                }

            }
        }else{
            if(selected){
                unselect();
            }else{

                select();
            }
        }

        System.out.println(selectedModules.size());
        System.out.println(MainWindow.selectedLinks.size());
    }



    public void select() {
        selected = true;
        //if(selected) {
        MainWindow.selectedModules.put(module.getName(), module);
        //Main.modulesClipboard.put(module.getName(), Converter.moduleToJSON(module));
        this.setStyle("-fx-border-color: darkblue");
        this.setStyle("-fx-effect: dropshadow(three-pass-box, darkblue, 10,0, 0, 0) ");


        selectLinks();


    }

    private void selectLinks() {

        for (LinkView lv:links ) {
            lv.select(getName());
        }

    }

    private void unselectLinks() {
        for (LinkView lv:links ) {
            lv.unselectLinkView();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void unselect() {
        MainWindow.selectedModules.remove(module.getName());
        Main.modulesClipboard.remove(module.getName());
        selected = false;
        this.setStyle("-fx-border-color:rgba(119,85,51,255)");
        this.setStyle("-fx-effect: null");
        unselectLinks();
    }

    private void buildNodeDragHandlers() {


        mModuleHandlerDrag = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                relocateSelection(new Point2D(event.getSceneX()-position.getX(),event.getSceneY()-position.getY()));
                relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                System.out.println(position.toString());
                event.consume();
            }
        };


        //dropping of node
        mModuleHandlerDrop = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                event.setDropCompleted(true);
                setAllFinalPosition(new Point2D(event.getSceneX() - position.getX(), event.getSceneY() - position.getY()));

                event.consume();

            }

        };
        //controller of event drag


        titleBar.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (MainWindow.currentSidebar != null)
                    MainWindow.closeSidebar();

                controllSelectAction(event);
                select();
                oldPosition = new Point2D(event.getSceneX(), event.getSceneY());
                System.out.println("parent: " + getParent().getParent().getClass().getName());

                mainScrollPane.setOnDragOver(null);
                mainScrollPane.setOnDragDropped(null);

                mainScrollPane.setOnDragOver(mModuleHandlerDrag);
                mainScrollPane.setOnDragDropped(mModuleHandlerDrop);

                //set operations drag
                mDragOffset = new Point2D(event.getX(), event.getY());
                mOldDragOffset = new Point2D(event.getX(), event.getY());


                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData("type", module.getType());
                content.put(DragContainer.DragNode, container);

                startDragAndDrop(TransferMode.ANY).setContent(content);

                event.consume();
            }
        });

    }

    private void setAllFinalPosition(Point2D subtract) {
        for (String modID:MainWindow.selectedModules.keySet() ) {
            DraggableModule dm=MainWindow.allDraggableModule.get(modID);
            Point2D pos=dm.position;


            Command move = new Move(dm, dm.oldPosition, pos.add(subtract), dm.mOldDragOffset,dm.mDragOffset);
            move.execute();
            CareTaker.addMemento(move);

        }

    }

    private void relocateSelection(Point2D subtract) {

        for (String modID:MainWindow.selectedModules.keySet() ) {
            Point2D oldPos=MainWindow.allDraggableModule.get(modID).position;

            MainWindow.allDraggableModule.get(modID).relocateToPoint(oldPos.add(subtract));
        }
    }


    public void relocateToPoint(Point2D p, Point2D offset) {
        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords;

        position = p;
        Group par = (Group) Main.mScene.lookup("#mainGroup");

        localCoords = par.sceneToLocal(p);
        System.out.println(localCoords.getX()+"--"+localCoords.getY());

        this.relocate(
                (int) localCoords.getX() - offset.getX(), (int) localCoords.getY() - offset.getY()

        );
        //System.out.println("mdragoffset: " + offset.getX() + " " + offset.getY());

        for (LinkView lv : links) {

            lv.updateBottonChannels();
        }



    }

    public void relocateToPoint(Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates

        Point2D localCoords;
        Point2D oldPosition;
        oldPosition = position;
        position = p;
        localCoords = getParent().sceneToLocal(p);
        module.setPosition(p);

        // System.out.println((int) (localCoords.getX()) - mDragOffset.getX());
        // System.out.println((int) (localCoords.getY()) - mDragOffset.getY());

        this.relocate(
                (int) localCoords.getX() - mDragOffset.getX(), (int) localCoords.getY() - mDragOffset.getY()

        );
        //System.out.println("mdragoffset: " + mDragOffset.getX() + " " + mDragOffset.getY());


        //   Command move = new Move(this, oldPosition, new Point2D(localCoords.getX() - mDragOffset.getX(), localCoords.getY() - mDragOffset.getY()));
        //  move.execute();
        //   CareTaker.addMemento(move);


        //  System.out.println(links.size() + "-----------------");
        for (LinkView lv : links) {

            lv.updateBottonChannels();
        }


    }


    public Module getModule() {
        return module;
    }

    public String getName() {

        return module.getName();
    }

    public void addLink(String id) {
        if (!links.contains(MainWindow.allLinkView.get(id))) {

            links.add(MainWindow.allLinkView.get(id));
        }
    }

    public LinkView getShadow() {
        return mShadowLink;
    }

    public ArrayList<LinkView> getLinks() {
        return links;
    }


    public void removeLinkView(LinkView lvToDel) {

        links.remove(lvToDel);


    }

    public void updateModule() {
        this.module = Main.modules.get(module.getName());
    }

}