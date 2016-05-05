package commands;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by felipe on 05/05/16.
 */
public class ExecuteAll implements Command {
    ArrayList<Command> commands = new ArrayList<>();

    public ExecuteAll(ArrayList<Command> commands) {
        this.commands = commands;
    }

    @Override
    public boolean execute() {
        commands.forEach(command -> {
            command.execute();
        });
        return true;
    }

    @Override
    public boolean reverse() {
        Collections.reverse(commands);

        return execute();
    }
}
