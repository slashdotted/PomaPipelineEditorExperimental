package controller;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import main.Main;
import model.Link;
import model.Module;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Marco on 18/03/2016.
 */
public class LinkView extends Group{

    private static String channelOutImageDefault="ChannelOut.png";
    private static String channelOutImageEntries="ChannelOut.png";
    private static String channelInImage="ChannelIn.png";

    Point3D centerOut =new Point3D(0,0,0);

    private DoubleProperty channelInX=new SimpleDoubleProperty();
    private DoubleProperty channelInY=new SimpleDoubleProperty();
    private DoubleProperty channelOutX=new SimpleDoubleProperty();
    private DoubleProperty channelOutY=new SimpleDoubleProperty();

    private  Link link;
    private DraggableModule from;
    private DraggableModule to;

    private String channel;
    private Label label;
    private String id;

    boolean fromTo=false;
    boolean toFrom=false;



    //visual effects
    private ImageView imageChannelIn;
    private ImageView imageChannelOut;
    private AnchorPane paneChannel;


    private Line line=new Line();


    public LinkView(boolean isShadow) {
        this.getChildren().add(0,line);
    }
    public LinkView(DraggableModule from,DraggableModule to, String channel){

            imageChannelIn=new ImageView("ChannelInDefault.png");
            imageChannelOut=new ImageView("ChannelOutDefault.png");
            imageChannelOut.setFitWidth(30);
            imageChannelOut.setFitHeight(30);
            imageChannelIn.setFitHeight(30);
            imageChannelIn.setFitWidth(30);


            /*TODO add to Map of Main
            * update model
            * add controls (is already exist?)
            * */
            this.from=from;
            this.to=to;
            this.channel=channel;


            this.id=from.getName()+to.getName()+channel;
            link=new Link(Main.modules.get(from.getName()),Main.modules.get(to.getName()),"default");
            Main.links.put(link.getID(),link);
            this.getChildren().add(0,line);
            MainWindow.allLinkView.put(link.getID(),this);


            //first time will be FromTo always
            fromTo=true;


    }



    public DraggableModule getFrom() {
        return from;
    }

    public DraggableModule getTo() {
        return to;
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
        line.setStartX(start.getX());
        line.setStartY(start.getY());
    }

    public void setEnd(Point2D end) {
        line.setEndX(end.getX());
        line.setEndY(end.getY());
    }

    public void bindLink(){
        bindLink(this.from,this.to);
    }
    public void bindLink (DraggableModule from,DraggableModule to){


        line.startXProperty().bind(from.layoutXProperty().add(from.getWidth()/2.0));


        line.startYProperty().bind(from.layoutYProperty().add(to.getHeight()/2.0));;

        line.endXProperty().bind(to.layoutXProperty().add(from.getWidth()/2.0));

        line.endYProperty().bind(to.layoutYProperty().add(to.getHeight()/2.0));

        from.addLink (link.getID());
        to.addLink (link.getID());


    }
    public void bindBottonChannels(String orientation){


        channelOutX.bind((line.endXProperty().add(line.startXProperty()).divide(2.0)));
        channelOutY.bind((line.endYProperty().add(line.startYProperty()).divide(2.0)));

        channelInX.bind(line.startXProperty().add(line.endXProperty()).divide(2.0));
        channelInY.bind(line.startYProperty().add(line.endYProperty()).divide(2.0));

        switch(orientation){
            case "toFrom":
                toFrom=true;
                this.getChildren().add(imageChannelIn);
                 break;
            case "fromTo":

                this.getChildren().add(imageChannelOut);
                break;
        }

        updateBottonChannels();

    }

    public void updateBottonChannels(){

        System.out.println("*--*-*-*-*-*-*"+calcAngleLine());


        System.out.println("COS angle"+Math.cos(calcAngleLine()));
        System.out.println("SIN angle"+Math.sin(calcAngleLine()));

        if(fromTo) {
            imageChannelOut.setX(channelOutX.intValue()+ Math.cos(calcAngleLine()) * 30 - imageChannelOut.getFitHeight() / 2);
            imageChannelOut.setY(channelOutY.intValue()+ Math.sin(calcAngleLine()) * 30 - imageChannelOut.getFitWidth() / 2);
            //imageChannelOut.setRotationAxis(Rotate.Z_AXIS);

            imageChannelOut.setRotationAxis(Rotate.Z_AXIS);
            imageChannelOut.setRotate(Math.toDegrees(calcAngleLine()));
        }
        if(toFrom){
            imageChannelIn.setX(channelInX.intValue() - Math.cos(calcAngleLine())*30  - imageChannelIn.getFitHeight() / 2);
            imageChannelIn.setY(channelInY.intValue() - Math.sin(calcAngleLine()) *30 - imageChannelIn.getFitWidth() / 2);
            //imageChannelOut.setRotationAxis(Rotate.Z_AXIS);

            imageChannelIn.setRotationAxis(Rotate.Z_AXIS);
            imageChannelIn.setRotate(Math.toDegrees(calcAngleLine()));

        }


    }
    private double calcAngleLine() {

        double angle= Math.atan2(        //find angle of line
                line.getEndY() - line.getStartY(),
                line.getEndX() - line.getStartX());
        return angle;

    }


    public String getName() {
        return link.getID();
    }

    public void addChannel(DraggableModule from, DraggableModule to,String channel) {
       Module modFrom =Main.modules.get(from.getName());
        Module modTo =Main.modules.get(to.getName());

        link.addChannel(modFrom,modTo,channel);
    }

    public Link getLink() {
        return link;
    }
}
