package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Marco on 18/03/2016.
 */
public class Link {
//TODO control chanfes simplestringproperties
    private final Module moduleA;
    private final Module moduleB;

    private List<SimpleStringProperty> channelsAToB;
    private List<SimpleStringProperty> channelsBToA;

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

        this.channelsAToB = new ArrayList<>();
        this.channelsBToA = new ArrayList<>();

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


    public static boolean checkIfPresent(String string, List<SimpleStringProperty> list){
        long match = list.stream().filter(stringProperty -> stringProperty.getValue().equals(string)).count();
        if(match>0)
            return true;
        return false;
    }

    public List<SimpleStringProperty> getChannelsAToB() {
        return channelsAToB;
    }

    public void setChannelsAToB(List<SimpleStringProperty> channelsAToB) {
        this.channelsAToB = channelsAToB;
    }

    public List<SimpleStringProperty> getChannelsBToA() {
        return channelsBToA;
    }

    public void setChannelsBToA(List<SimpleStringProperty> channelsBToA) {
        this.channelsBToA = channelsBToA;
    }

    public Module getModuleA() {
        return moduleA;
    }

    public Module getModuleB() {
        return moduleB;
    }

//    public String getChannel() {
//        return channel;
//    }

//    public void setChannel(String channel) {
//        this.channel = channel;
//    }
}
