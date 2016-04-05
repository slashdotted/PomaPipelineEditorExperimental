package model;

/**
 * Created by Marco on 18/03/2016.
 */
public class Link {

    private final Module from;
    private final Module to;
    private String channel = "default";

    public Link() {
        from = null;
        to = null;
    }

    public String getID() {
        return from.getName() + "-" + to.getName() + ":" + channel;
    }


    public Link(Module from, Module to, String channel) {
        this.to = to;
        this.from = from;
        if (channel != null)
            this.channel = channel;
    }


    public Module getFrom() {
        return from;
    }

    public Module getTo() {
        return to;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
