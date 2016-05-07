package utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.DataFormat;
import main.Main;
import model.Link;
import model.Module;
import model.ModuleTemplate;
import model.Value;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 23/03/16.
 */
public class Converter {
    public static DataFormat MODULES_DATA_FORMAT = new DataFormat("modules");
    public static DataFormat LINKS_DATA_FORMAT = new DataFormat("links");

    //TODO check for comment parameter

    public static Module jsonToModule(String name, JSONObject jsonObject) {

        Module module = null;

        String type = jsonObject.getString("type");
//        if(jsonObject.has("type"))
//            type = jsonObject.getString("type");

        Point2D position = null;
        Double x = null;
        Double y = null;
        if (jsonObject.has("#x"))
            x = jsonObject.getDouble("#x");
        if (jsonObject.has("#y"))
            y = jsonObject.getDouble("#y");


        if (x != null && y != null)
            position = new Point2D(x, y);



        ModuleTemplate template = Main.templates.get(type);

        if(template == null)
            return null;

        module = Module.getInstance(template);
        name = BadElements.checkDuplicateModules(name, 0);
        module.setName(name);
        if (position != null)
            module.setPosition(position);


        module.setParameters(extractParams(template, jsonObject));

        JSONArray jsonArray = null;
        if (jsonObject.has("cparams"))
            jsonArray = jsonObject.getJSONArray("cparams");

        if (jsonArray != null)
            module.addCParams(jsonArray);

        return module;
    }

    public static JSONObject moduleToJSON(Module module) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", module.getType());
        module.getParameters().keySet().forEach(key ->
                jsonObject.put(key, module.getParameters().get(key).getValue()));

        jsonObject.put("#x", module.getPosition().getX());
        jsonObject.put("#y", module.getPosition().getY());

        if (module.getcParams() != null) {
            JSONArray jsonArray = new JSONArray();
            module.getcParams().forEach(param -> jsonArray.put(param.get()));
            jsonObject.put("cparams", jsonArray);
        }

        return jsonObject;
    }

    public static ArrayList<JSONObject> linkToJSON(Link link) {
        ArrayList<JSONObject> jsonLinks = new ArrayList<>();

        link.getChannelsAToB().forEach(channel -> {
            JSONObject jsonLink = new JSONObject();
            jsonLink.put("from", link.getModuleA().getName());
            jsonLink.put("to", link.getModuleB().getName());
            if (!channel.getValue().equals("default"))
                jsonLink.put("channel", channel.getValue());
            jsonLinks.add(jsonLink);
        });

        link.getChannelsBToA().forEach(channel -> {
            JSONObject jsonLink = new JSONObject();
            jsonLink.put("from", link.getModuleB().getName());
            jsonLink.put("to", link.getModuleA().getName());
            if (!channel.getValue().equals("default"))
                jsonLink.put("channel", channel.getValue());
            jsonLinks.add(jsonLink);

        });
        return jsonLinks;
    }


    public static Link jsonToLink(JSONObject jsonLink, String fromName, String toName) {
        Link link = null;

        Module from = Main.modules.get(fromName);
        Module to = Main.modules.get(toName);

        String channel = null;
        if (jsonLink.has("channel"))
            channel = jsonLink.getString("channel");

        if (channel == null)
            channel = "default";

        if ((link = Main.links.get(from.getName() + "-" + to.getName())) != null) {
            //System.out.println("FromTo " + channel);
            //  link.addChannel("fromTo",new SimpleStringProperty(channel));
            link.addChannel(from, to, new SimpleStringProperty(channel));
            return link;
        }

        if ((link = Main.links.get(to.getName() + "-" + from.getName())) != null) {
            //System.out.println("ToFrom " + channel);

            // link.addChannel("toFrom",new SimpleStringProperty(channel));
            link.addChannel(from, to, new SimpleStringProperty(channel));
            return link;
        }

        link = new Link(from, to, channel);
        //System.out.println("FromTo " + channel);
        return link;
    }


    private static Map<String, Value> extractParams(ModuleTemplate template, JSONObject jsonObject) {
        Map<String, Value> params = new HashMap<>();
        // Mandatory params extraction

        //System.out.println("Converter: " + template.getType());  // TODO remove this
        if (!template.getMandatoryParameters().isEmpty())
            template.getMandatoryParameters().keySet().forEach(key -> {
                boolean validValue = false;
                Value value = new Value(template.getMandatoryParameters().get(key));
                Object val = null;
                if (jsonObject.has(key))
                    val = jsonObject.get(key);

                if (val != null) {
                    value.setValue(val);
                    validValue = true;
                }
                value.setValid(validValue);
                params.put(key, value);
            });

        // Optional params extraction
        if (!template.getOptParameters().isEmpty())
            // Skips parameters not present
            template.getOptParameters().keySet().forEach(key -> {
                boolean validValue = false;
                Value value = new Value(template.getOptParameters().get(key));
                Object val = null;
                if (jsonObject.has(key))
                    val = jsonObject.get(key);

                if (val != null) {
                    value.setValue(val);
                    validValue = true;
                }
                value.setValid(validValue);
                params.put(key, value);
            });


        return params;
    }

    public static void populateClipBoards(Map<String, Module> modules, Map<String, Link> links) {
        Main.modulesClipboard.clear();
        Main.linksClipboard.clear();
        //System.out.println("Before populating modules");

        modules.keySet().forEach(key -> {
          //  System.out.println("Populating module");
            Module current = modules.get(key);
            //System.out.println(current);
            Main.modulesClipboard.put(current.getName(), moduleToJSON(current));

        });
        //System.out.println("Before Populating links: " + links.size());

        links.keySet().forEach(key -> {
            Link current = links.get(key);
            ArrayList<JSONObject> jsonLinks = linkToJSON(current);
          //  System.out.println("json links size: " + jsonLinks.size());
            jsonLinks.forEach(jsonLink -> {
            //    System.out.println("Populating links");


                String channel = "default";

                if (jsonLink.has("channel"))
                    channel = jsonLink.getString("channel");


                Main.linksClipboard.put("" + jsonLink.get("from") + jsonLink.get("to") + channel, jsonLink);
            });
        });
    }

    /**
     * Method for generate a pipeline string from modules
     * Better checkMatching before calling this method.
     *
     * @param pipelineModules
     * @param pipelineLinks
     * @return
     */
    public static String getPipelineString(JSONObject pipelineModules, JSONArray pipelineLinks) {
        StringBuffer pipelineStringBuffer = new StringBuffer();
        JSONObject pipeline = new JSONObject();
        pipeline.put("links", pipelineLinks);
        pipeline.put("modules", pipelineModules);
        pipelineStringBuffer.append("{\n \"modules\" : ");
        pipelineStringBuffer.append(pipelineModules.toString(4));
        pipelineStringBuffer.append(",\n \"links\" : ");
        pipelineStringBuffer.append(pipelineLinks.toString(4));
        pipelineStringBuffer.append("\n}");


//        pipelineStringBuffer.append(pipelineModules.toString());
//        pipelineStringBuffer.append(",\n\t\"links\" : ");
//        pipelineStringBuffer.append(pipelineLinks.toString());
//        pipelineStringBuffer.append("\n}");

        return pipelineStringBuffer.toString();
        //return pipeline.toString(4);
    }


    public static boolean checkMatching(JSONObject pipelineModules, JSONArray pipelineLinks) {

        for (Object pipelineLink : pipelineLinks) {
            JSONObject link = (JSONObject) pipelineLink;
            if (!(pipelineModules.has(link.get("from").toString()) && pipelineModules.has(link.get("to").toString()))) ;
            return false;
        }

        return true;
    }


}
