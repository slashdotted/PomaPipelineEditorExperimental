package commands;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Marco on 30/04/2016.
 */
public class EditStringProperty implements Command {

    private String oldValue;
    private String newValue;
    private SimpleStringProperty strProperty;


    public EditStringProperty( SimpleStringProperty strProperty, String newValue) {

        this.oldValue = strProperty.getValue();
        this.newValue = newValue;
        this.strProperty = strProperty;
    }

    @Override
    public boolean execute() {

        strProperty.setValue(newValue);
        return true;
    }

    @Override
    public boolean reverse() {
        Command undoEditStrProp=new EditStringProperty(strProperty, oldValue);
        return undoEditStrProp.execute();
    }
}
