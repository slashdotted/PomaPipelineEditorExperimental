package commands;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 02/05/2016.
 */
public class RemoveStringProperty implements Command{

    private SimpleStringProperty oldValue;
       private List<SimpleStringProperty> motherStructure;

    public RemoveStringProperty(SimpleStringProperty oldValue, List<SimpleStringProperty> motherStructure) {
        this.oldValue = oldValue;
        this.motherStructure = motherStructure;
    }

    @Override
    public boolean execute() {
        motherStructure.remove(oldValue);
        return false;
    }

    @Override
    public boolean reverse() {
        Command addStringProp=new AddStringProperty(oldValue,motherStructure);
        return addStringProp.execute();
    }
}
