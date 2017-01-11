package commands;

import controller.MainWindow;
import model.Value;

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
            MainWindow.stackedLogBar.logAndWarning("Operation not permitted! insert a correct value");
            return false;
        } else {
            MainWindow.stackedLogBar.logAndSuccess("Value " +value.getName() +" successful updated");
            return true;
        }
    }

    @Override
    public boolean reverse() {
        EditValue reverse = new EditValue(value, oldValue);
        return reverse.execute();
    }
}
