package model;

import java.util.HashMap;
import java.util.Map;


/**
 * Module Template Class
 */
public class ModuleTemplate {
    public static final String DEFAULT_TEMPLATE_IMAGE_URL = "moduleImage.png";


    private int counter;
    private String description;
    private String type;
    private Map<String, Value> optParameters;
    private Map<String, Value> mandatoryParameters;
    private boolean cansource;

    private String imageURL = DEFAULT_TEMPLATE_IMAGE_URL;
    private String cparams;

    private ModuleTemplate() {

        this.counter = 0;

    }
    
    public void setCanBeSource(boolean value) {
        cansource = value;
    }

    public static ModuleTemplate getInstance() {
        ModuleTemplate template = new ModuleTemplate();
        return template;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        if (ClassLoader.getSystemResource(imageURL) != null)
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

    public String getNameInstance() {
        return this.type.toLowerCase() + "_" + this.counter++;
    }



    @Override
    public String toString() {
        return "ModuleTemplate{" +
                " counter=" + counter +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", imageURL='" + imageURL + '\'' +
                " }";
    }

    public Map<String, Value> getParamsCopy() {

        Map<String, Value> paramsCopy = new HashMap<>();
        mandatoryParameters.keySet().forEach(key -> paramsCopy.put(key, new Value(mandatoryParameters.get(key))));

        optParameters.keySet().forEach(key -> paramsCopy.put(key, new Value(optParameters.get(key))));

        return paramsCopy;
    }

    boolean getCanBeSource() {
        return cansource;
    }

    public void setCParams(String p) {
        cparams = p;
    }
    
    public String getCParams() {
        return cparams;
    }
}
