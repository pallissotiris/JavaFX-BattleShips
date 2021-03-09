package application;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoadScreenController {

    int ID;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        loadScenarioButton.setDefaultButton(true);
    }

    @FXML
    Label loadScenarioLabel;
    @FXML
    TextField loadScenarioTextField;

    @FXML
    Button loadScenarioButton;

    @FXML protected void loadScenarioAction()
    {
        if(isInt(loadScenarioTextField, errorLabel))
        {
            mainController.loadScenario(ID);
            Stage stage = (Stage) loadScenarioButton.getScene().getWindow();
            stage.close();
        }
    }

    private boolean isInt (TextField input, Label error)
    {
        try
        {
            ID = Integer.parseInt(input.getText());
            return true;
        }
        catch(NumberFormatException e)
        {
            error.setText("This is not a number!");
            return false;
        }
    }

    @FXML
    Label errorLabel;
}
