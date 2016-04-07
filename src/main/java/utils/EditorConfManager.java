package utils;

import main.Main;
import model.ModuleTemplate;
import model.Value;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 10/03/16.
 */
public class EditorConfManager {


    public boolean load(File input) {
        FileReader fileReader = null;
        JSONParser parser = new JSONParser();
        JSONArray jsonModules = new JSONArray();

        try {
            fileReader = new FileReader(input);
            jsonModules = (JSONArray) parser.parse(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonModules.forEach(obj -> {
            JSONObject object = (JSONObject) obj;

            ModuleTemplate moduleTemplate = ModuleTemplate.getInstance();
            Map<String, Value> optParameters = new HashMap<>();
            Map<String, Value> mandatoryParameters = new HashMap<>();

            // String name = (String) object.get("name");
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

            Main.templates.put(moduleTemplate.getType(), moduleTemplate);

        });

        return true;
    }

    private void getParameters(Map<String, Value> map, JSONArray params, boolean isMandatory) {
        // TODO add to conf.json description of parameters and parse them here

        for (int j = 0; j < params.size(); j++) {
            JSONObject param = (JSONObject) params.get(j);
            String paramName = (String) param.get("name");
            String paramType = (String) param.get("type");
            Value value;
            boolean defaultValue = false;

            Object val = null;

            try {
                Constructor ctor = Class.forName("java.lang." + paramType).getConstructor(String.class);
                val = ctor.newInstance("0");
                //val = Class.forName("java.lang." + paramType);

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
            if (param.get("default") != null) {
                val = param.get("default");

            }

            value = new Value(paramName, val, false, isMandatory);


            map.put(paramName, value);
        }

    }

}
