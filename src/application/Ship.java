package application;

public class Ship {
    private final ship_type type;
    private ship_state state;
    int size;
    private int healthPoints;
    private int hitPoints;
    private int sinkPoints;

    public Ship(ship_type type) {
        this.type = type;
        switch (type) {
            case CARRIER -> {
                size = 5;
                healthPoints = 5;
                hitPoints = 350;
                sinkPoints = 1000;
            }
            case BATTLESHIP -> {
                size = 4;
                healthPoints = 4;
                hitPoints = 250;
                sinkPoints = 500;
            }
            case CRUISER -> {
                size = 3;
                healthPoints = 3;
                hitPoints = 100;
                sinkPoints = 250;
            }
            case SUBMARINE -> {
                size = 3;
                healthPoints = 3;
                hitPoints = 100;
                sinkPoints = 0;
            }
            case DESTROYER -> {
                size = 2;
                healthPoints = 2;
                hitPoints = 50;
                sinkPoints = 0;
            }
        }
        this.state = ship_state.SOLID;
    }

    public void hit() {
        state = ship_state.HIT;
        if (--healthPoints == 0) {
            state = ship_state.SUNK;
        }
    }

    public ship_state getState() {
        return state;
    }

    public ship_type getType() {
        return type;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getSinkPoints() {
        return sinkPoints;
    }
}
