package controller;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import main.Main;
import model.Link;

/**
 * Created by Marco on 18/03/2016.
 */
public class LinkView extends Group {

    private static String channelOutImageDefault = "ChannelOutDefault.png";
    private static String channelOutImageEntries = "ChannelOutEntries.png";
    private static String channelInImageDefault = "ChannelInDefault.png";
    private static String channelInImageEntries = "ChannelInEntries.png";
    private static String channelOutImageDefaultSelected = "ChannelOutDefaultSelected.png";
    private static String channelOutImageEntriesSelected = "ChannelOutEntriesSelected.png";
    private static String channelInImageDefaultSelected = "ChannelInDefaultSelected.png";
    private static String channelInImageEntriesSelected = "ChannelInEntriesSelected.png";


    private DoubleProperty channelInX = new SimpleDoubleProperty();
    private DoubleProperty channelInY = new SimpleDoubleProperty();
    private DoubleProperty channelOutX = new SimpleDoubleProperty();
    private DoubleProperty channelOutY = new SimpleDoubleProperty();

    private boolean selectedOut;
    private boolean selectedIn;
    private Link link;
    private DraggableModule from;
    private DraggableModule to;


    boolean fromTo = false;
    boolean toFrom = false;


    //visual effects
    private ImageView imageChannelIn;
    private ImageView imageChannelOut;
    private boolean select;
    private String oldStyle;


    private Line line = new Line();

    /*
    * Constructor of LinkView through a link (Model)
    * */
    public LinkView(Link link) {
        initImages();
        this.from = MainWindow.allDraggableModule.get(link.getModuleA().getName());
        this.to = MainWindow.allDraggableModule.get(link.getModuleB().getName());

        this.link = link;

        addHandlerChannels(imageChannelIn, link, "toFrom");
        addHandlerChannels(imageChannelOut, link, "fromTo");

        selectedIn = false;
        selectedOut = false;

        oldStyle = this.getStyle();
        this.getChildren().add(0, line);


    }

    /*
    * Create a shadowLink to help user to create a new LinkView
    * */
    public LinkView(boolean isShadow) {
        this.getChildren().add(0, line);
    }


    private void initImages() {
        imageChannelIn = new ImageView("ChannelInDefault.png");
        imageChannelOut = new ImageView("ChannelOutDefault.png");

        imageChannelOut.setFitWidth(30);
        imageChannelOut.setFitHeight(30);
        imageChannelIn.setFitHeight(30);
        imageChannelIn.setFitWidth(30);

    }

    /*
    * Handlers relatives to the linkView events, click, drag&Drop creation...
    * */
    private void addHandlerChannels(ImageView image, Link link, String orientation) {
        image.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (!event.isControlDown()) {

                selectImage(orientation);
                select();
                //open Channels manager windows to modify the channels
                ChannelsManager channelsManager = new ChannelsManager(link, orientation);

            } else {
                if (!select) {
                    select();
                } else {
                    unselect();
                }
            }
        });
    }

    /*
    * Modify css style, structure selectedLinks, and Channels views of link view if it is selected or unselected
    * */
    public void unselect() {
        this.setStyle(oldStyle);
        this.select = false;
        MainWindow.selectedLinks.remove(getName());
    }

    private void select() {
        this.select = true;
        this.setStyle("-fx-border-color: darkblue");
        this.setStyle("-fx-effect: dropshadow(three-pass-box, darkblue, 10,0, 0, 0) ");
        MainWindow.selectedLinks.put(getName(), Main.links.get(getName()));


    }


    public void updateImageViews(String orientation) {
        switch (orientation) {
            case "fromTo":
                int size = link.getChannelsAToB().size();
                if (!selectedOut) {
                    if (size > 1) {
                        imageChannelOut.setImage(new Image(channelOutImageEntries));
                    } else if (size == 1) {
                        imageChannelOut.setImage(new Image(channelOutImageDefault));
                    } else {
                        this.getChildren().remove(imageChannelOut);
                    }
                }
                if (selectedOut) {
                    if (size > 1) {
                        imageChannelOut.setImage(new Image(channelOutImageEntriesSelected));
                    } else if (size == 1) {
                        imageChannelOut.setImage(new Image(channelOutImageDefaultSelected));
                    } else {
                        this.getChildren().remove(imageChannelOut);
                    }
                }

                setTooltipImmage(imageChannelOut, link.getChannelsAToB());
                break;
            case "toFrom":
                int size2 = link.getChannelsBToA().size();
                if (selectedIn) {
                    if (size2 > 1) {
                        imageChannelIn.setImage(new Image(channelInImageEntriesSelected));
                    } else if (size2 == 1) {
                        imageChannelIn.setImage(new Image(channelInImageDefaultSelected));
                    } else {
                        this.getChildren().remove(imageChannelIn);
                    }

                } else {
                    if (size2 > 1) {
                        imageChannelIn.setImage(new Image(channelInImageEntries));
                    } else if (size2 == 1) {
                        imageChannelIn.setImage(new Image(channelInImageDefault));
                    } else {
                        this.getChildren().remove(imageChannelIn);
                    }

                }
                setTooltipImmage(imageChannelIn, link.getChannelsBToA());
        }

    }

    public void unselectImage(String orientation) {
        ImageView image = getImmage(orientation);
        switch (orientation) {
            case "fromTo":
                if (link.getChannelsAToB().size() == 1) {
                    image.setImage(new Image(channelOutImageDefault));
                } else {
                    image.setImage(new Image(channelOutImageEntries));
                }
                selectedOut = false;
                break;
            case "toFrom":
                if (link.getChannelsAToB().size() == 1) {
                    image.setImage(new Image(channelInImageDefault));
                } else {
                    image.setImage(new Image(channelInImageEntries));
                }
                selectedIn = false;
                break;
        }
    }

    public void selectImage(String orientation) {
        ImageView image = getImmage(orientation);
        switch (orientation) {
            case "fromTo":
                if (link.getChannelsAToB().size() == 1) {
                    if (image != null) {
                        image.setImage(new Image(channelOutImageDefaultSelected));
                    }
                } else {
                    if (image != null) {
                        image.setImage(new Image(channelOutImageEntriesSelected));
                    }
                }
                selectedOut = true;
                break;
            case "toFrom":
                if (link.getChannelsBToA().size() == 1) {
                    if (image != null) {
                        image.setImage(new Image(channelInImageDefaultSelected));
                    }
                } else {
                    if (image != null) {
                        image.setImage(new Image(channelInImageEntriesSelected));
                    }
                }
                selectedIn = true;
                break;
        }
    }

    private void setTooltipImmage(ImageView immageChannel, ObservableList<SimpleStringProperty> channelsList) {
        Tooltip tooltip = new Tooltip();
        SimpleStringProperty channelsFromTo = new SimpleStringProperty();
        String channels = "";
        for (SimpleStringProperty strProp : channelsList) {
            channels += strProp.getValue();
            channels += "\n";
            channelsFromTo.setValue(channels);

        }

        tooltip.textProperty().bind(channelsFromTo);
        Tooltip.install(immageChannel, tooltip);

    }


    /*
    * Binds start and end points of the linkview to the modules
    * */

    public void bindLink(DraggableModule from, DraggableModule to) {


        line.startXProperty().bind(from.layoutXProperty().add(from.getWidth() / 2.0));


        line.startYProperty().bind(from.layoutYProperty().add(to.getHeight() / 2.0));


        line.endXProperty().bind(to.layoutXProperty().add(from.getWidth() / 2.0));

        line.endYProperty().bind(to.layoutYProperty().add(to.getHeight() / 2.0));


        from.addLink(link.getID());
        to.addLink(link.getID());


    }

    /*
    * Bind the image channels to the correct position in the link
    * */
    public void bindButtonChannels(String orientation) {


        channelOutX.bind((line.endXProperty().add(line.startXProperty()).divide(2.0)));
        channelOutY.bind((line.endYProperty().add(line.startYProperty()).divide(2.0)));

        channelInX.bind(line.startXProperty().add(line.endXProperty()).divide(2.0));
        channelInY.bind(line.startYProperty().add(line.endYProperty()).divide(2.0));

        switch (orientation) {
            case "toFrom":
                toFrom = true;
                if (!this.getChildren().contains(imageChannelIn)) {
                    this.getChildren().add(imageChannelIn);
                }

                break;
            case "fromTo":
                fromTo = true;
                if (!this.getChildren().contains(imageChannelOut)) {
                    this.getChildren().add(imageChannelOut);
                }

                break;
        }

        updateBottonChannels();

    }

    /*
    * Update image relative to the channels with the correct position an orientation
    * */
    public void updateBottonChannels() {


        if (fromTo) {
            imageChannelOut.setX(channelOutX.intValue() + Math.cos(calcAngleLine()) * 20 - imageChannelOut.getFitHeight() / 2);
            imageChannelOut.setY(channelOutY.intValue() + Math.sin(calcAngleLine()) * 20 - imageChannelOut.getFitWidth() / 2);
            imageChannelOut.setRotationAxis(Rotate.Z_AXIS);
            imageChannelOut.setRotate(Math.toDegrees(calcAngleLine()));
        }
        if (toFrom) {
            imageChannelIn.setX(channelInX.intValue() - Math.cos(calcAngleLine()) * 20 - imageChannelIn.getFitHeight() / 2);
            imageChannelIn.setY(channelInY.intValue() - Math.sin(calcAngleLine()) * 20 - imageChannelIn.getFitWidth() / 2);
            imageChannelIn.setRotationAxis(Rotate.Z_AXIS);
            imageChannelIn.setRotate(Math.toDegrees(calcAngleLine()));

        }


    }

    /*
    * Find angle of line to rotate the image relative to channels
    * */
    private double calcAngleLine() {

        return Math.atan2(
                line.getEndY() - line.getStartY(),
                line.getEndX() - line.getStartX());

    }


    /*
    *  Logic to know if a linkView can be selected if one module get selected
    * */
    public void select(String dragMod) {
        if (dragMod.equals(from.getName())) {
            if (to.isSelected()) {
                select();

            }
        } else {
            if (from.isSelected()) {
                select();
            }
        }

    }




/*
* Getter and Setter
* */

    public DraggableModule getFrom() {
        return from;
    }

    public DraggableModule getTo() {
        return to;
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

    private ImageView getImmage(String orientation) {

        switch (orientation) {
            case "fromTo":
                return imageChannelOut;

            case "toFrom":
                return imageChannelIn;

            default:
                return null;
        }

    }

    public String getName() {
        return link.getID();
    }


    public Link getLink() {
        return link;
    }


}
