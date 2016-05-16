package utils;

import commands.Command;
import commands.RemoveLink;
import commands.RemoveModule;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import main.Main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for memento pattern management
 * This class offers undo and redo operations
 * Mementos are a collection of commands
 */
public class CareTaker {

    private static int counter = 0;
    private static int upperBound = 0;
    private static List<Command> mementos = new ArrayList<>();
    public static BooleanProperty redoable = new SimpleBooleanProperty(false);
    public static BooleanProperty undoable = new SimpleBooleanProperty(false);

    public static void addMemento(Command command) {

        redoable.setValue(false);
        Main.dirty.setValue(true);

        if (counter < mementos.size()) {
            mementos.set(counter, command);
            upperBound = counter - 1;
        } else {
            mementos.add(command);
        }
        undoable.setValue(true);

        counter++;
        if (upperBound == (counter - 2)) {
            upperBound++;
        }

    }

    public static void undo() {

        if (counter > 0) {
            Main.dirty.setValue(true);
            counter--;
            Command command = mementos.get(counter);
            command.reverse();
            redoable.setValue(true);
        }

        if (counter == 0) {
            Main.dirty.set(false);
            undoable.setValue(false);
        }

    }


    public static void redo() {

        if (counter <= upperBound) {
            Main.dirty.setValue(true);
            Command command = mementos.get(counter);
            command.execute();
            counter++;
            undoable.setValue(true);
        }
        if (counter == mementos.size())
            redoable.setValue(false);


    }

    public static void removeAllElements(){
        Set<String> linksKeySet = new HashSet<>( Main.links.keySet());
        linksKeySet.forEach(key->{
            Command removeCurrentLink = new RemoveLink(Main.links.get(key));
            removeCurrentLink.execute();
        });

        Set<String> modulesKeySet = new HashSet<>(Main.modules.keySet());

        modulesKeySet.forEach(key ->{
            Command removeCurrentModule = new RemoveModule(Main.modules.get(key));
            removeCurrentModule.execute();
        });

        resetCareTaker();
        Main.dirty.setValue(false);
    }

    public static int size(){
        return mementos.size();
    }

    public static void resetCareTaker(){
        mementos.clear();
        counter = 0;
        upperBound = 0;
        redoable.setValue(false);
        undoable.setValue(false);
    }

    public static String caretakerStatus(){
        return "\tCareTaker: \n" + "\t\tcounter: " + counter + " upperbound : " + upperBound + " mementos size: " +mementos.size();
    }


}
