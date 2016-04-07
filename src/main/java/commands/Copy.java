package commands;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 23/03/16.
 */
public class Copy implements Command {

    public Map<String, JSONObject> modulesClipboard = new HashMap<>();
    public Map<String, JSONObject> linksClipboard = new HashMap<>();

    public Copy() {

    }

    @Override
    public void execute() {
       // Clipboard systemClipBoard = Clipboard.getSystemClipboard();
       // systemClipBoard.setContent(clipboard);
    }

    @Override
    public void revert() {

    }
}
