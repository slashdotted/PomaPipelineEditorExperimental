package utils;

import controller.ChannelView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Callback;
import model.Link;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 13/04/2016.
 */
public class ChannelsManager extends BorderPane {

    private VBox vBox;
    private HBox buttons;
    private Button addChannel=new Button("ADD");
    private Button removeChannel=new Button("REMOVE");
    private ListView<SimpleStringProperty> listV=new ListView<>();
    private String orientation;
    private  ObservableList<SimpleStringProperty> channelsObs =null;
    String oldValue="";
        public ChannelsManager(Link link, String orientation){
            vBox=new VBox();
            buttons=new HBox();

            buttons.getChildren().addAll(addChannel,removeChannel);
            buttons.setAlignment(Pos.CENTER_RIGHT);
            vBox.getChildren().add(buttons);



            setCenter(vBox);
            switch (orientation){
                case "fromTo":
                    orientation="fromTo";
                    channelsObs= FXCollections.observableList(link.getChannelsAToB());
                 //  Bindings.bindContent(link.getChannelsAToB(),channelsObs);
                  //  channelsObs= FXCollections.observableList();
                    break;
                case "toFrom":
                    orientation="toFrom";
                    channelsObs= FXCollections.observableList(link.getChannelsBToA());

                  //  Bindings.bindContent(link.getChannelsBToA(),channelsObs);
                    //channelsObs=FXCollections.observableList(link.getChannelsBToA());
                    break;

            }

            listV.setItems(channelsObs);
            listV.setCellFactory(new Callback<ListView<SimpleStringProperty>, ListCell<SimpleStringProperty>>() {
             @Override
             public ListCell<SimpleStringProperty> call(ListView<SimpleStringProperty> param) {

                 ListCell<SimpleStringProperty> cell = new ListCell<SimpleStringProperty>(){

                     @Override
                     protected void updateItem(SimpleStringProperty t, boolean bln) {
                         super.updateItem(t, bln);
                         if (t != null) {
                             HBox hbox=new HBox();
                             HBox hboxImages=new HBox();



                             ImageView removeChannel=new ImageView("/images/Delete.png");
                             ImageView acceptChannel=new ImageView("/images/accept.png");
                             removeChannel.setFitHeight(15);
                             removeChannel.setFitWidth(15);
                             acceptChannel.setFitHeight(15);
                             acceptChannel.setFitWidth(15);
                             acceptChannel.setVisible(false);


                             TextField channelName=new TextField();
                             channelName.setText(t.getValue());
                            // Bindings.bindBidirectional(channelName.textProperty(),t);
                                channelName.setStyle("-fx-text-box-border: transparent;");

                             channelName.setAlignment(Pos.CENTER_LEFT);
                             channelName.setMaxWidth(Double.MAX_VALUE);
                             hbox.setHgrow(channelName, Priority.ALWAYS);



                             hboxImages.getChildren().addAll(acceptChannel,removeChannel);

                             hbox.setAlignment(Pos.CENTER_LEFT);
                             hbox.getChildren().addAll(channelName,hboxImages);

                             addListenerHandler(channelName,acceptChannel);
                             addListenerHandler(removeChannel);


                             setGraphic(hbox);


                         }
                     }

                     private void addListenerHandler(TextField channelName,ImageView acceptImage) {

                         channelName.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                             @Override
                             public void handle(MouseEvent event) {
                                 //TODO let modify name of channel
                                 acceptImage.setVisible(true);
                                 oldValue=channelName.getText();
                                 channelName.setEditable(true);
                                 System.out.println("Click on label");
                                 System.out.println(channelsObs.toString());


                             }
                         });

                         channelName.focusedProperty().addListener((observable, oldValue1, newValue) ->
                         {if( !newValue && oldValue1){
                             Platform.runLater(() ->acceptImage.setVisible(false) );


                         }

                         });
                         acceptImage.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                             @Override
                             public void handle(MouseEvent event) {
                                 System.out.println(oldValue);
                                 System.out.println(channelName.getText());
                                 if(!oldValue.equals(channelName.getText())){
                                     System.out.println("ha cambiato");
                                 }
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


