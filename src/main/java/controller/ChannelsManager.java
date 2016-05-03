package controller;

import commands.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Callback;
import model.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 13/04/2016.
 */
public class ChannelsManager extends BorderPane {

    private VBox vBox;
    private HBox buttons;
    private Button addChannel = new Button("ADD");
    private Button removeChannel = new Button("REMOVE");
    private ListView<SimpleStringProperty> listV = new ListView<>();
    private static String orientation;
    private ObservableList<SimpleStringProperty> channelsObs = null;
    private String oldValue;
    private Link link;
    static SimpleStringProperty currentChannel;

    public ChannelsManager(Link link, String orientation) {
        this.link = link;
        ChannelsManager.orientation = orientation;
        vBox = new VBox();
        buttons = new HBox();

        buttons.getChildren().addAll(addChannel, removeChannel);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().add(buttons);
        setCenter(vBox);

        channelsObs=link.getChannelList(orientation);

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
                            //     ImageView acceptChannel = new ImageView("/images/accept.png");
                            removeChannel.setFitHeight(15);
                            removeChannel.setFitWidth(15);

                            TextField channelName = new TextField();
                            channelName.setText(t.getValue());

                            channelName.setStyle("-fx-text-box-border: transparent;");

                            channelName.setAlignment(Pos.CENTER_LEFT);
                            channelName.setMaxWidth(Double.MAX_VALUE);
                            channelName.setEditable(false);
                            hbox.setHgrow(channelName, Priority.ALWAYS);


                            hboxImages.getChildren().addAll(removeChannel);

                            hbox.setAlignment(Pos.CENTER_LEFT);
                            hbox.getChildren().addAll(channelName, hboxImages);

                            addListenerHandler(channelName, t);
                            addListenerHandler(removeChannel,channelName);

                            setGraphic(hbox);


                        }
                    }

                    private void addListenerHandler(TextField channelName, SimpleStringProperty t) {

                        channelName.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {

                                oldValue = channelName.getText();
                                channelName.setEditable(true);

                            }
                        });

                       channelName.focusedProperty().addListener((observable, oldValue1, newValue) -> {
                            if (oldValue1 && !newValue) {
                                System.out.println(oldValue);

                                String newText = channelName.getText();
                                System.out.println(newValue);

                                //TODO aded to memento
                                if (!oldValue.equals(newText) && channelName.isEditable()) {
                                    Command edit = new EditStringProperty(t, newText);
                                    edit.execute();
                                }
                                channelName.setEditable(false);

                            }
                        });

                    }

                    private void addListenerHandler(ImageView removeChanne,TextField channelName) {

                        removeChanne.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                System.out.println("Elimino channel");

                                List<SimpleStringProperty> channels=link.getChannelList(ChannelsManager.orientation);
                                SimpleStringProperty channelToRemove=link.getChannel(channelName.getText(),ChannelsManager.orientation);
                                System.out.println(channelToRemove.getValue());
                                Command removeChannel=new RemoveChannel(channelToRemove,channels,link,ChannelsManager.orientation);
                                removeChannel.execute();




                                //TODO add to memento

                            }
                        });

                    }

                };


                return cell;
            }


        });


        vBox.getChildren().add(listV);


    }


}


