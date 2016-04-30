package commands;

import controller.MainWindow;
import controller.SideBar;
import main.Main;
import model.Link;
import model.Module;

/**
 * Created by felipe on 28/04/16.
 */
public class EditItem<T> implements Command {
    T oldestValue;
    T oldValue;
    T newValue;
    Module module;

    public EditItem(T oldValue, T newValue) {
        this.oldValue = oldValue;
        this.oldestValue=oldValue;
        this.newValue = newValue;
        this.module = null;
    }

 /*   public EditItem(T oldValue, T newValue, Module module) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.module = module;
    }*/

    @Override
    public boolean execute() {
        oldValue = newValue;


        if(oldValue instanceof Module){
            MainWindow.update(((Module) oldValue).getName(),(Module) newValue);
        }
        if(oldValue instanceof Link){
            MainWindow.updateLinkView((Link)oldValue,(Link)newValue);
        }


        return true;
    }

    @Override
    public boolean reverse() {
        EditItem<T> reverse = new EditItem(newValue, oldestValue);
        return reverse.execute();
    }
}
