package controller;

import commands.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Link;
import utils.GraphicsElementsFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private int firstSelect=-1;

    public ChannelsManager(Link link, String orientation) {
        setButtons();
        this.link = link;

        ChannelsManager.orientation = orientation;
        vBox = new VBox();
        buttons = new HBox();
        newChannelField=new TextField();
        newChannelField.setPrefWidth(240);

        buttons.getChildren().addAll(newChannelField,addChannel, removeChannel);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().add(buttons);
        setCenter(vBox);

        channelsObs = link.getChannelList(orientation);
        setButtonsEvents();
        listV.setItems(channelsObs);
        listV.setCellFactory(new Callback<ListView<SimpleStringProperty>, ListCell<SimpleStringProperty>>() {
            @Override
            public ListCell<SimpleStringProperty> call(ListView<SimpleStringProperty> param) {


                ListCell<SimpleStringProperty> cell = new ListCell<SimpleStringProperty>() {

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

                        channelName.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {

                                oldValue = channelName.getText();
                                if (event.isShiftDown()) {

                                    if(firstSelect<0){
                                        firstSelect=cell;
                                        System.out.println("setto firstselect");

                                    }else{
                                        listV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                                        int aux=cell;
                                        if(firstSelect>cell){

                                            aux=firstSelect;
                                            firstSelect=cell;

                                        }
                                        listV.getSelectionModel().selectRange(firstSelect,aux+1);
                                    }

                                } else if (event.isControlDown()) {
                                    firstSelect=-1;
                                    listV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                                    listV.getSelectionModel().select(cell);
                                } else {
                                    firstSelect=cell;
                                    listV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                                    listV.getSelectionModel().clearAndSelect(cell);
                                }


                            }
                        });

                        channelName.focusedProperty().addListener((observable, oldValue1, newValue) -> {
                            if (oldValue1 && !newValue) {

                                String newText = channelName.getText();

                                //TODO aded to memento
                                if (!oldValue.equals(newText) && channelName.isEditable()) {
                                    Command edit = new EditStringProperty(t, newText);
                                    edit.execute();
                                }


                            }
                        });

                    }

                    private void addListenerHandler(ImageView removeChanne, TextField channelName) {

                        removeChanne.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                System.out.println("Elimino channel");
                                System.out.println(listV.getItems().size());

                                List<SimpleStringProperty> channels = link.getChannelList(ChannelsManager.orientation);
                                SimpleStringProperty channelToRemove = link.getChannel(channelName.getText(), ChannelsManager.orientation);
                                System.out.println(channelToRemove.getValue());
                                String title="Remove channel";
                                String msg="Are you sure you wish remove the selected channel?";

                                if(GraphicsElementsFactory.showWarning(title,msg)) {
                                    Command removeChannel = new RemoveChannel(channelToRemove, channels, link, ChannelsManager.orientation);
                                    removeChannel.execute();
                                    //TODO add to memento
                                }
                                //listV.refresh();
                                //System.out.println(listV.getItems().size());


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
        removeChannel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<SimpleStringProperty> selection = listV.getSelectionModel().getSelectedItems();
                ObservableList<Integer> indices = listV.getSelectionModel().getSelectedIndices();

                System.out.println(selection.size() + "siazeeeeeeeeee");
                int i = indices.size();
                String title="Remove channels";
                String msg="Are you sure you wish remove the "+i+" selected channel?";
                if(GraphicsElementsFactory.showWarning(title,msg)) {
                    while (i > 0) {
                        SimpleStringProperty selectedItem = listV.getItems().get(indices.get(0));
                        Command command = new RemoveChannel(selectedItem, link.getChannelList(orientation), link, orientation);
                        command.execute();
                        i--;
                        //TODO add to memento

                    }
                }

            }
        });
        addChannel.setOnAction(event -> {
            SimpleStringProperty newValue;
            String newString =newChannelField.getText();
            if(!newString.equals("")){
                newValue=new SimpleStringProperty(newString);
                Command addChannel=new AddChannel(newValue,link.getChannelList(orientation),link,orientation);
                addChannel.execute();
                //TODO add to memento
            }
            newChannelField.clear();
        });
    }

    private void setButtons() {
        addChannel.setGraphic(new ImageView("/images/plus.png"));
        removeChannel.setGraphic(new ImageView("/images/minus.png"));
        addChannel.setTooltip(new Tooltip("Add channel"));
        removeChannel.setTooltip(new Tooltip("Remove channel"));
    }


}


