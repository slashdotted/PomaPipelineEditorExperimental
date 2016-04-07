package model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private ArrayList<String> cparams;
    private Map<String, Value> parameters;
    private Point2D position=new Point2D (0.0, 0.0);;


    private Module(ModuleTemplate template){
        this.template =template;
        //this.name=template.getNameInstance();
        //TODO init cparams and parameters

    }
    public static Module getInstance(ModuleTemplate template){
        Module module = new Module(template);


        
        return module;
    }


    public String getType(){
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


    public ArrayList<String> getCparams() {
        return cparams;
    }

    public void setCparams(ArrayList<String> cparams) {
        this.cparams = cparams;
    }

    public Map<String, Value> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Value> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "Module{" +
                "name='" + name + '\'' +
                ", template=" + template.getType()+
                '}';
    }
    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

}
