package commands;

/**
 * Created by felipe on 23/03/16.
 */
public interface Command {
    boolean execute();

    boolean reverse();

    default void debug(String info) {
        System.out.println("\t\tIn command: " + this.getClass().getSimpleName());
        System.out.println("\t\tInfo: " + info);
    }
}
