package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Marco on 18/03/2016.
 */
public class Link {

    private final Module moduleA;
    private final Module moduleB;

    private Set<String> channelsAToB;
    private Set<String> channelsBToA;

    //private String channel = "default";


//    public Link() {
//        moduleA = null;
//        moduleB = null;
//    }

    public String getJsonId(String channel) {
        if (channelsAToB.contains(channel) || channelsBToA.contains(channel))
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

        this.channelsAToB = new HashSet<>();
        this.channelsBToA = new HashSet<>();

        this.channelsAToB.add(channel);
    }

    public boolean addChannel(Module from, Module to, String channel) {
        if (from.equals(moduleA)) {
            if (to.equals(moduleB)) {
                channelsAToB.add(channel);
                return true;
            }
        }

        if (from.equals(moduleB)) {
            if (to.equals(moduleA)) {
                channelsBToA.add(channel);
                return true;
            }
        }
        return false;
    }

    public boolean removeChannel(Module from, Module to, String channel) {
        if (from.equals(moduleA)) {
            if (to.equals(moduleB)) {
                channelsAToB.remove(channel);
                return true;
            }
        }

        if (from.equals(moduleB)) {
            if (to.equals(moduleA)) {
                channelsBToA.remove(channel);
                return true;
            }
        }

        return false;
    }

    public Set<String> getChannelsAToB() {
        return channelsAToB;
    }

    public void setChannelsAToB(Set<String> channelsAToB) {
        this.channelsAToB = channelsAToB;
    }

    public Set<String> getChannelsBToA() {
        return channelsBToA;
    }

    public void setChannelsBToA(Set<String> channelsBToA) {
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
