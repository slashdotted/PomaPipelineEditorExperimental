package commands;

import controller.LinkView;
import controller.MainWindow;
import javafx.beans.property.SimpleStringProperty;
import model.Link;

import java.util.List;

/**
 * Created by Marco on 02/05/2016.
 */
public class AddChannel implements Command {

    private SimpleStringProperty newValue;
    private List<SimpleStringProperty> motherStructure;
    private String orientation;
    private Link link;

    public AddChannel(SimpleStringProperty newValue, List<SimpleStringProperty> motherStructure, Link link,String orientation) {
        this.newValue = newValue;
        this.motherStructure = motherStructure;
        this.link=link;
        this.orientation=orientation;
    }

    @Override
    public boolean execute() {
        LinkView lv= MainWindow.allLinkView.get(link.getID());
        motherStructure.add(newValue);
        MainWindow.updateLinkView(lv,orientation);

        return false;
    }

    @Override
    public boolean reverse() {
        Command addChannel=new RemoveChannel(newValue,motherStructure,link,orientation);
        return addChannel.execute();
    }
}
