package commands;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Created by felipe on 23/03/16.
 */
public class Copy implements Command {

    ClipboardContent clipboard;

    public Copy(ClipboardContent clipboard) {
        this.clipboard = clipboard;
    }

    @Override
    public void execute() {
        Clipboard systemClipBoard = Clipboard.getSystemClipboard();
        systemClipBoard.setContent(clipboard);
    }
}
