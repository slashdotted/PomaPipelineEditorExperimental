package commands;

import controller.MainWindow;
import main.Main;
import model.Module;

/**
 * Command for add a module to the current pipeline
 */
public class AddModule implements Command{
    private Module module;

    public AddModule(Module module) {
        this.module = module;
    }

    @Override
    public boolean execute() {
        Main.modules.put(module.getName(), module);
        MainWindow.addDraggableModule(module);
        return true;
    }

    @Override
    public boolean reverse() {
        Command reverse = new RemoveModule(module);
        return reverse.execute();
    }

}
