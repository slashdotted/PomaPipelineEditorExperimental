package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import main.Main;

/**
 * Created by Marco on 06/05/2016.
 */
public class ContextualMenu {

    private final ContextMenu contextMenu = new ContextMenu();
    private MenuItem pasteIt = new MenuItem("Paste");
    private MenuItem importIt =new MenuItem("Import");
    private static MouseEvent mouse;

    public ContextualMenu() {

        pasteIt.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {

                MainWindow.paste(mouse);


                System.out.println("Arrivo a paste");
            }
        });

        contextMenu.getItems().add(pasteIt);


/*
        MainWindow.mainScrollPaneStat.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {

                            if (event.getButton() == MouseButton.SECONDARY) {
                                System.out.println("faccio vedere");
                                        contextMenu.show(MainWindow.mainScrollPaneStat, event.getScreenX(), event.getScreenY());
                            }
                      }
                });
*/

    }

    public void showContextMenu(MouseEvent event){
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
