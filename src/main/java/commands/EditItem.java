package commands;

import controller.MainWindow;
import controller.SideBar;
import main.Main;
import model.Module;

/**
 * Created by felipe on 28/04/16.
 */
public class EditItem<T> implements Command {

    T oldValue;
    T newValue;
    Module module;

    public EditItem(T oldValue, T newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.module = null;
    }

    public EditItem(T oldValue, T newValue, Module module) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.module = module;
    }

    @Override
    public boolean execute() {
        oldValue = newValue;


        if(oldValue instanceof Module){
            MainWindow.update(((Module) oldValue).getName(),(Module) newValue);
        }

        return true;
    }

    @Override
    public boolean reverse() {
        EditItem<T> reverse = new EditItem(newValue, oldValue, module);
        return reverse.execute();
    }
}
