package utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Date;

public class StackedLogBar extends VBox {

    public static final int MODIFIED = 100;
    public static final int CREATED = 200;
    public static final int DELETED = 300;
    private LogBar logBar;
    ToolBar logToolbar = new ToolBar();
    Text logBarText = new Text("Event Log");
    Button clear = new Button();
    Button toggleVisibility = new Button();

    public StackedLogBar() {

        this.setPrefHeight(150);
        this.setMinHeight(25);
        this.setMaxHeight(150);
        this.setFillWidth(true);


        ImageView hideImg = new ImageView("images/hide_history.png");
        hideImg.setFitWidth(20);
        hideImg.setFitHeight(20);
        ImageView showImg = new ImageView("images/show_history.png");
        showImg.setFitWidth(20);
        showImg.setFitHeight(20);
        toggleVisibility.setGraphic(hideImg);
        toggleVisibility.setAlignment(Pos.BOTTOM_RIGHT);
        toggleVisibility.setPadding(Insets.EMPTY);
        toggleVisibility.setOnAction(ev -> {
            if (logBar.isVisible()) {
                logBar.setVisible(false);
                this.setMaxHeight(25);
                toggleVisibility.setGraphic(showImg);
            } else {
                logBar.setVisible(true);
                this.setMaxHeight(150);
                toggleVisibility.setGraphic(hideImg);
            }
        });

        ImageView clearImg = new ImageView("images/clear_history.png");
        clearImg.setFitWidth(20);
        clearImg.setFitHeight(20);
        clear.setGraphic(clearImg);
        clear.setPadding(Insets.EMPTY);
        clear.setOnAction(event -> logBar.clearLogBar());

        HBox separator = new HBox();
        separator.setHgrow(separator, Priority.ALWAYS);

        logToolbar.getItems().addAll(logBarText, separator, clear, toggleVisibility);
        logToolbar.setMaxHeight(20);

        logBar = new LogBar(this.logBarText);
        this.getChildren().addAll(logToolbar, logBar);
        toggleVisibility.fire();
    }

    public void log(String message) {
        this.logBar.addMessage(message);
    }

    public void displayMessage(String message){
        logBarText.setText(message);
    }

    public LogBar getLogBar() {
        return logBar;
    }

    public static class LogBar extends ScrollPane {

        private ArrayList<Text> messages = new ArrayList();
        //private Deque<Text> msg = new ArrayDeque<>();
        private Text externalText;

        public LogBar(Text externalText) {
            this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
            this.setVbarPolicy(ScrollBarPolicy.ALWAYS);
            this.setPrefHeight(150);
            this.setMinHeight(30);
            this.setMaxHeight(150);
            Date date = new Date();
            this.externalText = externalText;

            //messages.add(new Text(date.toString() + ": Begin session"));
            this.setStyle("-fx-background: rgb(255,255,255);");
            addMessage("Begin session");
            //displayMessages();
        }


        public void displayMessages() {
            String messageDisplayed = "";
            for (int i = messages.size() - 1; i >= 0; i--) {
                messageDisplayed += messages.get(i).getText() + "\n";

            }
            Label displayed = new Label(messageDisplayed);
            this.setContent(displayed);
        }

        public void addMessage(String msg) {
            externalText.setText(msg);
            Date date = new Date();
            messages.add(new Text(date.toString() + ": " + msg));
            displayMessages();
        }

        public void clearLogBar() {
            this.messages.clear();
            displayMessages();
        }


    }

}
