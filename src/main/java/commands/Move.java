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

    public Move(DraggableModule draggableModule, Point2D oldCoords, Point2D newCoords) {
        this.draggableModule = draggableModule;
        this.oldCoords = oldCoords;
        this.newCoords = newCoords;
    }


    @Override
    public boolean execute() {

        draggableModule.relocate((int) newCoords.getX(), (int) newCoords.getY());
        draggableModule.getModule().setPosition(newCoords);
        return true;
    }

    @Override
    public boolean reverse() {
        Command reversMove = new Move(draggableModule, newCoords, oldCoords);
        reversMove.execute();
        return true;
    }
}
