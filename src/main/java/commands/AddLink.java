package commands;

import controller.MainWindow;
import javafx.application.Platform;
import main.Main;
import model.Link;

/**
 * Created by Marco on 01/05/2016.
 */
public class AddLink implements Command {

    private Link link;

    public AddLink(Link link) {
        this.link = link;
    }

    @Override
    public boolean execute() {
        Main.links.put(link.getID(), link);
        Platform.runLater(() -> MainWindow.addLinkView(link));
        debug("");
        return true;
    }

    @Override
    public boolean reverse() {
        Command removeLink=new RemoveLink(link);
        return removeLink.execute();
    }
}
