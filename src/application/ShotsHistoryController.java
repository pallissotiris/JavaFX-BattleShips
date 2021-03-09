package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ShotsHistoryController {
    private Player player;
    public void setPlayer(Player player) {
        this.player = player;
    }
    public void setTitleLabel(String value){titleLabel.setText(value);}

    @FXML
    ListView<String> coordinatesList;

    @FXML
    ListView<String> resultList;

    @FXML
    ListView<String> shipList;

    @FXML
    Label titleLabel;

    public void updateShotHistory() {
        coordinatesList.setItems(player.coordinates);
        resultList.setItems(player.results);
        shipList.setItems(player.shipsHit);
    }
}
