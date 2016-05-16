package commands;

import controller.LinkView;
import controller.MainWindow;
import main.Main;
import model.Link;

/**
 * Command for remove a link from model
 */
public class RemoveLink implements Command {

    private Link link;

    public RemoveLink(Link link) {
        this.link = link;
    }

    @Override
    public boolean execute() {
        LinkView lv = MainWindow.allLinkView.get(link.getID());
        MainWindow.removeLinkView(lv);
        if (Main.links.containsKey(link.getID())) {
            Main.links.remove(link.getID());
        }
        return false;
    }


    @Override
    public boolean reverse() {
        Command addLink = new AddLink(link);
        return addLink.execute();
    }
}
