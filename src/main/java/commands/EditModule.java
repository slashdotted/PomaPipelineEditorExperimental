package commands;

import controller.DraggableModule;
import controller.LinkView;
import controller.MainWindow;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import main.Main;
import model.Module;
import model.ModuleTemplate;
import utils.ProgramUtils;

import java.util.ArrayList;

/**
 * Command for module edit of:
 * - name
 * - template
 * - host
 */
public class EditModule implements Command {

    public enum Type {Name, Template, Host, Source}

    private Type typeEdit;
    private Module oldModule;

    private Module module;
    private String oldValue;
    private String newValue;

    public EditModule(Module module, Type typeEdit, String newValue) {

        if (typeEdit.equals(Type.Template))
            this.oldModule = module;

        this.typeEdit = typeEdit;
        this.module = module;
        this.newValue = newValue;
    }


    @Override
    public boolean execute() {
        switch (typeEdit) {
            case Source:
                oldValue = Module.getSource();
                DraggableModule dms = MainWindow.instance().getModuleByName(module.getName());
                Module.setSource(newValue);
                dms.updateSourceIcon();
                break;
            case Name:
                oldValue = module.getName();
                MainWindow.instance().getModuleByName(oldValue).unselect();
                newValue = ProgramUtils.checkDuplicateModules(newValue, 0);
                if (Module.isSource(oldValue)) {
                    Module.setSource(newValue);
                }
                updateLinks();
                updateModule();
                updateDraggableModule();
                break;
            case Template:
                Module newModule = createNewModule();
                if (newModule == null)
                    return false;
                Main.modules.put(module.getName(), newModule);
                DraggableModule dm = MainWindow.instance().getModuleByName(module.getName());
                dm.updateModule();
                dm.updateName();
                dm.updateType();
                this.module = newModule;

                break;
            case Host:
                oldValue = module.getHost();
                module.setHost(newValue);
                DraggableModule dm2 = MainWindow.instance().getModuleByName(module.getName());
                dm2.updateHost();
                break;
        }
        return true;
    }

    /**
     * This method returns a new module from the given new template
     * @return  a new module from the given new template
     */
    private Module createNewModule() {

        ModuleTemplate template = Main.templates.get(newValue);

        if (template == null)
            return null;
        Module newModule = Module.getInstance(template);
        newModule.setName(module.getName());
        newModule.setParameters(module.getParameters());
        newModule.setPosition(new Point2D(module.getPosition().getX(), module.getPosition().getY()));
        newModule.setcParams(FXCollections.observableArrayList(module.getcParams()));
        newModule.setHost(module.getHost());
        return newModule;

    }

    /**
     * This method updates the draggable module associated to renamed module
     */
    private void updateDraggableModule() {
        DraggableModule dm = MainWindow.instance().getModuleByName(oldValue);
        dm.unselect();
        dm.updateName();
        dm.select();
    }

    /**
     * This method is necessary for update the module model structure
     */
    private void updateModule() {
        Main.modules.remove(oldValue);
        module.setName(newValue);
        Main.modules.put(module.getName(), module);
    }

    private void updateLinks() {
        DraggableModule db = MainWindow.instance().getModuleByName(oldValue);
        ArrayList<LinkView> allLv = db.getLinks();
        for (LinkView lv : allLv) {
            String moduleName = lv.getFrom().getName();
            String nomeToModule = lv.getFrom().getName();

            if (moduleName.equals(oldValue)) {
                Main.links.remove(lv.getLink());
                Main.links.put(newValue + "_" + nomeToModule, lv.getLink());
            }

            if (nomeToModule.equals(oldValue)) {
                Main.links.remove(lv.getLink());
                Main.links.put(moduleName + "_" + newValue, lv.getLink());
            }
        }
    }

    @Override
    public boolean reverse() {
        Command reverse = null;
        if (typeEdit.equals(Type.Template)) {
            module.setTemplate(oldModule.getTemplate());
            oldValue = oldModule.getTemplate().getType();
            reverse = new EditModule(oldModule, typeEdit, oldValue);
        }else{
            reverse = new EditModule(module, typeEdit, oldValue);
        }

        return reverse.execute();
    }
}
