import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by Anton on 24.03.2018.
 */
public class Cell extends Rectangle {
    private int coordinate;
    private boolean hit;
    private boolean hasShip;

    public Cell (int coordinate){
        super(25,25);
        this.coordinate = coordinate;
        this.hasShip = false;
        this.hit = false;
        setFill(Color.LIGHTGRAY);
        setStroke(Color.BLACK);
    }

    public int getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(int coordinate) {
        this.coordinate = coordinate;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean isHasShip() {
        return hasShip;
    }

    public void setHasShip(boolean hasShip) {
        this.hasShip = hasShip;
    }
}
