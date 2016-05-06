package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 03/03/16.
 */
public class Module {

    //private String id;

    // Used as id
    private String name;
    //private String description;
    //  private String type;
    private ModuleTemplate template;
    // private ArrayList<String> cParams;
    private ObservableList<SimpleStringProperty> cParams;

    private Map<String, Value> parameters;
    private Point2D position;

    private String host = "localhost";
    private SimpleBooleanProperty valid=new SimpleBooleanProperty(true);


    private Module(ModuleTemplate template) {
        this.template = template;
        //this.name=template.getNameInstance();
        //this.cParams = new ArrayList<>();
        this.cParams = FXCollections.observableArrayList();
        this.parameters = new HashMap<>();
    }

    public static Module getInstance(ModuleTemplate template) {

        Module module = new Module(template);
        module.cloneParams();
        return module;
    }


    public String getType() {
        return template.getType();
    }

    public ModuleTemplate getTemplate() {
        return template;
    }

    public void setTemplate(ModuleTemplate template) {
        this.template = template;
    }
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

//


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObservableList<SimpleStringProperty> getcParams() {
        return cParams;
    }

    public void setcParams(ObservableList<SimpleStringProperty> cParams) {
        this.cParams = cParams;
    }

    //    public ArrayList<String> getCParams() {
//        return cParams;
//    }
//
//    public void setCParams(ArrayList<String> cParams) {
//        this.cParams = cParams;
//    }

    public Map<String, Value> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Value> parameters) {
        this.parameters.clear();
        parameters.keySet().forEach(s -> {
            Value mandatory = template.getMandatoryParameters().get(s);
            Value optional =  template.getOptParameters().get(s);
            Value current = parameters.get(s);


            if ((mandatory != null && mandatory.getType().equals(current.getType())) ||
                    optional !=null && optional.getType().equals(current.getType())) {
                this.parameters.put(s, parameters.get(s));
            }
        });
    }

    @Override
    public String toString() {
        return "Module{" +
                "name='" + name + '\'' +
                ", template=" + template.getType() +
                '}';
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public void addCParams(ArrayList<String> cparams) {
        cparams.forEach(s -> this.cParams.add(new SimpleStringProperty(s)));
    }

    public void addCParams(JSONArray jsonparams){
        for (int i = 0; i < jsonparams.length(); i++) {
            this.cParams.add(new SimpleStringProperty(jsonparams.getString(i)));
        }

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void cloneParams() {
        this.parameters = template.getParamsCopy();
    }

    public void setValid(boolean valid) {

        this.valid.setValue(valid);
    }
    public SimpleBooleanProperty getValid() {
        return this.valid;
    }

    public boolean isValid() {
        return valid.getValue();
    }
}
