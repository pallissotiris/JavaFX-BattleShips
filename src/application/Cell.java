package application;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
    boolean isHit;
    private Ship ship;

    public Cell(){
        super();
        isHit = false;
        this.setHeight(35);
        this.setWidth(35);
        this.setStroke(Paint.valueOf("#000000"));
        this.setFill(Paint.valueOf("#0774f0"));
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public Ship getShip() {
        return ship;
    }
}
