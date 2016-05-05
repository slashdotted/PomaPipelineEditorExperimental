package commands;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by felipe on 05/05/16.
 */
public class ExecuteAll implements Command {
    private ArrayList<Command> commands = new ArrayList<>();

    public ExecuteAll(ArrayList<Command> commands) {
        this.commands = commands;
    }

    @Override
    public boolean execute() {
        //debug("Executing all command");
        for (Command command: commands) {
            command.execute();
        }
        return true;
    }

    @Override
    public boolean reverse() {
        Collections.reverse(commands);
        boolean executed = true;

        for (Command command : commands) {
            executed &= command.reverse();
        }

        return executed;
    }
}
