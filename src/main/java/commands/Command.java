package commands;

/**
 * Interface for all commands, thi structure is suitable for a memento element in CareTaker
 */
public interface Command {


    /**
     * This method performs the relative command operation
     * @return whether command is successful or not
     */
    boolean execute();


    /**
     * This method performs a revert of the current command
     * @return whether reverse command is successful or not
     */
    boolean reverse();


    /**
     * Useful method for analyze a command
     * @param info
     */
    default void debug(String info) {
        System.out.println("\t\tIn command: " + this.getClass().getSimpleName());
        System.out.println("\t\tInfo: " + info);
    }
}
