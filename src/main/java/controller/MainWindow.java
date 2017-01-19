package controller;

import commands.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
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

    public static double posXAssign = 100;
    public static double posYAssign = 0;

    private SelectionArea selectionArea;
    private ModuleTemplate currentlyDraggedModuleShadow = null;
    private ModuleItem currentlyDraggedModuleInstance = null;
    private AnchorPane pipelineCanvasPane;

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
    private MenuItem saveSelMenuItem;
    @FXML
    private MenuItem alignHorizontallyMenuItem;
    @FXML
    private MenuItem alignVerticallyMenuItem;

    private EventHandler<DragEvent> mModuleItemOverRoot = null;
    private EventHandler<DragEvent> mModuleItemDropped = null;
    private EventHandler<DragEvent> mModuleItemOverMainScrollPane = null;

    private static MainWindow instance;

    private MainWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainWindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MainWindow instance() {
        if (instance == null) {
            instance = new MainWindow();
        }
        return instance;
    }

    @FXML
    public void initialize() {
        alignHorizontallyMenuItem.setDisable(true);
        alignVerticallyMenuItem.setDisable(true);

        currentlyDraggedModuleShadow = ModuleTemplate.getInstance();
        currentlyDraggedModuleInstance = new ModuleItem(currentlyDraggedModuleShadow.getType(), true);
        currentlyDraggedModuleInstance.setVisible(false);
        currentlyDraggedModuleInstance.setOpacity(0.65);
        getChildren().add(currentlyDraggedModuleInstance);

        List<ModuleTemplate> templates = new ArrayList<>(Main.templates.values());

        for (ModuleTemplate template : templates) {
            ModuleItem item = new ModuleItem(template.getType(), false);
            moduleVBox.getChildren().add(item);
            addTemplateItemDragHandlers(item);
        }

        pipelineCanvasPane = new AnchorPane();
        pipelineCanvasPane.setId("mainGroup");
        mainScrollPane.setContent(pipelineCanvasPane);

        setMouseAndKeys();

        //pipelineCanvasPane.getChildren().add(new Pane());
        buildDragHandlers();
        setButtons();

        this.setBottom(StackedLogBar.instance());
        mainScrollPane.setPannable(false);
        selectionArea = new SelectionArea(pipelineCanvasPane);
    }

    public DraggableModule getModuleByName(String name) {
        for (Node n : pipelineCanvasPane.getChildren()) {
            if (n instanceof DraggableModule) {
                DraggableModule dm = (DraggableModule) n;
                if (dm.getName().equals(name)) {
                    return dm;
                }
            }
        }
        return null;
    }

    public LinkView getLinkByName(String name) {
        for (Node n : pipelineCanvasPane.getChildren()) {
            if (n instanceof LinkView) {
                LinkView lv = (LinkView) n;
                if (lv.getId().equals(name)) {
                    return lv;
                }
            }
        }
        return null;
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
        //saveAsMenuItem.disableProperty().bind(Main.dirty.not());
        saveSelMenuItem.disableProperty().bind(Main.dirty.not());
    }

    @FXML
    private void newPipeline() {
        closeSidebar(true);
        if (Main.dirty.getValue()) {
            if (!GraphicsElementsFactory.saveDialog("create new")) {
                return;
            }
        }
        CareTaker.removeAllElements();
    }

    @FXML
    private void openPipeline() {
        closeSidebar(true);
        if (Main.dirty.getValue()) {

            if (!GraphicsElementsFactory.saveDialog("open another")) {
                return;
            }
        }
        CareTaker.removeAllElements();
        Module.setSource(null);
        importPipeline();

        Main.dirty.setValue(false);
    }

    @FXML
    private void importPipeline() {
        closeSidebar(true);
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
                StackedLogBar.instance().logAndSuccess("Pipeline imported");
            } else {
                StackedLogBar.instance().logAndWarning("There was an error while importing");
            }
        }
    }

    @FXML
    public void savePipeline() {
        closeSidebar(false);

        if (Main.modules.isEmpty()) {
            return;
        }

        if (Main.modulesClipboard.isEmpty() && Main.linksClipboard.isEmpty()) {
            Converter.populateClipBoards(Main.modules, Main.links);
        }

        /*if (!ProgramUtils.validateModules(Main.modulesClipboard.keySet())) {
            StackedLogBar.instance().logAndWarning("Attention, save not possible! Before saving make sure all modules are filled right.");
            return;
        }*/
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
                StackedLogBar.instance().logAndSuccess("Pipeline saved");
                Main.dirty.setValue(false);
            } else {

                StackedLogBar.instance().logAndWarning("Something was wrong with saving");
            }

        }
    }

    @FXML
    private void saveSelected() {
        // TODO
        /*
        if (selectedModules.isEmpty() && selectedLinks.isEmpty()) {
            StackedLogBar.instance().displayWarning("No items selected!");

            return;
        }

        String previousPath = PipelineManager.CURRENT_PIPELINE_PATH;
        Converter.populateClipBoards(selectedModules, selectedLinks);

        saveAs();
        PipelineManager.CURRENT_PIPELINE_PATH = previousPath;*/
    }

    @FXML
    private void saveAs() {
        PipelineManager.CURRENT_PIPELINE_PATH = null;
        savePipeline();
    }

    @FXML
    private void undo() {
        closeSidebar(true);
        CareTaker.undo();
    }

    @FXML
    private void redo() {
        closeSidebar(true);
        CareTaker.redo();
    }

    @FXML
    private void copy() {
        // TODO
        /*  Converter.populateClipBoards(selectedModules, selectedLinks);
        Command copyCommand = new Copy();
        copyCommand.execute();*/
    }

    @FXML
    public void paste() {
        Command pasteCommand = new Paste(null);
        pasteCommand.execute();

    }

    @FXML
    public void exitApplication() {
        closeSidebar(true);
        if (Main.dirty.getValue()) {
            if (!GraphicsElementsFactory.saveDialog("closing")) {
                return;
            }
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
        if (getSelectedModules().isEmpty()) {
            StackedLogBar.instance().logAndWarning("Please select some modules first...");
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

    @FXML
    public void alignVertically() {
        List<DraggableModule> smodules = getSelectedModules();
        if (!smodules.isEmpty()) {
            ArrayList<Command> allCommands = new ArrayList<>();
            double fy = smodules.get(0).getPosition().getY();
            for (DraggableModule dm : smodules) {
                Command move = new Move(dm.getName(), dm.getPosition(),
                        new Point2D(dm.getPosition().getX(), fy), dm.getmDragOffset(), dm.getmDragOffset());
                allCommands.add(move);
            }
            Command executeAll = new ExecuteAll(allCommands);
            executeAll.execute();
            CareTaker.addMemento(executeAll);
        } else {
            StackedLogBar.instance().logAndWarning("Please select some modules first...");
        }
    }

    @FXML
    public void alignHorizontally() {
        List<DraggableModule> smodules = getSelectedModules();
        if (!smodules.isEmpty()) {
            ArrayList<Command> allCommands = new ArrayList<>();
            double fx = smodules.get(0).getPosition().getX();
            for (DraggableModule dm : smodules) {
                Command move = new Move(dm.getName(), dm.getPosition(),
                        new Point2D(fx, dm.getPosition().getY()), dm.getmDragOffset(), dm.getmDragOffset());
                allCommands.add(move);
            }
            Command executeAll = new ExecuteAll(allCommands);
            executeAll.execute();
            CareTaker.addMemento(executeAll);
        } else {
            StackedLogBar.instance().logAndWarning("Please select some modules first...");
        }
    }

    public void updateSelection() {
        if (getSelectedModules().size() < 2) {
            alignHorizontallyMenuItem.setDisable(true);
            alignVerticallyMenuItem.setDisable(true);
        } else {
            alignHorizontallyMenuItem.setDisable(false);
            alignVerticallyMenuItem.setDisable(false);
        }
    }

    ContextMenu contextMenu = new ContextMenu();

    private void setMouseAndKeys() {

        MenuItem pasteIt = new MenuItem("Paste");
        MenuItem selectAllIt = new MenuItem("Select All");
        MenuItem importIt = new MenuItem("Import");
        contextMenu.getItems().add(pasteIt);
        contextMenu.getItems().add(selectAllIt);
        selectAllIt.setOnAction(event -> Main.root.selectAll());

        pasteIt.setOnAction(e -> {
            Main.root.paste();
        });

        pipelineCanvasPane.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            contextMenu.show(pipelineCanvasPane, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        pipelineCanvasPane.setOnMouseClicked(event1 -> {
            contextMenu.hide();
            closeSidebar(false);
            unselectAll();
            event1.consume();
        });

        pipelineCanvasPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (ProgramUtils.resetZoomCombination.match(event)) {
                StackedLogBar.instance().displayMessage("Zoom reset");
            }
            if (ProgramUtils.selectAllCombination.match(event)) {
                StackedLogBar.instance().displayMessage("All elements selected");
                selectAll();
            }
            if (ProgramUtils.saveCombination.match(event)) {
                StackedLogBar.instance().log("Saving pipeline");
                savePipeline();
            }
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                deleteSelected();

            }
            if (ProgramUtils.copyCombination.match(event)) {
                StackedLogBar.instance().displayMessage("Selected elements copied");
                copy();
            }
            if (ProgramUtils.pasteCombination.match(event)) {
                StackedLogBar.instance().displayMessage("Selected elements copied");
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
    public void addLinkView(Link link) {
        if (getLinkByName(link.getID()) != null) {
            LinkView lv = new LinkView(link);

            lv.bindLink(getModuleByName(link.getModuleA().getName()), getModuleByName(link.getModuleB().getName()));
            if (!link.getChannelsAToB().isEmpty()) {
                lv.bindButtonChannels("fromTo");
                lv.fromTo = true;
                lv.updateImageViews("fromTo");
            }

            if (!link.getChannelsBToA().isEmpty()) {
                lv.toFrom = true;
                lv.bindButtonChannels("toFrom");
                lv.updateImageViews("toFrom");
            }
            pipelineCanvasPane.getChildren().add(0, lv);
        }
    }

    /*
   *   Create a new DraggableModule and add it to the editor
   * */
    public void addDraggableModule(Module mod) {

        Point2D position = mod.getPosition();
        DraggableModule node = new DraggableModule(mod);

        //Set position to DraggableModule without a position set
        if (position == null) {

            posYAssign = calcolateBiggestPosY();

            posXAssign += 200;
            if (posXAssign > 1400 || posYAssign <= 100) {
                posXAssign = 300;
                posYAssign += 150;
            }

            position = new Point2D(posXAssign, posYAssign);

        }
        //    position = position.add(hoffset, voffset);
        //   node.setmDragOffset(new Point2D(hoffset, voffset));
        mod.setPosition(position);
        pipelineCanvasPane.getChildren().add(node);
        node.relocateToPoint(new Point2D(position.getX(), position.getY()));

        posYAssign = 100;
    }

    /*
    *  Return the biggest coordinate Y where is placed a node
    * */
    private double calcolateBiggestPosY() {
        double maxPosY = 40;
        for (Node node : pipelineCanvasPane.getChildren()) {

            if (node instanceof DraggableModule) {
                Point2D posNode = (pipelineCanvasPane.localToScene(node.getLayoutX(), node.getLayoutY()));
                if (posNode.getY() > maxPosY) {

                    maxPosY = posNode.getY();
                }
            }

        }
        if (maxPosY + 150 < posYAssign) {
            return posYAssign;
        }

        return maxPosY;
    }

    /*
    * Handlers to manage the creation of new modules from a moduleItem,
    * Drag detect of Module item to start with Drag & drop proccess
    * */
    private void addTemplateItemDragHandlers(ModuleItem moduleItem) {

        moduleItem.setOnDragDetected(event -> {
            //set drag respective events
            splitPane.setOnDragOver(mModuleItemOverRoot);
            mainScrollPane.setOnDragOver(mModuleItemOverMainScrollPane);

            pipelineCanvasPane.setOnDragDropped(mModuleItemDropped);

            //get the item clicked
            ModuleItem itemClicked = (ModuleItem) event.getSource();

            //drag operations
            //set shadowModule of draggableModuleItem
            currentlyDraggedModuleInstance.setParameters(itemClicked.getTemplateType());
            currentlyDraggedModuleInstance.relocate(new Point2D(event.getSceneX(), event.getSceneY()));

            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();

            container.addData("TemplateType", currentlyDraggedModuleInstance.getTemplateType());
            content.put(DragContainer.AddNode, container);

            currentlyDraggedModuleInstance.startDragAndDrop(TransferMode.ANY).setContent(content);
            currentlyDraggedModuleInstance.setVisible(true);
            currentlyDraggedModuleInstance.setMouseTransparent(true);
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
                    StackedLogBar.instance().logAndSuccess("Imported file: " + filePath);
                } else {
                    StackedLogBar.instance().logAndWarning("There was an error while importing");
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
                currentlyDraggedModuleInstance.relocate(new Point2D(event.getSceneX(), event.getSceneY()));
                return;
            }

            event.consume();

        };

        mModuleItemOverMainScrollPane = event -> {

            event.acceptTransferModes(TransferMode.ANY);

            //mouse coordinates->scene coordinates ->draggableModuleItem's parent
            //but now draggableModuleItem must be in the splitPane's coordinates to work
            Point2D position = new Point2D(event.getSceneX(), event.getSceneY());

            currentlyDraggedModuleInstance.relocate(position);

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

            currentlyDraggedModuleInstance.setVisible(false);

            //Create node drag operation
            DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

            if (container != null) {
                if (container.getValue("scene_coords") != null) {
                    //creo instance of model

                    Point2D mousePoint = container.getValue("scene_coords");
                    //build module
                    ModuleTemplate template = Main.templates.get(currentlyDraggedModuleInstance.getTemplateType());
                    Module module = Module.getInstance(template);
                    String name = template.getNameInstance();
                    name = ProgramUtils.checkDuplicateModules(name, 0);
                    module.setName(name);
                    module.setPosition(mousePoint);

                    Command addModule = new AddModule(module);
                    addModule.execute();
                    unselectAll();
                    getModuleByName(module.getName()).select();
                    CareTaker.addMemento(addModule);
                    StackedLogBar.instance().log(module.getName() + " created");

                    openSideBar(module);
                }
            }
            //AddLink drag operation
            container
                    = (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

            if (container != null) {

                //bind the ends of our link to the nodes whose id's are stored in the drag container
                String fromId = container.getValue("fromId");
                String toId = container.getValue("toId");

                if ((fromId != null && toId != null) && (!fromId.equals(toId))) {

                    DraggableModule from = getModuleByName(fromId);
                    DraggableModule to = getModuleByName(toId);

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
                                        LinkView linkV = getLinkByName(idLink);
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

    public void openSideBar(Module module) {
        if (Main.root.getRight() != null && ((SideBar) Main.root.getRight()).getModule() != module) {
            closeSidebar(true);
        }
        SideBar sidebar = new SideBar(module, 400);
        Main.root.setRight(sidebar);
        sidebar.show();
    }

    public static void closeSidebar(boolean force) {
        if (Main.root.getRight() == null) {
            return;
        } else {
            SideBar sidebar = (SideBar) Main.root.getRight();
            if (!sidebar.isPinned() || force) {
                Main.root.setRight(null);
                sidebar.hide(force);
            }
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
    public void removeLinkView(LinkView lv) {
        DraggableModule dmFrom = lv.getFrom();
        DraggableModule dmTo = lv.getTo();
        dmFrom.removeLinkView(lv);
        dmTo.removeLinkView(lv);
        pipelineCanvasPane.getChildren().remove(lv);

    }

    public Command removeDraggableModule(DraggableModule dm) {
        dm.unselect();
        ArrayList<Command> allCommands = new ArrayList<>();
        for (LinkView lv : dm.getLinks()) {
            Command removeLV = new RemoveLink(lv.getLink());
            allCommands.add(removeLV);
        }
        Command execAll = new ExecuteAll(allCommands);
        pipelineCanvasPane.getChildren().remove(dm);
        return execAll;
    }

    public void updateLinkView(LinkView lv, String orientation) {
        lv.updateImageViews(orientation);
        lv.bindButtonChannels(orientation);
    }

    public void selectAll() {
        for (Node n : pipelineCanvasPane.getChildren()) {
            if (n instanceof DraggableModule) {
                ((DraggableModule) n).select();
            } else if (n instanceof LinkView) {
                ((LinkView) n).select();
            }
        }
    }

    public void unselectAll() {
        for (Node n : pipelineCanvasPane.getChildren()) {
            if (n instanceof DraggableModule) {
                ((DraggableModule) n).unselect();
            } else if (n instanceof LinkView) {
                ((LinkView) n).unselect();
            }
        }
    }

    public List<DraggableModule> getSelectedModules() {
        List<DraggableModule> selection = new ArrayList<>();
        for (Node n : pipelineCanvasPane.getChildren()) {
            if (n instanceof DraggableModule) {
                DraggableModule dm = (DraggableModule) n;
                if (dm.isSelected()) {
                    selection.add(dm);
                }
            }
        }
        return selection;
    }

    public List<LinkView> getSelectedLinks() {
        List<LinkView> selection = new ArrayList<>();
        for (Node n : pipelineCanvasPane.getChildren()) {
            if (n instanceof LinkView) {
                LinkView lv = (LinkView) n;
                if (lv.isSelected()) {
                    selection.add(lv);
                }
            }
        }
        return selection;
    }

    @FXML
    private void deleteSelected() {
        closeSidebar(true);
        clearClipboard();
        ArrayList<Command> allRemoved = new ArrayList<>();

        for (DraggableModule mod : getSelectedModules()) {
            mod.unselect();
            Command removeModule = new RemoveModule(mod.getModule());
            allRemoved.add(removeModule);
        }

        for (LinkView ln : getSelectedLinks()) {
            ln.unselect();
            Command removeLink = new RemoveLink(ln.getLink());
            allRemoved.add(removeLink);
        }

        if (!allRemoved.isEmpty()) {
            Command remModAll = new ExecuteAll(allRemoved);
            remModAll.execute();
            CareTaker.addMemento(remModAll);
        }

        StackedLogBar.instance().logAndWarning("Deleted " + allRemoved.size() + " elements");
    }

    private void clearClipboard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<DraggableModule> getModules() {
        List<DraggableModule> selection = new ArrayList<>();
        for (Node n : pipelineCanvasPane.getChildren()) {
            if (n instanceof DraggableModule) {
                DraggableModule dm = (DraggableModule) n;
                selection.add(dm);

            }
        }
        return selection;
    }

    public List<LinkView> getLinks() {
        List<LinkView> selection = new ArrayList<>();
        for (Node n : pipelineCanvasPane.getChildren()) {
            if (n instanceof LinkView) {
                LinkView lv = (LinkView) n;
                selection.add(lv);
            }
        }
        return selection;
    }

}
