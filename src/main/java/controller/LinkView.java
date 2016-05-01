package controller;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Main;
import model.Link;
import model.Module;
import utils.ChannelsManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Marco on 18/03/2016.
 */
public class LinkView extends Group{

    private static String channelOutImageDefault="ChannelOutDefault.png";
    private static String channelOutImageEntries="ChannelOutEntries.png";
    private static String channelInImageDefault="ChannelInDefault.png";
    private static String channelInImageEntries="ChannelInEntries.png";
    private static String channelOutImageDefaultSelected="ChannelOutDefaultSelected.png";
    private static String channelOutImageEntriesSelected="ChannelOutEntriesSelected.png";
    private static String channelInImageDefaultSelected="ChannelInDefaultSelected.png";
    private static String channelInImageEntriesSelected="ChannelInEntriesSelected.png";
    private static String channelInImage="ChannelIn.png";

    Point3D centerOut =new Point3D(0,0,0);

    private DoubleProperty channelInX=new SimpleDoubleProperty();
    private DoubleProperty channelInY=new SimpleDoubleProperty();
    private DoubleProperty channelOutX=new SimpleDoubleProperty();
    private DoubleProperty channelOutY=new SimpleDoubleProperty();

    private  Link link;
    private DraggableModule from;
    private DraggableModule to;

    private Label label;
    private String id;

    boolean fromTo=false;
    boolean toFrom=false;



    //visual effects
    private ImageView imageChannelIn;
    private ImageView imageChannelOut;
    private AnchorPane paneChannel;


    public Line getLine() {
        return line;
    }

    private Line line=new Line();

    public LinkView(Link link){
        initImages();
        this.from=MainWindow.allDraggableModule.get(link.getModuleA().getName());
        this.to=MainWindow.allDraggableModule.get(link.getModuleB().getName());

        this.link=link;

        addHandlerChannels(imageChannelIn,link,"toFrom");
        addHandlerChannels(imageChannelOut,link,"fromTo");

        this.getChildren().add(0,line);


    }

    public LinkView(boolean isShadow) {
        this.getChildren().add(0,line);
    }


    private void initImages() {
        imageChannelIn=new ImageView("ChannelInDefault.png");

        imageChannelOut=new ImageView("ChannelOutDefault.png");
        imageChannelOut.setFitWidth(30);
        imageChannelOut.setFitHeight(30);
        imageChannelIn.setFitHeight(30);
        imageChannelIn.setFitWidth(30);
    }

    private void addHandlerChannels(ImageView image,Link link,String orientation) {
        image.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                select(image,orientation);
                ChannelsManager root=new ChannelsManager(link,orientation);
                Stage stage=new Stage();
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(Main.mScene.getWindow());
                String tittle="";

                switch(orientation){
                    case "fromTo":
                        tittle="From: "+from.getName()+"->"+" to: "+to.getName();

                        break;
                    case "toFrom":
                        tittle="From: "+to.getName()+"->"+"to: "+from.getName();
                        break;
                }
                stage.setTitle(tittle);
                stage.setScene(new Scene(root,300,300));
                stage.setResizable(false);

                stage.show();
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        unselect(image,orientation);
                        updateImageViews(orientation);
                    }
                });

            }
        });
    }

    public void updateImageViews(String orientation) {
        switch (orientation){
            case "fromTo":
                int size=link.getChannelsAToB().size();
                if(size>1){
                    imageChannelOut.setImage(new Image(channelOutImageEntries));
                }else if(size==1){
                    imageChannelOut.setImage(new Image(channelOutImageDefault));
                }
                break;
            case "toFrom":
                int size2=link.getChannelsBToA().size();
                if(size2>1){
                    imageChannelIn.setImage(new Image(channelInImageEntries));
                }else if(size2==1){
                    imageChannelIn.setImage(new Image(channelInImageDefault));
                }
                break;
        }
    }

    private void unselect(ImageView image, String orientation) {
        switch(orientation){
            case "fromTo":
                if(link.getChannelsAToB().size()==1) {
                    image.setImage(new Image(channelOutImageDefault));
                }else{
                    image.setImage(new Image(channelOutImageEntries));
                }
                break;
            case "toFrom":
                if(link.getChannelsAToB().size()==1) {
                    image.setImage(new Image(channelInImageDefault));
                }else{
                    image.setImage(new Image(channelInImageEntries));
                }
                break;
        }
    }

    private void select(ImageView image,String orientation) {
        switch(orientation){
            case "fromTo":
                if(link.getChannelsAToB().size()==1) {
                    image.setImage(new Image(channelOutImageDefaultSelected));
                }else{
                    image.setImage(new Image(channelOutImageEntriesSelected));
                }
                break;
            case "toFrom":
                if(link.getChannelsAToB().size()==1) {
                    image.setImage(new Image(channelInImageDefaultSelected));
                }else{
                    image.setImage(new Image(channelInImageEntriesSelected));
                }
                break;
        }
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
                fromTo=true;
                this.getChildren().add(imageChannelOut);
                break;
        }

        updateBottonChannels();

    }

    public void updateBottonChannels(){


        if(fromTo) {
            imageChannelOut.setX(channelOutX.intValue()+ Math.cos(calcAngleLine()) * 30 - imageChannelOut.getFitHeight() / 2);
            imageChannelOut.setY(channelOutY.intValue()+ Math.sin(calcAngleLine()) * 30 - imageChannelOut.getFitWidth() / 2);
            imageChannelOut.setRotationAxis(Rotate.Z_AXIS);
            System.out.println(Math.toDegrees(calcAngleLine())+"faslkjdfklajfdlkaklñfjda");
            imageChannelOut.setRotate(Math.toDegrees(calcAngleLine()));
        }
        if(toFrom){
            imageChannelIn.setX(channelInX.intValue() - Math.cos(calcAngleLine())*30  - imageChannelIn.getFitHeight() / 2);
            imageChannelIn.setY(channelInY.intValue() - Math.sin(calcAngleLine()) *30 - imageChannelIn.getFitWidth() / 2);
            //imageChannelOut.setRotationAxis(Rotate.Z_AXIS);

            imageChannelIn.setRotationAxis(Rotate.Z_AXIS);
            imageChannelIn.setRotate(Math.toDegrees(calcAngleLine()));

        }
        System.out.println(imageChannelOut.getX()+"imageoutX");
        System.out.println(imageChannelOut.getY()+"imageoutY");
        System.out.println(imageChannelIn.getX()+"imageinX");
        System.out.println(imageChannelIn.getY()+"imageinY");

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
