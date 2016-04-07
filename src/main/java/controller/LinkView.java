package controller;


import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;

/**
 * Created by Marco on 18/03/2016.
 */
public class LinkView extends Group{

    private DraggableModule from;
    private DraggableModule to;

    private String channel;
    private Label label;

    //visual effects
    private double arrowHeight = 9;
    private double halfArrowWidth = 5;

    private Line line1Arrow = new Line();
    private Line line2Arrow = new Line();
    private Line line;


    public LinkView(){
        line=new Line();
    }


    public void setFrom(DraggableModule from) {
        this.from = from;
    }

    public void setTo(DraggableModule to) {
        this.to = to;
    }

    public void setChannel(String channel) {
        this.channel = channel;
        this.label.setText(channel);
    }


    public void setStart(Point2D start) {
        this.line.setStartX(start.getX());
        this.line.setStartY(start.getY());
    }

    public void setEnd(Point2D end) {
        this.line.setEndX(end.getX());
        this.line.setEndY(end.getY());
    }
}
