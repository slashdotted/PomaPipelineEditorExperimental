package controller;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import main.Main;

/**
 * Created by Marco on 06/05/2016.
 */
public class ContextualMenu {

    private final ContextMenu contextMenu = new ContextMenu();
    private MenuItem pasteIt = new MenuItem("Paste");
    private MenuItem selectAllIt = new MenuItem("Select All");
    private MenuItem importIt = new MenuItem("Import");
    private static MouseEvent mouse;

    /**
     * Contextual menu that allows to paste or select all
     */
    public ContextualMenu() {
        contextMenu.getItems().add(pasteIt);
        contextMenu.getItems().add(selectAllIt);

        selectAllIt.setOnAction(event -> Main.root.selectAll());

        pasteIt.setOnAction(e -> {
            Main.root.paste();

        });


    }

    public void showContextMenu(MouseEvent event) {
        MainWindow.mainScrollPaneStat.setContextMenu(contextMenu);
        contextMenu.show(MainWindow.mainScrollPaneStat, event.getScreenX(), event.getScreenY());
        MainWindow.mainScrollPaneStat.setContextMenu(null);
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public void setMouse(MouseEvent mouse) {

        this.mouse = mouse;
    }
}
