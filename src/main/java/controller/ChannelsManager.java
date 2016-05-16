package controller;

import commands.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import main.Main;
import model.Link;
import utils.CareTaker;
import utils.GraphicsElementsFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 13/04/2016.
 */
public class ChannelsManager extends BorderPane {

    private VBox vBox;
    private HBox buttons;
    private Button addChannel = new Button();
    private Button removeChannel = new Button();

    private ListView<SimpleStringProperty> listV = new ListView<>();
    private static String orientation;
    private ObservableList<SimpleStringProperty> channelsObs = null;
    private String oldValue;
    private Link link;
    private TextField newChannelField;
    static SimpleStringProperty currentChannel;
    private int firstSelect = -1;

/*
* Windows to manage all the channels of a link
* */

    public ChannelsManager(Link link, String orientation) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.mScene.getWindow());
        String tittle = "";
        this.setPadding(new Insets(2, 2, 2, 5));
        switch (orientation) {
            case "fromTo":
                tittle = link.getModuleA().getName() + " -> " + link.getModuleB().getName();

                break;
            case "toFrom":
                tittle = link.getModuleB().getName() + " -> " + link.getModuleA().getName();
                break;
        }
        stage.setTitle(tittle);
        stage.setScene(new Scene(this, 300, 300));
        stage.setResizable(false);

        stage.show();
        stage.setOnCloseRequest(event -> {
            (MainWindow.allLinkView.get(link.getID())).unselectImage(orientation);
            (MainWindow.allLinkView.get(link.getID())).updateImageViews(orientation);
            if (link.getNumberOfChannels() == 0) {
                Command removeLink = new RemoveLink(link);
                removeLink.execute();

                CareTaker.addMemento(removeLink);

            }

        });


        setButtons();
        this.link = link;

        ChannelsManager.orientation = orientation;
        vBox = new VBox();
        buttons = new HBox();
        newChannelField = new TextField();
        newChannelField.setPrefWidth(240);

        buttons.getChildren().addAll(newChannelField, addChannel, removeChannel);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().add(buttons);
        setCenter(vBox);

        channelsObs = link.getChannelList(orientation);
        setButtonsEvents();

        //LinkView with all the channels of a link

        listV.setItems(channelsObs);
        listV.setCellFactory(new Callback<ListView<SimpleStringProperty>, ListCell<SimpleStringProperty>>() {
            @Override
            public ListCell<SimpleStringProperty> call(ListView<SimpleStringProperty> param) {


                ListCell<SimpleStringProperty> cell = new ListCell<SimpleStringProperty>() {

                    /**
                     * All controllers of every events handlers in the listView and Buttons of this windows
                     * */
                    @Override
                    protected void updateItem(SimpleStringProperty t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            HBox hbox = new HBox();
                            HBox hboxImages = new HBox();

                            currentChannel = t;
                            ImageView removeChannel = new ImageView("/images/Delete.png");

                            removeChannel.setFitHeight(15);
                            removeChannel.setFitWidth(15);

                            TextField channelName = new TextField();
                            channelName.setText(t.getValue());

                            channelName.setStyle("-fx-text-box-border: transparent;");

                            channelName.setAlignment(Pos.CENTER_LEFT);
                            channelName.setMaxWidth(Double.MAX_VALUE);

                            hbox.setHgrow(channelName, Priority.ALWAYS);


                            hboxImages.getChildren().addAll(removeChannel);

                            hbox.setAlignment(Pos.CENTER_LEFT);
                            hbox.getChildren().addAll(channelName, hboxImages);

                            addListenerHandler(channelName, t, this.getIndex());
                            addListenerHandler(removeChannel, channelName);

                            setGraphic(hbox);


                        } else setGraphic(null);
                    }




                    private void addListenerHandler(TextField channelName, SimpleStringProperty t, int cell) {

                        channelName.setOnMouseClicked(event -> {

                            oldValue = channelName.getText();
                            if (event.isShiftDown()) {

                                if (firstSelect < 0) {
                                    firstSelect = cell;


                                } else {
                                    listV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                                    int aux = cell;
                                    if (firstSelect > cell) {

                                        aux = firstSelect;
                                        firstSelect = cell;

                                    }
                                    listV.getSelectionModel().selectRange(firstSelect, aux + 1);
                                }

                            } else if (event.isControlDown()) {
                                firstSelect = -1;
                                listV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                                listV.getSelectionModel().select(cell);
                            } else {
                                firstSelect = cell;
                                listV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                                listV.getSelectionModel().clearAndSelect(cell);
                            }


                        });

                        channelName.focusedProperty().addListener((observable, oldValue1, newValue) -> {
                            if (oldValue1 && !newValue) {

                                String newText = channelName.getText();

                                if (!oldValue.equals(newText) && channelName.isEditable()) {
                                    Command edit = new EditStringProperty(t, newText);
                                    edit.execute();

                                    CareTaker.addMemento(edit);
                                }


                            }
                        });

                    }

                    private void addListenerHandler(ImageView removeChanne, TextField channelName) {

                        removeChanne.setOnMouseClicked(event -> {

                            List<SimpleStringProperty> channels = link.getChannelList(ChannelsManager.orientation);
                            SimpleStringProperty channelToRemove = link.getChannel(channelName.getText(), ChannelsManager.orientation);
                            String title = "Remove channel";
                            String msg = "Are you sure you wish remove the selected channel?";

                            if (GraphicsElementsFactory.showWarning(title, msg)) {
                                Command removeChannel1 = new RemoveChannel(channelToRemove, channels, link, ChannelsManager.orientation);
                                removeChannel1.execute();

                                CareTaker.addMemento(removeChannel1);
                            }


                        });

                    }

                };


                return cell;
            }


        });


        vBox.getChildren().add(listV);


    }


    private void setButtonsEvents() {
        removeChannel.setOnAction(event -> {
            ObservableList<SimpleStringProperty> selection = listV.getSelectionModel().getSelectedItems();
            ObservableList<Integer> indices = listV.getSelectionModel().getSelectedIndices();

            int i = indices.size();
            String title = "Remove channels";
            String msg = "Are you sure you wish remove the " + i + " selected channel?";
            if (GraphicsElementsFactory.showWarning(title, msg)) {
                ArrayList<Command> allAdds = new ArrayList<>();
                for (int j = 0; j < i; j++) {
                    SimpleStringProperty selectedItem = listV.getItems().get(indices.get(j));
                    Command command = new RemoveChannel(selectedItem, link.getChannelList(orientation), link, orientation);


                    allAdds.add(command);
                }
                Command executeAll = new ExecuteAll(allAdds);
                executeAll.execute();
                CareTaker.addMemento(executeAll);
            }

        });
        addChannel.setOnAction(event -> {
            SimpleStringProperty newValue;
            String newString = newChannelField.getText();
            if (!newString.equals("")) {
                newValue = new SimpleStringProperty(newString);
                Command addChannel = new AddChannel(newValue, link.getChannelList(orientation), link, orientation);
                addChannel.execute();
                CareTaker.addMemento(addChannel);
            }
            newChannelField.clear();
        });
    }

    private void setButtons() {
        addChannel.setGraphic(new ImageView("/images/plus.png"));
        addChannel.setBackground(Background.EMPTY);
        removeChannel.setGraphic(new ImageView("/images/minus.png"));
        removeChannel.setBackground(Background.EMPTY);
        addChannel.setTooltip(new Tooltip("Add channel"));
        removeChannel.setTooltip(new Tooltip("Remove channel"));
    }


}


