package utils;

import controller.ChannelView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
    private ListView<ChannelView> listV=new ListView<>();
    private String orientation;
    private  ObservableList<ChannelView> channelsObs =null;
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
                    channelsObs= FXCollections.observableList(popolateChannels(link.getChannelsAToB()));
                  //  channelsObs= FXCollections.observableList();
                    break;
                case "toFrom":
                    orientation="toFrom";
                    channelsObs= FXCollections.observableList(popolateChannels(link.getChannelsBToA()));
                    //channelsObs=FXCollections.observableList(link.getChannelsBToA());
                    break;

            }

            listV.setItems(channelsObs);
         listV.setCellFactory(new Callback<ListView<ChannelView>, ListCell<ChannelView>>() {
             @Override
             public ListCell<ChannelView> call(ListView<ChannelView> param) {

                 ListCell<ChannelView> cell = new ListCell<ChannelView>(){

                     @Override
                     protected void updateItem(ChannelView t, boolean bln) {
                         super.updateItem(t, bln);
                         if (t != null) {
                             HBox hbox=new HBox();
                             HBox hboxImages=new HBox();

                           // hbox.setPrefWidth(300);
                             Label channelName=new Label(t.getName());

                             channelName.setAlignment(Pos.CENTER_LEFT);
                             channelName.setMaxWidth(Double.MAX_VALUE);
                             hbox.setHgrow(channelName, Priority.ALWAYS);


                             ImageView editChannel=t.getEditChannel();
                             ImageView removeChannel=t.getRemoveChannel();

                             addListenerHandler(channelName);
                             addListenerHandler(removeChannel);



                             hboxImages.getChildren().addAll(editChannel,removeChannel);

                             hbox.setAlignment(Pos.CENTER_LEFT);
                             hbox.getChildren().addAll(channelName,hboxImages);

                             setGraphic(hbox);


                         }
                     }

                     private void addListenerHandler(Label channelName) {

                         channelName.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                             @Override
                             public void handle(MouseEvent event) {
                                 //TODO let modify name of channel

                                 System.out.println("Click on label");
                             }
                         });
                         
                     }

                     private void addListenerHandler(ImageView removeChanne) {

                         removeChanne.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
                             @Override
                             public void handle(MouseEvent event) {
                                 //TODO update ListView e Model
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

    private List<ChannelView> popolateChannels(List<String> channelsFromLink) {
        List<ChannelView> allChannels=new ArrayList<>();
        for (String channel  :channelsFromLink) {

            ChannelView chanV=new ChannelView(channel);
            allChannels.add(chanV);
        }

        return allChannels;
    }

}


