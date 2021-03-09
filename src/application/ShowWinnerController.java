package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ShowWinnerController {
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    Label titleLabel;

    public void setTitleLabel(String value){
        titleLabel.setText(value);
        textLabel.setText("Thanks for playing!\nYou can load another scenario or\nexit the game.");
    }

    @FXML
    Label textLabel;

    @FXML
    Button exitButton;

    @FXML
    protected void exitButtonAction(){
        mainController.exitMenuAction();
    }

    @FXML
    Button loadButton;

    @FXML
    protected void loadButtonAction(){
        mainController.exitMenuAction();
    }

}
