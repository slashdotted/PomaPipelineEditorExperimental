package commands;

import controller.LinkView;
import controller.MainWindow;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import model.Link;

import java.util.List;

/**
 * Command for remove a channel from a link
 */
public class RemoveChannel implements Command {
    private SimpleStringProperty valueToRemove;
    private List<SimpleStringProperty> motherStructure;
    private Link link;
    private String orientation;

    public RemoveChannel(SimpleStringProperty valueToRemove, List<SimpleStringProperty> motherStructure, Link link, String orientation) {
        this.valueToRemove = valueToRemove;
        this.motherStructure = motherStructure;
        this.link = link;
        this.orientation = orientation;

    }

    @Override
    public boolean execute() {
        LinkView lv = MainWindow.allLinkView.get(link.getID());
        boolean success = link.removeChannel(orientation, valueToRemove);
        Platform.runLater(() -> lv.updateImageViews(orientation));


        return success;
    }

    @Override
    public boolean reverse() {
        Command addChannel = new AddChannel(valueToRemove, motherStructure, link, orientation);
        return addChannel.execute();
    }
}
