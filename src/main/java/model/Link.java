package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Link class
 */
public class Link {
    private final Module moduleA;
    private final Module moduleB;

    private ObservableList<SimpleStringProperty> channelsAToB;
    private ObservableList<SimpleStringProperty> channelsBToA;




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

    public boolean addChannel(String orientation, SimpleStringProperty channel){
        switch (orientation){
            case "fromTo":
                return addChannel(moduleA,moduleB,channel);

            case "toFrom":
                return addChannel(moduleB,moduleA,channel);

            default:
                return false;
        }
    }
    public boolean addChannel(Module from, Module to, SimpleStringProperty channel) {
        if (from.equals(moduleA)) {
            if ((to.equals(moduleB))&&(!checkIfPresent(channel.getValue(),channelsAToB))) {

                channelsAToB.add(channel);
                return true;
            }
        }

        if (from.equals(moduleB)) {
            if ((to.equals(moduleA))&&(!checkIfPresent(channel.getValue(),channelsBToA))) {
                channelsBToA.add(channel);
                return true;
            }
        }
        return false;
    }



    public boolean removeChannel(String orientation,SimpleStringProperty channel) {
        switch (orientation){
            case "fromTo":
                return channelsAToB.remove(channel);
            case "toFrom":
                return channelsBToA.remove(channel);
            default:
                return false;
        }

    }


    public static boolean checkIfPresent(String string, ObservableList<SimpleStringProperty> list){
        long match = list.stream().filter(stringProperty -> stringProperty.getValue().equals(string)).count();
        return match > 0;
    }

    public ObservableList<SimpleStringProperty> getChannelsAToB() {
        return channelsAToB;
    }


    public  ObservableList<SimpleStringProperty> getChannelsBToA() {
        return channelsBToA;
    }


    public Module getModuleA() {
        return moduleA;
    }

    public Module getModuleB() {
        return moduleB;
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
    public int getNumberOfChannels(){
        return channelsAToB.size()+channelsBToA.size();
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

    @Override
    public String toString() {
        return "Link{" +
                "moduleB=" + moduleB +
                ", moduleA=" + moduleA +
                '}';
    }
}
