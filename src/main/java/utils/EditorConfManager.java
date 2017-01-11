package utils;

import controller.MainWindow;
import java.io.BufferedReader;
import main.Main;
import model.ModuleTemplate;
import model.Value;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for the editor conf management
 * It performs the retrieving of templates configuration from the conf file
 * Parameters are represented by Value items, constructed by reflection
 */
public class EditorConfManager {


    public boolean load(File input) {
        FileReader fileReader = null;
        JSONTokener parser;
        JSONArray jsonModules = null;
        
        InputStream is;
        is = ClassLoader.getSystemResourceAsStream("data/conf_final.json");
        InputStreamReader isr;
        isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        //fileReader = new FileReader(input);
        parser = new JSONTokener(br);
        jsonModules = new JSONArray(parser);

        if (jsonModules == null) {
            MainWindow.stackedLogBar.logAndWarning("There was a problem while importing conf!!!");
            return false;
        }
        jsonModules.forEach(obj -> {
            JSONObject object = (JSONObject) obj;

            ModuleTemplate moduleTemplate = ModuleTemplate.getInstance();
            Map<String, Value> optParameters = new HashMap<>();
            Map<String, Value> mandatoryParameters = new HashMap<>();

            String description = (String) object.get("description");
            String type = (String) object.get("type");
            String imageURL = (String) object.get("imageURL");
            
           

            JSONArray jsonOptParams = (JSONArray) object.get("optParams");
            getParameters(optParameters, jsonOptParams, false);

            JSONArray jsonMndParams = (JSONArray) object.get("mandatoryParams");
            getParameters(mandatoryParameters, jsonMndParams, true);

            moduleTemplate.setMandatoryParameters(mandatoryParameters);
            moduleTemplate.setOptParameters(optParameters);

            moduleTemplate.setDescription(description);
            moduleTemplate.setType(type);
            moduleTemplate.setImageURL(imageURL);
            
            moduleTemplate.setCanBeSource(object.optBoolean("canBeSource"));

            Main.templates.put(moduleTemplate.getType(), moduleTemplate);

        });

        return true;
    }

    private void getParameters(Map<String, Value> map, JSONArray params, boolean isMandatory) {
        // TODO add to conf.json description of parameters and parse them here

        for (int j = 0; j < params.length(); j++) {
            JSONObject param = (JSONObject) params.get(j);
            String paramName = (String) param.get("name");
            String paramType = (String) param.get("type");
            String paramDesc = (String) param.get("description");
            Value value;
            boolean defaultValue = false;
            Object val = null;

            try {
                Constructor ctor = Class.forName("java.lang." + paramType).getConstructor(String.class);
                val = ctor.newInstance("0");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            if (param.has("default")) {
                defaultValue = true;
                val = param.get("default");
            }

            if (defaultValue)
                value = new Value(paramName, paramDesc, val, val, isMandatory);
            else
                value = new Value(paramName, paramDesc, val, null, isMandatory);

            map.put(paramName, value);
        }
    }
}
