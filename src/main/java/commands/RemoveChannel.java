package commands;

import controller.LinkView;
import controller.MainWindow;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import main.Main;
import model.Link;

import java.util.List;

/**
 * Created by Marco on 02/05/2016.
 */
public class RemoveChannel implements Command {
    private SimpleStringProperty valueToRemove;
    private List<SimpleStringProperty> motherStructure;
    private Link link;
    private String orientation;

    public RemoveChannel(SimpleStringProperty valueToRemove, List<SimpleStringProperty> motherStructure, Link link,String orientation) {
        this.valueToRemove = valueToRemove;
        this.motherStructure = motherStructure;
        this.link=link;
        this.orientation=orientation;

    }

    @Override
    public boolean execute() {
        LinkView lv= MainWindow.allLinkView.get(link.getID());
        motherStructure.remove(valueToRemove);
        Platform.runLater(() -> lv.updateImageViews(orientation));


        return false;
    }

    @Override
    public boolean reverse() {
        Command addChannel=new AddChannel(valueToRemove,motherStructure, link,orientation);
        return addChannel.execute();
    }
}
