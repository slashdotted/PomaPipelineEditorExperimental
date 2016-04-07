package commands;

/**
 * Created by felipe on 23/03/16.
 */
public interface Command {
    void execute();
    void revert();
}
