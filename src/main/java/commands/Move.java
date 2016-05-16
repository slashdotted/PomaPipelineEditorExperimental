package commands;

import controller.DraggableModule;
import controller.MainWindow;
import javafx.geometry.Point2D;

/**
 * Command for move a DraggableModule on the dragboard
 */
public class Move implements Command {

    private DraggableModule draggableModule;
    private String dragId;
    private Point2D oldCoords;
    private Point2D newCoords;
    private Point2D oldOffset;
    private Point2D newOffset;


    public Move(String dragRef, Point2D oldCoords, Point2D newCoords, Point2D oldOffset, Point2D newOffset) {
        this.draggableModule = MainWindow.allDraggableModule.get(dragRef);
        this.dragId = dragRef;
        this.oldCoords = oldCoords;
        this.newCoords = newCoords;
        this.oldOffset = oldOffset;
        this.newOffset = newOffset;
    }


    @Override
    public boolean execute() {
        this.draggableModule = MainWindow.allDraggableModule.get(dragId);

        draggableModule.relocateToPoint(new Point2D(newCoords.getX(), newCoords.getY()), newOffset);

        draggableModule.getModule().setPosition(newCoords);
        return true;
    }

    @Override
    public boolean reverse() {

        Command reversMove = new Move(dragId, newCoords, oldCoords, newOffset, oldOffset);
        reversMove.execute();
        return true;
    }
}
