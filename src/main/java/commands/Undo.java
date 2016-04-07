package commands;

/**
 * Created by felipe on 23/03/16.
 */
public class Undo implements Command {
    @Override
    public boolean execute() {
        return true;
    }

    @Override
    public boolean reverse() {
        return false;
    }

}
