package main;

import controller.MainWindow;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class CanvasPane extends Pane {
    @Override
    protected double computePrefWidth(double height) {
        double maxValue = 0;
        for (Node n : MainWindow.mainGroup.getChildren()) {
            if (maxValue < n.getLayoutX()) {
                maxValue = n.getLayoutX();
            }
        }
        return Math.max(MainWindow.mainScrollPaneStat.getMinViewportWidth(), maxValue + 200);
    }

    @Override
    protected double computePrefHeight(double width) {
        double maxValue = 0;
        for (Node n : MainWindow.mainGroup.getChildren()) {
            if (maxValue < n.getLayoutY()) {
                maxValue = n.getLayoutY();
            }
        }
        return Math.max(MainWindow.mainScrollPaneStat.getMinViewportHeight(), maxValue + 200);
    }

    @Override
    protected double computeMinWidth(double height) {
        return computePrefWidth(height);
    }

    @Override
    protected double computeMinHeight(double width) {
        return computePrefHeight(width);
    }   

}
