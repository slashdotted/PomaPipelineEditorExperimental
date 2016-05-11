package utils;

import controller.DraggableModule;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import main.Main;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by felipe on 11/05/16.
 */
public class SelectionArea {
    private Group selectionGroup = new Group();
    private Rectangle selectionShape = null;
    boolean drawing = false;
    private Color color = Color.BLUE;
    private AnchorPane pane;
    private Group intersectionGroup;
    private Map<String, DraggableModule> draggableModules;
    private double startingY;
    private double startingX;
    private ArrayList<DraggableModule> selectionBuffer = new ArrayList<>();


    public SelectionArea(AnchorPane pane, Group intersectionGroup, Map<String, DraggableModule> draggableModules) {
        //this.selectionGroup = selectionGroup;
        this.pane = pane;
        this.intersectionGroup = intersectionGroup;
        this.draggableModules = draggableModules;

        pane.getChildren().add(selectionGroup);

    }

    private void setHandlers() {
        pane.setOnMousePressed((MouseEvent event) -> {

            Point2D actualPosition = new Point2D(event.getX(), event.getY());
            if (pane.contains(actualPosition) && !drawing && !intersectionGroup.contains(actualPosition)) {
                startingX = event.getX();
                startingY = event.getY();

                selectionShape = new Rectangle();

                // A non-finished rectangle has always the same color.
                selectionShape.setOpacity(0.3);
                selectionShape.setFill(color); // almost white color
                selectionShape.setStroke(Color.DARKBLUE);
                selectionShape.setStrokeType(StrokeType.CENTERED);
                selectionShape.setStrokeWidth(2);
                selectionGroup.getChildren().add(selectionShape);

                drawing = true;
            }
        });


        pane.setOnMouseDragged((MouseEvent event) -> {
            if (drawing == true) {
                double currentMouseX = event.getX();
                double currentMouseY = event.getY();

                adjustSelectionShape(startingX,
                        startingY,
                        currentMouseX,
                        currentMouseY,
                        selectionShape);

                draggableModules.keySet().forEach(key -> {

                    DraggableModule current = draggableModules.get(key);
                    if (selectionShape.getBoundsInLocal().intersects(current.getBoundsInParent())) {
                        current.select();
                        // selectionBuffer.add(current);

                    } else if (current.isSelected()) {
                        current.unselect();
                        //selectionBuffer.remove(current);
                    }


                });


            }


        });

        pane.setOnMouseReleased((MouseEvent event) -> {
            if (drawing == true) {
                selectionShape.setFill(color);
                selectionGroup.getChildren().remove(selectionShape);
                selectionShape = null;
                drawing = false;
            }
        });

    }

    void adjustSelectionShape(double startX, double startY, double endX, double endY, Rectangle currentRectangle) {
        currentRectangle.setX(startX);
        currentRectangle.setY(startY);
        currentRectangle.setWidth(endX - startX);
        currentRectangle.setHeight(endY - startY);

        if (currentRectangle.getWidth() < 0) {
            currentRectangle.setWidth(-currentRectangle.getWidth());
            currentRectangle.setX(currentRectangle.getX() - currentRectangle.getWidth());
        }

        if (currentRectangle.getHeight() < 0) {
            currentRectangle.setHeight(-currentRectangle.getHeight());
            currentRectangle.setY(currentRectangle.getY() - currentRectangle.getHeight());
        }
    }


    public void initialize() {
        setHandlers();
    }
}
