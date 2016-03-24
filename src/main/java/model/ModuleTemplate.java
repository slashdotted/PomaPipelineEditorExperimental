package model;

import javafx.scene.image.Image;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by felipe on 08/03/16.
 */
public class ModuleTemplate {
    public static final String DEFAULT_TEMPLATE_IMAGE_URL = "moduleImage.png";

   // private UUID id;
  //  private String name;

    private int counter;
    private String description;
    private String type;
    private Map<String, Value> optParameters;
    private Map<String, Value> mandatoryParameters;
    //private Map<String, Value> outputData;
    private String imageURL = DEFAULT_TEMPLATE_IMAGE_URL;
    //TODO: insert cparams field in the instance configuration

    //public ModuleTemplate

    private ModuleTemplate() {

        this.counter=0;

      //  this.id = UUID.randomUUID();

        //this.optParameters = new HashMap<>();
        //this.mandatoryParameters = new HashMap<>();
    }

    public static ModuleTemplate getInstance() {
        ModuleTemplate template = new ModuleTemplate();
        return template;
    }




    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL){
        if (ClassLoader.getSystemResource(imageURL)!=null)
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


    public int getCounter() {
        return this.counter++;
    }
}
