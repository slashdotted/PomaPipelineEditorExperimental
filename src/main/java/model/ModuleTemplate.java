package model;

import java.util.Map;
import java.util.UUID;

/**
 * Created by felipe on 08/03/16.
 */
public class ModuleTemplate {
    private UUID id;
    private String name;

    private String description;
    private String type;
    private Map<String, Value> optParameters;
    private Map<String, Value> mandatoryParameters;
    private String imageURL;
    //TODO: insert cparams field in the instance configuration

    //public ModuleTemplate

    private ModuleTemplate() {

        this.id = UUID.randomUUID();
    }

    public static ModuleTemplate getInstance() {
        ModuleTemplate template = new ModuleTemplate();


        //TODO: load from configuration JSON

        return template;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Value> getOptParameters() {
        return optParameters;
    }

    public void setOptParameters(Map<String, Value> optParameters) {
        this.optParameters = optParameters;
    }

    public Map<String, Value> getMandatoryParameters() {
        return mandatoryParameters;
    }

    public void setMandatoryParameters(Map<String, Value> mandatoryParameters) {
        this.mandatoryParameters = mandatoryParameters;
    }


}
