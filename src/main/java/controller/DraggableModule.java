package controller;

import commands.Command;
import commands.ExecuteAll;
import commands.Move;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import main.Main;
import model.Module;
import model.ModuleTemplate;
import utils.CareTaker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Marco on 10/03/2016.
 */
public class DraggableModule extends Pane {


    private Point2D position;
    private Point2D oldPosition;

    private Point2D mDragOffset = new Point2D(0.0, 0.0);
    private Point2D mOldDragOffset = new Point2D(0.0, 0.0);

    private Module module;
    private ArrayList<LinkView> links = new ArrayList<>();
    private DraggableModule selfie;
    private boolean selected;


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


    private EventHandler<MouseEvent> mLinkHandleDragDetected;
    private EventHandler<DragEvent> mLinkHandleDragDropped;
    private EventHandler<DragEvent> mContextLinkDragOver;
    private EventHandler<DragEvent> mContextLinkDragDropped;


    public DraggableModule(Module module) {


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


        //Monitoring if the module has all the mandatory parameters
        if (module.isValid()) {
            validImage.setVisible(false);
        } else {
            validImage.setVisible(true);
        }

        module.getValid().addListener((observable, oldValue, newValue) -> {

            if (!newValue) {

                validImage.setVisible(true);
            } else {

                validImage.setVisible(false);
            }
        });
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
    /*
    * Updaters
    * */

    public void updateName() {
        this.modelItemLabel.setText(module.getName());

    }

    public void updateHost() {
        this.labelHost.setText(module.getHost());
    }

    public void updateType() {
        this.labelTemplate.setText(module.getType());
    }



    /*
    * Handlers than manages the link view creation
    * */

    private void buildLinkDragHandlers() {


        MainWindow.mainScrollPaneStat.setOnDragExited(event -> {
            Group group = MainWindow.mainGroup;
            group.getChildren().remove(mShadowLink);
            mShadowLink.setVisible(false);

        });

        mLinkHandleDragDetected = event -> {

            mainScrollPane.setOnDragOver(null);
            mainScrollPane.setOnDragDropped(null);

            mainScrollPane.setOnDragOver(mContextLinkDragOver);
            mainScrollPane.setOnDragDropped(mContextLinkDragDropped);

            //Set up user-draggable link
            Group group = MainWindow.mainGroup;

            if (!group.getChildren().contains(mShadowLink)) {
                group.getChildren().add(0, mShadowLink);
            }


            mShadowLink.setVisible(false);

            Point2D p = new Point2D(
                    getLayoutX() + (getWidth() / 2),
                    getLayoutY() + (getHeight() / 2)
            );
            mShadowLink.setStart(p);
            //Drag content code
            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();

            container.addData("fromId", module.getName());
            content.put(DragContainer.AddLink, container);

            startDragAndDrop(TransferMode.ANY).setContent(content);

            event.consume();


        };

        mLinkHandleDragDropped = event -> {
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

            Group group = MainWindow.mainGroup;

            group.getChildren().remove(mShadowLink);

        };
        mContextLinkDragOver = event -> {
            event.acceptTransferModes(TransferMode.ANY);
            Group group = MainWindow.mainGroup;

            if (!group.getChildren().contains(mShadowLink)) {
                group.getChildren().add(0, mShadowLink);

            }
            //update end position of shadowLink
            if (!mShadowLink.isVisible()) {
                mShadowLink.setVisible(true);
            }
            Group mainGroup = MainWindow.mainGroup;

            mShadowLink.setEnd(mainGroup.sceneToLocal(event.getSceneX(), event.getSceneY()));
        };
        //creation of link

        mContextLinkDragDropped = event -> {

            mainScrollPane.setOnDragOver(null);
            mainScrollPane.setOnDragDropped(null);

            //remove shodow
            mShadowLink.setVisible(false);

            Group group = MainWindow.mainGroup;

            group.getChildren().remove(mShadowLink);

            event.setDropCompleted(true);
            event.consume();
        };


    }

    /*
    * Controls all the selections actions, and the results of them
    * */
    private void controlSelectAction(MouseEvent event) {
        Map<String, Module> selectedModules = MainWindow.selectedModules;

        if (!event.isControlDown()) {

            if (selectedModules.size() > 1) {
                if (event.getEventType() == MouseEvent.MOUSE_RELEASED || !isSelected())
                    MainWindow.unselectAll();

                select();
            } else {
                MainWindow.unselectAll();
                if (selected) {
                    unselect();
                } else {
                    select();
                }
            }
        } else if (event.getEventType() != MouseEvent.MOUSE_RELEASED) {
            if (selected) {
                unselect();
            } else {

                select();
            }
        }
    }


    public void select() {
        selected = true;
        //if(selected) {
        MainWindow.selectedModules.put(module.getName(), module);
        this.toFront();

        //  dragging=true;
        //Main.modulesClipboard.put(module.getName(), Converter.moduleToJSON(module));
        this.setStyle("-fx-border-color: darkblue");
        this.setStyle("-fx-effect: dropshadow(three-pass-box, darkblue, 10,0, 0, 0) ");


        selectLinks();


    }

    private void selectLinks() {

        for (LinkView lv : links) lv.select(getName());

    }

    private void unselectLinks() {
        links.forEach(LinkView::unselect);
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

    /*
    * Event Handlers to control de selection of nodes, and drag and Drop
    * */
    private void buildNodeDragHandlers() {

        titleBar.setOnMousePressed(event -> {

            if (event.getClickCount() == 2 && MainWindow.selectedModules.size() <= 1) {

                MainWindow.openSideBar(module, false);
            }


            controlSelectAction(event);

        });

        titleBar.setOnMouseReleased(this::controlSelectAction);


        mModuleHandlerDrag = event -> {
            event.acceptTransferModes(TransferMode.ANY);
            relocateSelection(new Point2D(event.getSceneX() - position.getX() + MainWindow.hoffset, event.getSceneY() - position.getY() + MainWindow.voffset));
            event.consume();
        };


        //dropping of node
        mModuleHandlerDrop = event -> {

            mainScrollPane.setOnDragOver(null);
            mainScrollPane.setOnDragDropped(null);
            setAllFinalPosition(new Point2D(event.getSceneX() - position.getX() + MainWindow.hoffset, event.getSceneY() - position.getY() + MainWindow.voffset));
            event.setDropCompleted(true);
            event.consume();

        };

        //controller of event drag


        titleBar.setOnDragDetected(event -> {
            if (MainWindow.currentSidebar != null)
                MainWindow.closeSidebar(false);

            if (MainWindow.selectedModules.size() == 1) {
                controlSelectAction(event);
            }


            for (String modID : MainWindow.selectedModules.keySet()) {
                DraggableModule dm = MainWindow.allDraggableModule.get(modID);
                dm.setOldPosition(dm.getPosition());

            }

            mainScrollPane.setOnDragOver(null);
            mainScrollPane.setOnDragDropped(null);

            mainScrollPane.setOnDragOver(mModuleHandlerDrag);
            mainScrollPane.setOnDragDropped(mModuleHandlerDrop);


            double x = event.getX() - selfie.sceneToLocal(position).getX();
            double y = event.getY() - selfie.sceneToLocal(position).getY();

            mDragOffset = new Point2D(event.getX() - x, event.getY() - y);
            mOldDragOffset = new Point2D(event.getX() - x, event.getY() - y);

            for (String modID : MainWindow.selectedModules.keySet()) {
                DraggableModule dm = MainWindow.allDraggableModule.get(modID);
                dm.mDragOffset = new Point2D(mDragOffset.getX(), mDragOffset.getY());
                dm.mOldDragOffset = mDragOffset;
            }


            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();

            container.addData("type", module.getType());
            content.put(DragContainer.DragNode, container);

            startDragAndDrop(TransferMode.ANY).setContent(content);

            event.consume();
        });

    }


    /*
    * relocates the selected object to a point that has been converted to
    * scene coordinates
    * */
    private void relocateSelection(Point2D subtract) {

        for (String modID : MainWindow.selectedModules.keySet()) {
            DraggableModule current = MainWindow.allDraggableModule.get(modID);
            Point2D oldPos = current.position;
            current.relocateToPoint(oldPos.add(subtract));

        }


    }

    /*
    * relocates the object to a point that has been converted to
    * scene coordinates
    * */
    public void relocateToPoint(Point2D p, Point2D offset) {

        Point2D localCoords;
        position = p;
        Group par = (Group) Main.mScene.lookup("#mainGroup");

        localCoords = par.sceneToLocal(p);

        this.relocate(
                (int) localCoords.getX() - offset.getX(), (int) localCoords.getY() - offset.getY()

        );

        links.forEach(LinkView::updateBottonChannels);


    }
    /*
    * relocates the object to a point that has been converted to
    * scene coordinates
    * */

    public void relocateToPoint(Point2D p) {


        Point2D localCoords;

        position = p;
        localCoords = getParent().sceneToLocal(p);
        module.setPosition(p);

        this.relocate(
                (int) localCoords.getX() - mDragOffset.getX(), (int) localCoords.getY() - mDragOffset.getY()

        );

        links.forEach(LinkView::updateBottonChannels);

    }


    public void addLink(String id) {
        if (!links.contains(MainWindow.allLinkView.get(id))) {

            links.add(MainWindow.allLinkView.get(id));
        }
    }


    public void removeLinkView(LinkView lvToDel) {

        links.remove(lvToDel);


    }

    public void updateModule() {
        this.module = Main.modules.get(module.getName());
    }

    /*
    * Getters and Setters
    * */
    public void setmDragOffset(Point2D mDragOffset) {
        this.mDragOffset = mDragOffset;
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

    public LinkView getShadow() {
        return mShadowLink;
    }

    public Module getModule() {
        return module;
    }

    public String getName() {

        return module.getName();
    }

    public ArrayList<LinkView> getLinks() {
        return links;
    }

    /*
    * In drag and drop operation update all positions of selected item
    *
    * */
    private void setAllFinalPosition(Point2D subtract) {
        ArrayList<Command> allCommands = new ArrayList<>();
        for (String modID : MainWindow.selectedModules.keySet()) {
            DraggableModule dm = MainWindow.allDraggableModule.get(modID);
            Point2D pos = dm.position;


            Command move = new Move(dm.getName(), dm.oldPosition, pos.add(subtract), dm.mOldDragOffset, dm.mDragOffset);
            allCommands.add(move);


        }
        Command executeAll = new ExecuteAll(allCommands);
        executeAll.execute();
        CareTaker.addMemento(executeAll);
    }

    private void setOldPosition(Point2D oldPosition) {
        this.oldPosition = oldPosition;
    }

    public String getType() {
        return module.getType();
    }


}