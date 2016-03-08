package model;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by felipe on 03/03/16.
 */
public class Module {

    private String id;
    private String name;
    //private String description;
  //  private String type;
    private UUID templateId;
    private ArrayList<String> cparams;
    private Map<String, Value> parameters;

    private Module(UUID templateId) {
        this.templateId = templateId;
    }

    public Module getInstance(UUID templateId){
        Module module = new Module(templateId);


        
        return module;
    }




}
