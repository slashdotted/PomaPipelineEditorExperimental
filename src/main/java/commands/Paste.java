package commands;

import javafx.scene.input.ClipboardContent;

/**
 * Created by felipe on 23/03/16.
 */
public class Paste implements Command {

    private ClipboardContent clipboard;

    public Paste(ClipboardContent clipboard) {
        this.clipboard = clipboard;
    }

    @Override
    public boolean execute() {

        return true;
    }

    @Override
    public boolean reverse() {
        return false;
    }

}
