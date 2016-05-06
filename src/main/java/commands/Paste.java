package commands;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import org.json.simple.JSONObject;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 23/03/16.
 */
public class Paste implements Command {

    private ClipboardContent clipboard;
    public Map<String, JSONObject> modules = new HashMap<>();
    public Map<String, JSONObject> links = new HashMap<>();


    public Paste(ClipboardContent clipboard, Point2D translation) {

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
