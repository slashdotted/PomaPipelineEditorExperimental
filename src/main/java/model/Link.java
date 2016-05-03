package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * Created by Marco on 18/03/2016.
 */
public class Link {
//TODO control changes simplestringproperties
    private final Module moduleA;
    private final Module moduleB;

    private ObservableList<SimpleStringProperty> channelsAToB;
    private ObservableList<SimpleStringProperty> channelsBToA;

    //private String channel = "default";


//    public Link() {
//        moduleA = null;
//        moduleB = null;
//    }

    public String getJsonId(String channel) {
        if (checkIfPresent(channel,channelsAToB) ||  checkIfPresent(channel,channelsBToA))
            return getID() + channel;
        else
            return null;
    }

    public Link (boolean isShadow){
        moduleA=null;
        moduleB=null;

    }
    public String getID() {
        return moduleA.getName() + "-" + moduleB.getName();
    }


    public Link(Module moduleA, Module moduleB, String channel) {
        this.moduleB = moduleB;
        this.moduleA = moduleA;

        this.channelsAToB = FXCollections.observableArrayList();
        this.channelsBToA = FXCollections.observableArrayList();

        this.channelsAToB.add(new SimpleStringProperty(channel));
    }


    public boolean addChannel(Module from, Module to, String channel) {
        if (from.equals(moduleA)) {
            if ((to.equals(moduleB))&&(!checkIfPresent(channel,channelsAToB))) {

                channelsAToB.add(new SimpleStringProperty(channel));
                return true;
            }
        }

        if (from.equals(moduleB)) {
            if ((to.equals(moduleA))&&(!checkIfPresent(channel,channelsBToA))) {
                channelsBToA.add(new SimpleStringProperty(channel));
                return true;
            }
        }
        return false;
    }



    public boolean removeChannel(Module from, Module to, String channel) {
        if (from.equals(moduleA)) {
            if (to.equals(moduleB)) {
               return channelsAToB.remove(new SimpleStringProperty(channel));

            }
        }

        if (from.equals(moduleB)) {
            if (to.equals(moduleA)) {
                return channelsBToA.remove(new SimpleStringProperty(channel));

            }
        }

        return false;
    }


    public static boolean checkIfPresent(String string, ObservableList<SimpleStringProperty> list){
        long match = list.stream().filter(stringProperty -> stringProperty.getValue().equals(string)).count();
        if(match>0)
            return true;
        return false;
    }

    public ObservableList<SimpleStringProperty> getChannelsAToB() {
        return channelsAToB;
    }

    public void setChannelsAToB(ObservableList<SimpleStringProperty> channelsAToB) {
        this.channelsAToB = channelsAToB;
    }

    public  ObservableList<SimpleStringProperty> getChannelsBToA() {
        return channelsBToA;
    }

    public void setChannelsBToA(ObservableList<SimpleStringProperty> channelsBToA) {
        this.channelsBToA = channelsBToA;
    }

    public Module getModuleA() {
        return moduleA;
    }

    public Module getModuleB() {
        return moduleB;
    }

    public Link copy(){

        Link link=new Link(this.moduleA,this.moduleB,"default");
        link.setChannelsAToB(this.channelsAToB);
        link.setChannelsBToA(this.channelsBToA);
        return link;
    }

    public void updateChannel (String orientation, String oldValue,String newValue) {
        List<SimpleStringProperty> listChannel=getChannelList(orientation);
        SimpleStringProperty channel=new SimpleStringProperty();
        channel.set(newValue);

        for(int i=0;i<listChannel.size();i++){

            if(listChannel.get(i).getValue().equals(oldValue)){
                listChannel.get(i).set(newValue);
                break;
            }
        }

    }
    public SimpleStringProperty getChannel(String name,String orientation){
        List<SimpleStringProperty> channels=getChannelList(orientation);
        for (SimpleStringProperty channel:channels ) {
            if (channel.getValue().equals(name)){
                return channel;
            }
        }
        return null;

    }
    public ObservableList<SimpleStringProperty> getChannelList(String orientation){

        switch (orientation){
            case "fromTo":
                return this.channelsAToB;

            case "toFrom":
                return this.channelsBToA;

        }
        return null;
    }

//    public String getChannel() {
//        return channel;
//    }

//    public void setChannel(String channel) {
//        this.channel = channel;
//    }
}
