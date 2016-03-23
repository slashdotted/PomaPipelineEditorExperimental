package controller;



/**
 * Created by Marco on 18/03/2016.
 */
public class NodeLink {

    private DraggableModule from;
    private DraggableModule to;

    private String channel;

    public NodeLink(){}


    public void setFrom(DraggableModule from) {
        this.from = from;
    }

    public void setTo(DraggableModule to) {
        this.to = to;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }


}
