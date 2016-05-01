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
        Main.modules.put(module.getName(), module);
        Platform.runLater(() ->MainWindow.addDraggableModule(module) );


        return false;
    }

    @Override
    public boolean reverse() {

        return false;
    }

}
