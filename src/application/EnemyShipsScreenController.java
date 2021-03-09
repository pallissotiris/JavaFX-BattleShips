package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EnemyShipsScreenController {
    private Player enemy;

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    @FXML
    Label carrierLabel;
    @FXML
    Label battleshipLabel;
    @FXML
    Label cruiserLabel;
    @FXML
    Label submarineLabel;
    @FXML
    Label destroyerLabel;

    @FXML
    Label carrierStatusLabel;
    @FXML
    Label battleshipStatusLabel;
    @FXML
    Label cruiserStatusLabel;
    @FXML
    Label submarineStatusLabel;
    @FXML
    Label destroyerStatusLabel;

    public void setLabels()
    {
        carrierStatusLabel.setText(enemy.carrier.getState().toString());
        battleshipStatusLabel.setText(enemy.battleship.getState().toString());
        cruiserStatusLabel.setText(enemy.cruiser.getState().toString());
        submarineStatusLabel.setText(enemy.submarine.getState().toString());
        destroyerStatusLabel.setText(enemy.destroyer.getState().toString());
    }

    public void setDefaults(){
        carrierStatusLabel.setText(ship_state.SOLID.toString());
        battleshipStatusLabel.setText(ship_state.SOLID.toString());
        cruiserStatusLabel.setText(ship_state.SOLID.toString());
        submarineStatusLabel.setText(ship_state.SOLID.toString());
        destroyerStatusLabel.setText(ship_state.SOLID.toString());
    }
}
