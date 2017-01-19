package utils;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Date;

public class StackedLogBar extends VBox {

    public static final int MODIFIED = 100;
    public static final int CREATED = 200;
    public static final int DELETED = 300;
    private LogBar logBar;
    private ToolBar logToolbar = new ToolBar();
    private Text logBarText = new Text("Event Log");
    private Button clear = new Button();
    private Button toggleVisibility = new Button();
    private static StackedLogBar instance;

    private StackedLogBar() {
        this.setPrefHeight(200);
        this.setMinHeight(30);
        this.setMaxHeight(200);
        this.setFillWidth(true);


        ImageView hideImg = new ImageView("images/hide_history.png");
        hideImg.setFitWidth(20);
        hideImg.setFitHeight(20);
        ImageView showImg = new ImageView("images/show_history.png");
        showImg.setFitWidth(20);
        showImg.setFitHeight(20);
        toggleVisibility.setGraphic(hideImg);
        toggleVisibility.setAlignment(Pos.BOTTOM_RIGHT);
        toggleVisibility.setBackground(Background.EMPTY);
        toggleVisibility.setOnAction(ev -> {
            if (logBar.isVisible()) {
                logBar.setVisible(false);
                this.setMaxHeight(25);
                toggleVisibility.setGraphic(showImg);
            } else {
                logBar.setVisible(true);
                this.setMaxHeight(200);
                toggleVisibility.setGraphic(hideImg);
            }
        });

        ImageView clearImg = new ImageView("images/clear_history.png");
        clearImg.setFitWidth(20);
        clearImg.setFitHeight(20);

        clear.setGraphic(clearImg);
        clear.setBackground(Background.EMPTY);
        clear.setOnAction(event -> logBar.clearLogBar());


        HBox separator = new HBox();
        separator.setHgrow(separator, Priority.ALWAYS);

        logToolbar.getItems().addAll(toggleVisibility, clear, logBarText, separator);
        logToolbar.setMaxHeight(20);

        logBar = new LogBar();
        this.getChildren().addAll(logToolbar, logBar);
        toggleVisibility.fire();
    }
    
    public static StackedLogBar instance() {
        if (instance == null) {
            instance = new StackedLogBar();
        }
        return instance;
    }

    public void log(String message) {
        displayMessage(message);
        this.logBar.addMessage(message);
    }

    public void logAndWarning(String message) {
        displayWarning(message);
        this.logBar.addMessage(message);
    }

    public void logAndSuccess(String message) {
        displaySuccess(message);
        this.logBar.addMessage(message);
    }

    public void displayWarning(String message){
        logBarText.setText(message);
        logBarText.setFill(Color.RED);
    }

    public void displaySuccess(String message){
        logBarText.setText(message);
        logBarText.setFill(Color.GREEN);
    }

    public void displayMessage(String message){
        logBarText.setText(message);
        logBarText.setFill(Color.BLACK);
    }


    public static class LogBar extends ScrollPane {

        private ArrayList<Text> messages = new ArrayList();

        public LogBar() {
            this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
            this.setVbarPolicy(ScrollBarPolicy.ALWAYS);
            this.setPrefHeight(200);
            this.setMinHeight(30);
            this.setMaxHeight(200);
            this.setStyle("-fx-background: rgb(255,255,255);");
            addMessage("Begin session");
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
