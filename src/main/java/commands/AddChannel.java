package commands;

import controller.LinkView;
import controller.MainWindow;
import javafx.beans.property.SimpleStringProperty;
import model.Link;

import java.util.List;

/**
 *  Command for edit a Value
 */
public class AddChannel implements Command {

    private SimpleStringProperty newValue;
    private List<SimpleStringProperty> motherStructure;
    private String orientation;
    private Link link;

    public AddChannel(SimpleStringProperty newValue, List<SimpleStringProperty> motherStructure, Link link, String orientation) {
        this.newValue = newValue;
        this.motherStructure = motherStructure;
        this.link = link;
        this.orientation = orientation;

    }

    @Override
    public boolean execute() {
        LinkView lv = MainWindow.instance().getLinkByName(link.getID());
        boolean success = link.addChannel(orientation, newValue);
        MainWindow.instance().updateLinkView(lv, orientation);
        return success;
    }

    @Override
    public boolean reverse() {
        Command addChannel = new RemoveChannel(newValue, motherStructure, link, orientation);
        return addChannel.execute();
    }
}
