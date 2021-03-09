package application;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

public class MainController {

    //References to Main, Game and Players
    private Main main;
    private Game game;
    private Player player;
    private Player enemy;

    public void setMain(Main main) {
        this.main = main;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    @FXML
    protected void startMenuAction() {

        Random rand = new Random();
        int turnPlayer = rand.nextInt(2);

        try {
            if (!main.hasLoaded) {
                throw new LoadScenarioException("You haven't loaded a scenario yet!");
            }

            game = new Game(this.main);
            player.newGameStarted();
            enemy.newGameStarted();

            if (main.hasStarted) {
                clearGrids();
                if (enemyShipsScreenController != null) {
                    enemyShipsScreenController.setDefaults();
                }
                if (playerShotsHistoryController != null) {
                    playerShotsHistoryController.updateShotHistory();
                }
                if (enemyShotsHistoryController != null) {
                    enemyShotsHistoryController.updateShotHistory();
                }
            }

            updateGrid(player);
            updateGrid(enemy);
            updateLabels();
            disableInput(false);
            shootButton.setDefaultButton(true);
            main.hasStarted = true;

            game.startGame(turnPlayer);

        } catch (LoadScenarioException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Start error");
            alert.setHeaderText("Scenario not found");
            alert.setContentText("You haven't loaded a scenario yet!");
            alert.showAndWait();
        }
    }

    //GridPanes for the Boards
    @FXML
    GridPane playerGrid;
    @FXML
    GridPane enemyGrid;

    private void updateGrid(Player player) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Cell cell = new Cell();
                if (player.boardArray[i][j] > 0) {
                    switch (player.boardArray[i][j]) {
                        case 1 -> cell.setShip(player.carrier);
                        case 2 -> cell.setShip(player.battleship);
                        case 3 -> cell.setShip(player.cruiser);
                        case 4 -> cell.setShip(player.submarine);
                        case 5 -> cell.setShip(player.destroyer);
                    }
                    if (player.getType() == player_type.PLAYER) {
                        cell.setFill(Paint.valueOf("#4a4d4f"));
                    }
                }
                if (player.getType() == player_type.PLAYER) {
                    playerGrid.add(cell, j, i);
                } else {
                    enemyGrid.add(cell, j, i);
                }
            }
        }
    }

    private void clearGrids() {
        playerGrid.getChildren().remove(1, 100);
        enemyGrid.getChildren().remove(1, 100);
    }

    //Labels for score, active ships and successful hit rate for each player
    @FXML
    Label playerActiveShipLabel;
    @FXML
    Label enemyActiveShipLabel;
    @FXML
    Label playerPointsLabel;
    @FXML
    Label enemyPointsLabel;
    @FXML
    Label playerHitRateLabel;
    @FXML
    Label enemyHitRateLabel;

    private final DecimalFormat hitRateFormat = new DecimalFormat("#.0");

    public void updateLabels() {
        playerActiveShipLabel.setText("Active Ships: " + player.getActiveShips());
        enemyActiveShipLabel.setText("Active Ships: " + enemy.getActiveShips());
        playerPointsLabel.setText("Points: " + player.getTotalPoints());
        enemyPointsLabel.setText("Points: " + enemy.getTotalPoints());
        if (player.getTotalShots() > 0) {
            playerHitRateLabel.setText("Hit Rate: " + hitRateFormat.format(((double) player.getSuccessfulShots() / (double) player.getTotalShots() * 100)) + "%");
        } else {
            playerHitRateLabel.setText("Hit Rate: ");
        }
        if (enemy.getTotalShots() > 0) {
            enemyHitRateLabel.setText("Hit Rate: " + hitRateFormat.format(((double) enemy.getSuccessfulShots() / (double) enemy.getTotalShots() * 100)) + "%");
        } else {
            enemyHitRateLabel.setText("Hit Rate: ");
        }
    }

    @FXML
    protected void loadMenuAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoadScreen.fxml"));
            Parent root = loader.load();
            LoadScreenController loadScreenController = loader.getController();
            loadScreenController.setMainController(this);

            Scene scene = new Scene(root);
            Stage loadWindowStage = new Stage();
            loadWindowStage.setTitle("Load Scenario");
            Image appIcon = new Image(getClass().getResourceAsStream("icon1.png"));
            loadWindowStage.getIcons().add(appIcon);
            loadWindowStage.initModality(Modality.APPLICATION_MODAL);
            loadWindowStage.setScene(scene);
            loadWindowStage.setResizable(false);
            loadWindowStage.showAndWait();
            if (showWinnerController != null && showWinnerStage != null) {
                showWinnerStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadScenario(int ID) {
        try {
            if (player.loadScenario(ID) && enemy.loadScenario(ID)) {
                main.hasLoaded = true;
            }
            disableInput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void exitMenuAction() {
        if (playerShotsHistoryStage != null) {
            playerShotsHistoryStage.close();
        }
        if (enemyShotsHistoryStage != null) {
            enemyShotsHistoryStage.close();
        }
        if (enemyShipsStage != null) {
            enemyShipsStage.close();
        }
        if (showWinnerStage != null) {
            showWinnerStage.close();
        }
        Platform.exit();
    }

    @FXML
    protected void enemyShipsMenuAction() {
        if (enemyShipsStage == null) {
            try {

                if (!main.hasStarted) {
                    throw new GameNotStartedException("You haven't started the game yet!");
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("EnemyShipsScreen.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                enemyShipsScreenController = loader.getController();
                enemyShipsScreenController.setEnemy(enemy);
                enemyShipsScreenController.setLabels();
                enemyShipsStage = new Stage();
                enemyShipsStage.setTitle("Enemy Ships State");
                Image appIcon = new Image(getClass().getResourceAsStream("icon1.png"));
                enemyShipsStage.getIcons().add(appIcon);
                enemyShipsStage.setScene(scene);
                enemyShipsStage.setResizable(false);
                enemyShipsStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (GameNotStartedException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Enemy ships error");
                alert.setHeaderText("Game Not Started");
                alert.setContentText("You haven't started the game yet!");
                alert.showAndWait();
            }
        } else if (enemyShipsStage.isShowing()) {
            enemyShipsStage.toFront();
        } else {
            enemyShipsStage.show();
        }
    }

    EnemyShipsScreenController enemyShipsScreenController;
    private Stage enemyShipsStage;

    @FXML
    protected void playerShotsMenuAction() {
        if (playerShotsHistoryStage == null) {
            try {

                if (!main.hasStarted) {
                    throw new GameNotStartedException("You haven't started the game yet!");
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("ShotsHistoryScreen.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                playerShotsHistoryController = loader.getController();
                playerShotsHistoryController.setPlayer(player);
                playerShotsHistoryController.setTitleLabel("Player Shots");
                playerShotsHistoryController.updateShotHistory();
                playerShotsHistoryStage = new Stage();
                playerShotsHistoryStage.setTitle("Player Shots History");
                Image appIcon = new Image(getClass().getResourceAsStream("icon1.png"));
                playerShotsHistoryStage.getIcons().add(appIcon);
                playerShotsHistoryStage.setScene(scene);
                playerShotsHistoryStage.setResizable(false);
                playerShotsHistoryStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (GameNotStartedException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Enemy ships error");
                alert.setHeaderText("Game Not Started");
                alert.setContentText("You haven't started the game yet!");
                alert.showAndWait();
            }
        } else if (playerShotsHistoryStage.isShowing()) {
            playerShotsHistoryStage.toFront();
        } else {
            playerShotsHistoryStage.show();
        }
    }

    ShotsHistoryController playerShotsHistoryController;
    private Stage playerShotsHistoryStage;

    @FXML
    protected void enemyShotsMenuAction() {
        if (enemyShotsHistoryStage == null) {
            try {

                if (!main.hasStarted) {
                    throw new GameNotStartedException("You haven't started the game yet!");
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("ShotsHistoryScreen.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                enemyShotsHistoryController = loader.getController();
                enemyShotsHistoryController.setPlayer(enemy);
                enemyShotsHistoryController.setTitleLabel("Enemy Shots");
                enemyShotsHistoryController.updateShotHistory();
                enemyShotsHistoryStage = new Stage();
                enemyShotsHistoryStage.setTitle("Enemy Shots History");
                Image appIcon = new Image(getClass().getResourceAsStream("icon1.png"));
                enemyShotsHistoryStage.getIcons().add(appIcon);
                enemyShotsHistoryStage.setScene(scene);
                enemyShotsHistoryStage.setResizable(false);
                enemyShotsHistoryStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (GameNotStartedException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Enemy ships error");
                alert.setHeaderText("Game Not Started");
                alert.setContentText("You haven't started the game yet!");
                alert.showAndWait();
            }
        } else if (enemyShotsHistoryStage.isShowing()) {
            enemyShotsHistoryStage.toFront();
        } else {
            enemyShotsHistoryStage.show();
        }
    }

    ShotsHistoryController enemyShotsHistoryController;
    private Stage enemyShotsHistoryStage;

    @FXML
    private Button shootButton;

    @FXML
    private TextField shotInput;

    public void disableInput(boolean value) {
        shootButton.setDisable(value);
        shotInput.setDisable(value);
    }

    @FXML
    protected void shootButtonAction() {
        int row = -1;
        int col = -1;
        String input = shotInput.getText();
        String[] values;
        boolean flag = true;
        try {
            if (input.isEmpty()) {
                throw new EmptyInputException("Input is empty!");
            } else {
                values = input.split(",");
                if (values.length == 2) {
                    row = Integer.parseInt(values[0]);
                    if (Character.isAlphabetic(values[1].charAt(0))) {
                        col = values[1].charAt(0);
                    } else {
                        throw new IllegalArgumentException("2nd digit must be a Letter!");
                    }
                } else {
                    throw new IllegalArgumentException("Input syntax is wrong here");
                }
            }
            if (row < 1 || row > 10 || col < 65 || (col > 74 && col < 97) || col > 122) {
                throw new OutOfRangeException("Input out of range");
            }
            Cell cell = null;
            ObservableList<Node> children = enemyGrid.getChildren();
            for (Node node : children) {
                if (flag) {
                    flag = false;
                    continue;
                }
                if (col > 96) {
                    col -= 32;
                }
                if (GridPane.getRowIndex(node) == (row - 1) && GridPane.getColumnIndex(node) == (col % 65)) {
                    cell = (Cell) node;
                }
            }
            if (cell != null) {
                if (cell.isHit) {
                    throw new CellAlreadyHitException("Cell has already been hit!");
                } else {
                    cell.isHit = true;
                    game.makePlayerMove(cell);
                    player.updateLists(row, col, cell.getShip());
                }
            }
            shotInput.setText("");
            errorLabel.setVisible(false);

        } catch (EmptyInputException e) {
            errorLabel.setText("Input is empty, you need to define a cell to be shot.");
            errorLabel.setVisible(true);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            errorLabel.setText("Input syntax is wrong, shots must be defined as [row],[column], eg. \"3,A\"");
            errorLabel.setVisible(true);
        } catch (OutOfRangeException e) {
            System.out.println(e.getMessage());
            errorLabel.setText("Row must be a number in the range of 1-10 and column must be a letter in the range A-J.");
            errorLabel.setVisible(true);
        } catch (CellAlreadyHitException e) {
            System.out.println(e.getMessage());
            errorLabel.setText("Cell in row: " + row + " and column: " + Character.toString(col) + " has already been hit!");
            errorLabel.setVisible(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    Label errorLabel;

    ShowWinnerController showWinnerController;
    private Stage showWinnerStage;

    public void showWinner(Player player) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ShowWinnerScreen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            showWinnerController = loader.getController();
            showWinnerController.setMainController(this);
            showWinnerController.setTitleLabel((player.getType() == player_type.PLAYER) ? "YOU WIN!!!" : "YOU LOSE :(");
            disableInput(true);
            showWinnerStage = new Stage();
            showWinnerStage.setTitle("Game Result");
            Image appIcon = new Image(getClass().getResourceAsStream("icon1.png"));
            showWinnerStage.getIcons().add(appIcon);
            showWinnerStage.initModality(Modality.APPLICATION_MODAL);
            showWinnerStage.setScene(scene);
            showWinnerStage.setResizable(false);
            showWinnerStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showWinner() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ShowWinnerScreen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            showWinnerController = loader.getController();
            showWinnerController.setMainController(this);
            if (player.getTotalPoints() > enemy.getTotalPoints()) {
                showWinnerController.setTitleLabel("YOU WIN!!!");
            } else if (player.getTotalPoints() < enemy.getTotalPoints()) {
                showWinnerController.setTitleLabel("YOU LOSE :(");
            } else {
                showWinnerController.setTitleLabel("TIE");
            }
            disableInput(true);
            showWinnerStage = new Stage();
            showWinnerStage.setTitle("Game Result");
            Image appIcon = new Image(getClass().getResourceAsStream("icon1.png"));
            showWinnerStage.getIcons().add(appIcon);
            showWinnerStage.initModality(Modality.APPLICATION_MODAL);
            showWinnerStage.setScene(scene);
            showWinnerStage.setResizable(false);
            showWinnerStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
