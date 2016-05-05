package commands;

import controller.DraggableModule;
import javafx.geometry.Point2D;

/**
 * Created by felipe on 06/04/16.
 */
public class Move implements Command {

    private DraggableModule draggableModule;
    private Point2D oldCoords;
    private Point2D newCoords;
    private Point2D oldOffset;
    private Point2D newOffset;
    public Move(DraggableModule draggableModule, Point2D oldCoords, Point2D newCoords, Point2D oldOffset, Point2D newOffset) {
        this.draggableModule = draggableModule;
        this.oldCoords = oldCoords;
        this.newCoords = newCoords;
        this.oldOffset = oldOffset;
        this.newOffset = newOffset;
    }


    @Override
    public boolean execute() {
        //debug("");
        draggableModule.relocateToPoint(new Point2D(newCoords.getX(),newCoords.getY()), newOffset);
        draggableModule.getModule().setPosition(newCoords);
        return true;
    }

    @Override
    public boolean reverse() {

        Command reversMove = new Move(draggableModule, newCoords, oldCoords, newOffset, oldOffset);
        reversMove.execute();
        return true;
    }
}
