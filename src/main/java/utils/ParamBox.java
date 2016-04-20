package utils;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Created by felipe on 14/04/16.
 */
public class ParamBox extends HBox {

    private String param;

    private TextField textField = new TextField();
    private ImageView actionImageView = new ImageView("images/plus");

    public ParamBox() {

        this.setSpacing(10);
        this.setPadding(new Insets(10,5,5,10));
        this.getChildren().addAll(textField, actionImageView);
    }
}
