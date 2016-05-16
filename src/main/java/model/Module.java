package model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 03/03/16.
 */
public class Module {

    //private String id;

    // Used as id
    private String name;
    private ModuleTemplate template;

    private ObservableList<SimpleStringProperty> cParams;

    private Map<String, Value> parameters;
    private Point2D position;

    private String host = "localhost";
    private SimpleBooleanProperty valid = new SimpleBooleanProperty(true);


    private Module(ModuleTemplate template) {
        this.template = template;
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


    public Map<String, Value> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Value> parameters) {


        Map<String, Value> templateParams = template.getParamsCopy();
        parameters.keySet().forEach(s -> {

            Value templateValue = templateParams.get(s);
            Value current = parameters.get(s);

            if (current.getType().equals("Long") || current.getType().equals("Integer")) {
                if ((templateValue != null && (templateValue.getType().equals("Long")) ||
                        templateValue.getType().equals("Integer"))) {
                    this.parameters.put(s, parameters.get(s));
                }
            } else if (current.getType().equals("Double") || current.getType().equals("Float")) {
                if ((templateValue != null && (templateValue.getType().equals("Double")) ||
                        templateValue.getType().equals("Float"))) {
                    this.parameters.put(s, parameters.get(s));
                }
            } else {
                if ((templateValue != null && templateValue.getType().equals(current.getType()))) {
                    this.parameters.put(s, parameters.get(s));
                }
            }
        });
    }

    @Override
    public String toString() {
        return "Module{" +
                "name='" + name + '\'' +
                ", template=" + template.getType() +
                ", parameters=" + parameters +
                '}';
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }


    public void addCParams(JSONArray jsonparams) {
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
