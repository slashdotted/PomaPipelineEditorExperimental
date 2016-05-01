package utils;

import commands.Command;
import commands.EditStringProperty;
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
        this.orientation = orientation;
        vBox = new VBox();
        buttons = new HBox();

        buttons.getChildren().addAll(addChannel, removeChannel);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().add(buttons);
        setCenter(vBox);

        switch (orientation) {
            case "fromTo":
                orientation = "fromTo";
                channelsObs = FXCollections.observableList(link.getChannelsAToB());

                break;
            case "toFrom":
                orientation = "toFrom";
                channelsObs = FXCollections.observableList(link.getChannelsBToA());

                break;

        }

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
                            ImageView acceptChannel = new ImageView("/images/accept.png");
                            removeChannel.setFitHeight(15);
                            removeChannel.setFitWidth(15);
                            acceptChannel.setFitHeight(15);
                            acceptChannel.setFitWidth(15);


                            TextField channelName = new TextField();
                            channelName.setText(t.getValue());

                            channelName.setStyle("-fx-text-box-border: transparent;");

                            channelName.setAlignment(Pos.CENTER_LEFT);
                            channelName.setMaxWidth(Double.MAX_VALUE);
                            channelName.setEditable(false);
                            hbox.setHgrow(channelName, Priority.ALWAYS);


                            hboxImages.getChildren().addAll(acceptChannel, removeChannel);

                            hbox.setAlignment(Pos.CENTER_LEFT);
                            hbox.getChildren().addAll(channelName, hboxImages);

                            addListenerHandler(channelName, acceptChannel, t);
                            addListenerHandler(removeChannel);

                            setGraphic(hbox);


                        }
                    }

                    private void addListenerHandler(TextField channelName, ImageView acceptImage, SimpleStringProperty t) {

                        channelName.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                //TODO let modify name of channel

                                oldValue = channelName.getText();
                                channelName.setEditable(true);
                                System.out.println("Click on label");
                                System.out.println(channelsObs.toString());


                            }
                        });


                        acceptImage.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                System.out.println(oldValue);

                                String newValue = channelName.getText();
                                System.out.println(newValue);

                                //TODO aded to memento
                                if (!oldValue.equals(newValue) && channelName.isEditable()) {


                                    Command edit = new EditStringProperty( t,newValue);
                                    edit.execute();
                                }
                                channelName.setEditable(false);
                            }
                        });

                    }

                    private void addListenerHandler(ImageView removeChanne) {

                        removeChanne.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {

                                System.out.println("Click on remove");
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


