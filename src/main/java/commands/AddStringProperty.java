package commands;

import javafx.beans.property.SimpleStringProperty;

import java.util.List;

/**
 * Command for add a StringProperty to a list
 */
public class AddStringProperty implements Command {
    private SimpleStringProperty newValue;
    private List<SimpleStringProperty> motherStructure;

    public AddStringProperty(SimpleStringProperty newValue, List<SimpleStringProperty> motherStructure) {
        this.newValue = newValue;
        this.motherStructure = motherStructure;
    }

    @Override
    public boolean execute() {
        motherStructure.add(newValue);
        return false;
    }

    @Override
    public boolean reverse() {
        Command addStringProp=new RemoveStringProperty(newValue,motherStructure);
        return addStringProp.execute();
    }
}
