package utils;

import com.sun.org.apache.xpath.internal.operations.Mod;
import main.Main;
import model.Link;
import model.Module;
import model.ModuleTemplate;
import model.Value;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 23/03/16.
 */
public class Converter {

    //TODO check for comment parameter

    public static Module jsonToModule(String name, JSONObject jsonObject) {

        Module module = null;
        String type = (String) jsonObject.get("type");

        //System.out.println(type); // TODO remove this
        ModuleTemplate template = Main.templates.get(type);

        module = Module.getInstance(template);
        module.setName(name);

        module.setParameters(extractParams(template, jsonObject));

        JSONArray jsonArray = (JSONArray) jsonObject.get("cparams");

        module.setCparams(jsonArray);

        return module;
    }

    public static JSONObject moduleToJSON(Module module) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", module.getType());
        module.getParameters().keySet().parallelStream().forEach(key ->
                jsonObject.put(key, module.getParameters().get(key).getValue()));

        if (module.getCparams() != null) {
            JSONArray jsonArray = new JSONArray();
            module.getCparams().parallelStream().forEach(param -> jsonArray.add(param));
            jsonObject.put("cparams", jsonArray);
        }


        return jsonObject;
    }


    //    public static JSONObject linkToJSON(Link link) {
//        JSONObject jsonObject = new JSONObject();
//
//        jsonObject.put("from", link.getModuleA().getName());
//        jsonObject.put("to", link.getModuleB().getName());
//
//        if (!link.getChannel().equals("default"))
//            jsonObject.put("channel", link.getChannel());
//
//        return jsonObject;
//    }
    public static ArrayList<JSONObject> linkToJSON(Link link) {
        ArrayList<JSONObject> jsonLinks = new ArrayList<>();

        link.getChannelsAToB().forEach(channel -> {
            JSONObject jsonLink = new JSONObject();
            jsonLink.put("from", link.getModuleA().getName());
            jsonLink.put("to", link.getModuleB().getName());
            if (!channel.equals("default"))
                jsonLink.put("channel", channel);
        });

        link.getChannelsBToA().forEach(channel -> {
            JSONObject jsonLink = new JSONObject();
            jsonLink.put("from", link.getModuleB().getName());
            jsonLink.put("to", link.getModuleA().getName());
            if (!channel.equals("default"))
                jsonLink.put("channel", channel);
        });
        return jsonLinks;
    }


    public static Link jsonToLink(JSONObject jsonLink) {
        Link link = null;

        Module from = Main.modules.get(jsonLink.get("from"));
        Module to = Main.modules.get(jsonLink.get("to"));

        String channel = (String) jsonLink.get("channel");
        if (channel == null)
            channel = "default";

        if ((link = Main.links.get(from.getName() + "-" + to.getName())) != null) {
            link.addChannel(from, to, channel);
            return link;
        }

        if ((link = Main.links.get(to.getName() + "-" + from.getName())) != null) {
            link.addChannel(from, to, channel);
            return link;
        }

        link = new Link(from, to, channel);
        return link;
    }


    public static Link jsonToLinkOLD(JSONObject jsonLink) {
        Link link = null;

        Module from = Main.modules.get(jsonLink.get("from"));

        Module to = Main.modules.get(jsonLink.get("to"));
        String channel = (String) jsonLink.get("channel");

        link = new Link(from, to, channel);

        return link;
    }

    private static Map<String, Value> extractParams(ModuleTemplate template, JSONObject jsonObject) {
        Map<String, Value> params = new HashMap<>();
        // Mandatory params extraction

        //System.out.println("Converter: " + template.getType());  // TODO remove this
        if (!template.getMandatoryParameters().isEmpty())
            template.getMandatoryParameters().keySet().parallelStream().forEach(key -> {
                Value value = new Value(template.getMandatoryParameters().get(key));
                value.setValue(jsonObject.get(key));
                params.put(key, value);
            });

        // Optional params extraction
        if (!template.getOptParameters().isEmpty())
            // Skips parameters not present
            template.getOptParameters().keySet().parallelStream().filter(key -> jsonObject.get(key) != null).forEach(key -> {
                Value value = new Value(template.getOptParameters().get(key));
                value.setValue(jsonObject.get(key));
                params.put(key, value);
            });


        return params;
    }


}
