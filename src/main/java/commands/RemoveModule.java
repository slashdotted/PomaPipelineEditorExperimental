package commands;

import controller.MainWindow;
import main.Main;
import model.Module;

/**
 * Created by Marco on 01/05/2016.
 */
public class RemoveModule implements Command {
    private Module module;

    public RemoveModule(Module module) {
        this.module = module;
    }

    @Override
    public boolean execute() {
        debug(module.toString());
        MainWindow.removeDraggableModule(MainWindow.allDraggableModule.get(module.getName()));
        Main.modules.remove(module.getName());
        return false;
    }

    @Override
    public boolean reverse() {
        Command addModule = new AddModule(module);
        return addModule.execute();
    }
}
