package utils;

import commands.Command;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felipe on 03/05/16.
 */
public class CareTaker {

    private static int counter = 0;
    private static int upperBound = 0;
    private static List<Command> mementos = new ArrayList<>();
    public BooleanProperty redoable = new SimpleBooleanProperty(false);
    public BooleanProperty undoable = new SimpleBooleanProperty(false);
    //TODO implement redoable and undoable logic

    public static void addMemento(Command command) {
        System.out.println("---------In add ----------");
        System.out.println("Before, mementos size: " + mementos.size());
        System.out.println("counter = " + counter + ", upperbound = " + upperBound);

        if (counter < mementos.size()) {
            System.out.println("Setting");
            mementos.set(counter, command);
            upperBound = counter - 1;
            //mementos = mementos.subList(0, counter);
        } else {
            System.out.println("Adding");
            mementos.add(command);
        }

        counter++;
        if (upperBound == (counter - 2)) {
            upperBound++;
        }
        System.out.println("After, mementos size: " + mementos.size());
        System.out.println("counter = " + counter + ", upperbound = " + upperBound);
        System.out.println("--------------------------\n");
    }

    public static void undo() {
        System.out.println("---------In undo---------");
        System.out.println("Before, mementos size: " + mementos.size());
        System.out.println("counter = " + counter + ", upperbound = " + upperBound);

        if (counter > 0) {
            counter--;
            System.out.println("Reversing command");
            Command command = mementos.get(counter);
            command.reverse();
        }

        System.out.println("After, mementos size: " + mementos.size());
        System.out.println("counter = " + counter + ", upperbound = " + upperBound);
        System.out.println("--------------------------\n");

    }


    public static void redo() {
        System.out.println("---------In redo---------");

        System.out.println("Before, mementos size: " + mementos.size());
        System.out.println("counter = " + counter + ", upperbound = " + upperBound);

        if (counter <= upperBound ) {
            System.out.println("Executing command");
            Command command = mementos.get(counter);
            command.execute();
            counter++;
        }

        System.out.println("After, mementos size: " + mementos.size());
        System.out.println("counter = " + counter + ", upperbound = " + upperBound);
        System.out.println("--------------------------\n");

    }

}
