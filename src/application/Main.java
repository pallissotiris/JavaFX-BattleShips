package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This Application is a version of the classic game "Battleship".
 * It was developed for the purposes of Multimedia Technology's Project
 *
 * Class Player has full JavaDoc Documentation
 *
 * @author  Pallis Sotirios 03117810
 * @version 1.0
 * @since   2021-03-01
 */
public class Main extends Application {

    public Stage mainStage;

    boolean hasLoaded = false;
    public boolean hasStarted = false;

    Player player;
    Player enemy;
    MainController mainController;

    /**
     *This method starts the main stage which will hold the GUI.
     * It loads the corresponding FXML file and opens the window.
     * @param primaryStage The Primary Stage to be loaded.
     * @throws IOException In case the FXML file loading fails.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        //Open window and get controller
        mainStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainGUI.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        mainController.setMain(this);
        mainController.disableInput(true);

        //Make 2 players and assign them to the controller and to main
        player = new Player(player_type.PLAYER);
        enemy = new Player(player_type.ENEMY);
        mainController.setPlayer(player);
        mainController.setEnemy(enemy);

        mainStage.setOnCloseRequest(e -> mainController.exitMenuAction());
        mainStage.setTitle("MediaLab Battleship");
        mainStage.setScene(new Scene(root));
        Image appIcon = new Image(getClass().getResourceAsStream("icon1.png"));
        mainStage.getIcons().add(appIcon);
        mainStage.setResizable(false);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
