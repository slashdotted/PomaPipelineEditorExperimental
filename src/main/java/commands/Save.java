package commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.Map;

/**
 * Created by felipe on 23/03/16.
 */
public class Save implements Command {

    private File output;
    private Map<String, JSONObject> modules;
    private Map<String, JSONObject> links;

    public Save(File output, Map<String, JSONObject> modules, Map<String, JSONObject> links) {
        this.output = output;
        this.modules = modules;
        this.links = links;
    }

    @Override
    public void execute() {

        JSONObject pipeline = new JSONObject();

        JSONObject pipelineModules = new JSONObject();
        JSONArray pipelineLinks = new JSONArray();

        pipelineModules.putAll(modules);
        // modules.keySet().forEach(key -> {


        pipelineLinks.addAll(links.values());

        System.out.println("Saved modules: " + pipelineModules.size());
       // System.out.println(pipelineModules.toJSONString());
       // System.out.println("Saved links: "  + pipelineLinks.toJSONString());
        System.out.println("Saved links: ");
        links.keySet().forEach(key->{
            System.out.println(key + " : " + links.get(key));
        });
//        pipelineLinks.forEach(link ->{
//            System.out.println(link);
//        });
//        pipelineModules.keySet().forEach(key -> {
//            System.out.println(key + " : " + ((JSONObject)pipelineModules.get(key)).toJSONString());
//        });

//        modules.keySet().forEach(key ->{
//            pipelineModules.put(key, modules.get());
//        });

    }

    @Override
    public void revert() {
        // Nothing to do here
    }
}
