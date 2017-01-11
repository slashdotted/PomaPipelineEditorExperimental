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
                oldValue = MainWindow.getSource();
                DraggableModule dms = MainWindow.allDraggableModule.get(module.getName());
                MainWindow.setSource(newValue);
                dms.updateSourceIcon();
                break;
            case Name:
                oldValue = module.getName();
                MainWindow.allDraggableModule.get(oldValue).unselect();
                newValue = ProgramUtils.checkDuplicateModules(newValue, 0);
                MainWindow.allDraggableModule.get(oldValue).unselect();
                if (MainWindow.isSource(oldValue)) {
                    MainWindow.setSource(newValue);
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
                MainWindow.allDraggableModule.get(module.getName()).updateModule();
                MainWindow.allDraggableModule.get(module.getName()).updateName();
                MainWindow.allDraggableModule.get(module.getName()).updateType();
                this.module = newModule;

                break;
            case Host:
                oldValue = module.getHost();
                module.setHost(newValue);
                DraggableModule dm = MainWindow.allDraggableModule.get(module.getName());
                dm.updateHost();
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
        DraggableModule dm = MainWindow.allDraggableModule.get(oldValue);
        dm.unselect();
        MainWindow.allDraggableModule.remove(oldValue);
        MainWindow.allDraggableModule.put(module.getName(), dm);
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
        DraggableModule db = MainWindow.allDraggableModule.get(oldValue);
        ArrayList<LinkView> allLv = db.getLinks();
        for (LinkView lv : allLv) {
            String moduleName = lv.getFrom().getName();
            String nomeToModule = lv.getFrom().getName();

            if (moduleName.equals(oldValue)) {
                MainWindow.allLinkView.remove(lv.getName());
                MainWindow.allLinkView.put(newValue + "_" + nomeToModule, lv);

                Main.links.remove(lv.getLink());
                Main.links.put(newValue + "_" + nomeToModule, lv.getLink());
            }

            if (nomeToModule.equals(oldValue)) {
                MainWindow.allLinkView.remove(lv.getName());
                MainWindow.allLinkView.put(moduleName + "_" + newValue, lv);

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
