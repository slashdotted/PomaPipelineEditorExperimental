package commands;


import java.util.ArrayList;
import java.util.Collections;

/**
 *
 */
public class ExecuteAll implements Command {
    ArrayList<Command> commands = new ArrayList<>();

    public ExecuteAll(ArrayList<Command> commands) {
        this.commands = commands;
    }

    @Override
    public boolean execute() {
        commands.forEach(command -> command.execute());
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
