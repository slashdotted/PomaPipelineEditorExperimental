package commands;

import controller.MainWindow;
import model.Value;
import utils.StackedLogBar;

/**
 *  Command for edit a Value
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
        String updatedValue = value.updateValue(newValue);
        value.validate();
        if(!updatedValue.equals(newValue)) {
            StackedLogBar.instance().logAndWarning("Operation not permitted! insert a correct value");
            return false;
        } else {
            StackedLogBar.instance().logAndSuccess("Value " +value.getName() +" successful updated");
            return true;
        }
    }

    @Override
    public boolean reverse() {
        EditValue reverse = new EditValue(value, oldValue);
        return reverse.execute();
    }
}
