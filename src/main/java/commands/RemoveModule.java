package commands;

import controller.MainWindow;
import main.Main;
import model.Module;

/**
 * Command for remove a module from model
 */
public class RemoveModule implements Command {
    private Module module;
    private Command removeLinks;

    public RemoveModule(Module module) {
        this.module = module;
    }

    @Override
    public boolean execute() {
        removeLinks = MainWindow.instance().removeDraggableModule(MainWindow.instance().getModuleByName(module.getName()));
        removeLinks.execute();
        Main.modules.remove(module.getName());
        return false;
    }

    @Override
    public boolean reverse() {
        Command addModule = new AddModule(module);
        addModule.execute();

        return removeLinks.reverse();
    }
}
