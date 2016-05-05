package commands;

import controller.MainWindow;
import javafx.application.Platform;
import main.Main;
import model.Module;
import model.ModuleTemplate;

/**
 * Created by felipe on 06/04/16.
 */
public class AddModule implements Command{
    private Module module;

    public AddModule(Module module) {
        this.module = module;
    }

    @Override
    public boolean execute() {
        //debug("module = " + module.toString());

        Main.modules.put(module.getName(), module);
        //Platform.runLater(() ->MainWindow.addDraggableModule(module) );
        MainWindow.addDraggableModule(module);
        return true;
    }

    @Override
    public boolean reverse() {
        Command reverse = new RemoveModule(module);
        return reverse.execute();
    }

}
