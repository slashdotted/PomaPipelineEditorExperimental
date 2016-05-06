package utils;

import commands.Command;
import commands.RemoveLink;
import commands.RemoveModule;
import controller.MainWindow;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import main.Main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by felipe on 03/05/16.
 */
public class CareTaker {

    private static int counter = 0;
    private static int upperBound = 0;
    private static List<Command> mementos = new ArrayList<>();
    public static BooleanProperty redoable = new SimpleBooleanProperty(false);
    public static BooleanProperty undoable = new SimpleBooleanProperty(false);

    public static void addMemento(Command command) {
//        System.out.println("---------In add ----------");
//        System.out.println("Before, mementos size: " + mementos.size());
//        System.out.println("counter = " + counter + ", upperbound = " + upperBound);
        redoable.setValue(false);
        Main.dirty.setValue(true);

        if (counter < mementos.size()) {
            //System.out.println("Setting");
            mementos.set(counter, command);
            upperBound = counter - 1;
            //mementos = mementos.subList(0, counter);
        } else {
            //System.out.println("Adding");
            mementos.add(command);
        }
        undoable.setValue(true);

        counter++;
        if (upperBound == (counter - 2)) {
            upperBound++;
        }


//        System.out.println("After, mementos size: " + mementos.size());
//        System.out.println("counter = " + counter + ", upperbound = " + upperBound);
//        System.out.println("--------------------------\n");
    }

    public static void undo() {
//        System.out.println("---------In undo---------");
//        System.out.println("Before, mementos size: " + mementos.size());
//        System.out.println("counter = " + counter + ", upperbound = " + upperBound);

        if (counter > 0) {
            Main.dirty.setValue(true);
            counter--;
           // System.out.println("Reversing command");
            Command command = mementos.get(counter);
            command.reverse();
            redoable.setValue(true);
        }

//        System.out.println("After, mementos size: " + mementos.size());
//        System.out.println("counter = " + counter + ", upperbound = " + upperBound);
//        System.out.println("--------------------------\n");
        if (counter == 0) {
            Main.dirty.set(false);
            undoable.setValue(false);
        }

    }


    public static void redo() {
//        System.out.println("---------In redo---------");
//
//        System.out.println("Before, mementos size: " + mementos.size());
//        System.out.println("counter = " + counter + ", upperbound = " + upperBound);

        if (counter <= upperBound) {
            Main.dirty.setValue(true);
            System.out.println("Executing command");
            Command command = mementos.get(counter);
            command.execute();
            counter++;
            undoable.setValue(true);
        }
        if (counter == mementos.size())
            redoable.setValue(false);

//        System.out.println("After, mementos size: " + mementos.size());
//        System.out.println("counter = " + counter + ", upperbound = " + upperBound);
//        System.out.println("--------------------------\n");

    }

    public static void removeAllElements(){
        Set<String> linksKeySet = new HashSet<>( Main.links.keySet());
        linksKeySet.forEach(key->{
            Command removeCurrentLink = new RemoveLink(Main.links.get(key));
            removeCurrentLink.execute();
            //addMemento(removeCurrentLink);
        });

        Set<String> modulesKeySet = new HashSet<>(Main.modules.keySet());

        modulesKeySet.forEach(key ->{
            Command removeCurrentModule = new RemoveModule(Main.modules.get(key));
            removeCurrentModule.execute();
            //addMemento(removeCurrentModule);
        });

        resetCareTaker();
        Main.dirty.setValue(false);

        Main.modules.keySet().forEach(s -> System.out.println(Main.modules.get(s)));
        Main.links.keySet().forEach(s -> System.out.println(Main.links.get(s)));
        MainWindow.allDraggableModule.keySet().forEach(s -> System.out.println(MainWindow.allDraggableModule.get(s)));
        MainWindow.allLinkView.keySet().forEach(s -> System.out.println(MainWindow.allLinkView.get(s)));
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

}
