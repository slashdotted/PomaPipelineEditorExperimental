package commands;

import controller.MainWindow;
import model.Module;
import model.Value;

/**
 * Created by felipe on 28/04/16.
 */
public class EditValue implements Command {

    private Value value;
    private String oldValue;
    private String newValue;

    public EditValue(Value value, String newValue) {
        this.value = value;
        this.oldValue = value.getValue().toString();
        this.newValue = newValue;
    }

    @Override
    public boolean execute() {
        return value.updateValue(newValue);
    }

    @Override
    public boolean reverse() {
        EditValue reverse = new EditValue(value, oldValue);
        return reverse.execute();
    }
}
