package commands;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import controller.DraggableModule;
import controller.LinkView;
import controller.MainWindow;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import main.Main;
import model.Link;
import model.Module;
import model.ModuleTemplate;
import utils.BadElements;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Marco on 30/04/2016.
 */
public class EditModule implements Command {

    public static enum Type {Name, Template, Host}

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

    // TODO: edit name(id)? edit template? edit host?
    @Override
    public boolean execute() {
        switch (typeEdit) {

            case Name:
                oldValue = module.getName();
                MainWindow.allDraggableModule.get(oldValue).unselect();
                newValue = BadElements.checkDuplicateModules(newValue, 0);
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
        debug("module = "+ module.getName()+ ", type = " + typeEdit);
        return true;
    }

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

    private void updateDraggableModule() {
        DraggableModule dm = MainWindow.allDraggableModule.get(oldValue);
        dm.unselect();
        MainWindow.allDraggableModule.remove(oldValue);
        MainWindow.allDraggableModule.put(module.getName(), dm);
        dm.updateName();
        dm.select();

    }

    private void updateModule() {
        Main.modules.remove(oldValue);
        module.setName(newValue);
        Main.modules.put(module.getName(), module);
    }

    private void updateLinks() {
        DraggableModule db = MainWindow.allDraggableModule.get(oldValue);
        ArrayList<LinkView> allLv = db.getLinks();
        for (LinkView lv : allLv) {
            String nomeFromModule = lv.getFrom().getName();
            String nomeToModule = lv.getFrom().getName();

            if (nomeFromModule.equals(oldValue)) {
                MainWindow.allLinkView.remove(lv.getName());
                MainWindow.allLinkView.put(newValue + "_" + nomeToModule, lv);

                Main.links.remove(lv.getLink());
                Main.links.put(newValue + "_" + nomeToModule, lv.getLink());
            }
            if (nomeToModule.equals(oldValue)) {
                MainWindow.allLinkView.remove(lv.getName());
                MainWindow.allLinkView.put(nomeFromModule + "_" + newValue, lv);

                Main.links.remove(lv.getLink());
                Main.links.put(nomeFromModule + "_" + newValue, lv.getLink());
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
