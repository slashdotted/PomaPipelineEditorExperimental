package controller;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import main.Main;
import model.Link;

/**
 * Created by Marco on 18/03/2016.
 */
public class LinkView extends Group{

    private static String channelOutImage="ChannelOut.png";
    Point3D centerOut =new Point3D(0,0,0);
    private static String channelInImage="ChannelIn.png";
    private DoubleProperty channelInX=new SimpleDoubleProperty();
    private DoubleProperty channelinY=new SimpleDoubleProperty();
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

    private double arrowHeight = 9;
    private double halfArrowWidth = 5;

    private Line line=new Line();
    private Line line1Arrow = new Line();
    private Line line2Arrow = new Line();

    private Circle circle=new Circle();

    public LinkView(boolean isShadow) {
    }
    public LinkView(DraggableModule from,DraggableModule to, String channel){

            imageChannelIn=new ImageView("ChannelIn.png");
            imageChannelOut=new ImageView("ChannelOut.png");
            imageChannelOut.setFitWidth(30);
            imageChannelOut.setFitHeight(30);

            /*TODO add to Map of Main
            * update model
            * add controls (is already exist?)
            * */
            this.from=from;
            this.to=to;
            this.channel=channel;


            this.id=from.getName()+to.getName()+channel;
            link=new Link(Main.modules.get(from.getName()),Main.modules.get(to.getName()),"");
            Main.links.put(link.getID(),link);
            this.getChildren().add(line);
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
    public void bindBottonChannels(){

        //ScrollPane mainScrollpane = (ScrollPane) Main.mScene.lookup("mainScrollPane");


        channelOutX.bind((line.endXProperty().add(line.startXProperty()).divide(2.0)));
        channelOutY.bind((line.endYProperty().add(line.startYProperty()).divide(2.0)));


        circle.setOpacity(1);
        circle.setStyle("-fx-background-color:black;");
        imageChannelOut.setStyle("-fx-border-color: black;");
        circle.setRadius(5);
      //  imageChannelOut.xProperty().bind((line.endXProperty().add(line.startXProperty()).divide(2.0)));
      //  imageChannelOut.yProperty().bind((line.endYProperty().add(line.startYProperty()).divide(2.0)));
      //  imageChannelOut.setX((line.getEndX()+line.getStartX())/2);
       // imageChannelOut.setY((line.getEndY()+line.getStartY())/2);
        this.getChildren().add(circle);
        this.getChildren().add(imageChannelOut);
        updateBottonChannels();

    }

    public void updateBottonChannels(){
        // double angle=calcAngleLine();
      //  Math.rad

        centerOut.add(imageChannelOut.getX()-imageChannelOut.getFitHeight()/2,imageChannelOut.getY()-imageChannelOut.getFitWidth()/2,0);
        System.out.println("*--*-*-*-*-*-*"+calcAngleLine());

        circle.setCenterX(channelOutX.doubleValue()+Math.cos(calcAngleLine())*30);
        circle.setCenterY(channelOutY.doubleValue()+Math.sin(calcAngleLine())*30);


        System.out.println("COS angle"+Math.cos(calcAngleLine()));
        System.out.println("SIN angle"+Math.sin(calcAngleLine()));
      //  imageChannelOut.setX(channelOutX.intValue());
      //  imageChannelOut.setY(channelOutY.intValue());

        imageChannelOut.setX(channelOutX.intValue()+Math.cos(calcAngleLine())*30/*-Math.sin(calcAngleLine())*imageChannelOut.getFitWidth()/2*/);
        imageChannelOut.setY(channelOutY.intValue()+Math.sin(calcAngleLine())*30/*-Math.cos(calcAngleLine())*imageChannelOut.getFitHeight()/2*/);
        //imageChannelOut.setRotationAxis(Rotate.Z_AXIS);

        imageChannelOut.setRotationAxis(Rotate.Z_AXIS);
        imageChannelOut.setRotate(Math.toDegrees(calcAngleLine()));
        circle.setRotationAxis(Rotate.Z_AXIS);
        circle.setRotate(Math.toDegrees(calcAngleLine()));

    }
    private double calcAngleLine() {

        double angle= Math.atan2(        //find angle of line
                line.getEndY() - line.getStartY(),
                line.getEndX() - line.getStartX());
        return angle;

    }
/*
    private void calcolateArrow(Point2D from, Point2D to) {

        double angle = calcAngleLine(from, to);
        //calc vertex

        Point2D aroBase = new Point2D(
                (float) (to.getX() - arrowHeight * Math.cos(angle)),
                (float) (to.getY() - arrowHeight * Math.sin(angle))


        );
        //determine the location of middle of
        //the base of the arrow - basically move arrowHeight
        //distance back towards the starting point


        Point2D end1 = new Point2D(

                (float) (aroBase.getX() - halfArrowWidth * Math.cos(angle - Math.PI / 2)),
                (float) (aroBase.getY() - halfArrowWidth * Math.sin(angle - Math.PI / 2)));
        //locate one of the points, use angle-pi/2 to get the
        //angle perpendicular to the original line(which was 'angle')


        Point2D end2 = new Point2D(
                (float) (aroBase.getX() - halfArrowWidth * Math.cos(angle + Math.PI / 2)),
                (float) (aroBase.getY() - halfArrowWidth * Math.sin(angle + Math.PI / 2)));

        //same thing but with other side

        if (associationType == AssociationModel.AssociationType.Inherit) {

            arrowInherit.getPoints().setAll(to.getX(), to.getY(),
                    end1.getX(), end1.getY(),
                    end2.getX(), end2.getY());
            arrowInherit.setFill(Color.BLACK);

        } else {

            line1Arrow.setStartX(to.getX());
            line1Arrow.setStartY(to.getY());
            line1Arrow.setEndX(end1.getX());
            line1Arrow.setEndY(end1.getY());

            line2Arrow.setStartX(to.getX());
            line2Arrow.setStartY(to.getY());
            line2Arrow.setEndX(end2.getX());
            line2Arrow.setEndY(end2.getY());
        }

    }*/

}
