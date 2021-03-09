package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;

public class Player {

    //Object's player type, either PLAYER or ENEMY
    private final player_type type;

    //2D Array that holds ship values (0 if no ship, 1 for Carrier, 2 for Battleship etc.)
    protected int[][] boardArray;

    //An array that helps in constructing the boardArray by holding each ship's size
    protected int[] shipArray;

    //ID of the scenario that was loaded
    private int scenarioID;

    //Player's total shots for the current game
    private int totalShots;

    //Player's successful shots for the current game
    private int successfulShots;

    //Player's total points for the current game
    private int totalPoints;

    //Player's active ships for the current game
    private int activeShips;

    //Player's ships
    Ship carrier;
    Ship battleship;
    Ship cruiser;
    Ship submarine;
    Ship destroyer;

    //Lists for Player's Shot History
    ObservableList<String> coordinates;
    ObservableList<String> results;
    ObservableList<String> shipsHit;


    //CONSTRUCTOR
    /**
     * This method is the Constructor of an Object Type Player.
     * These objects are used to hold information about both the player and the computer.
     * The scenario loading and validation also happens in this object
     * @param type This is the player's type, either PLAYER or ENEMY
     */
    public Player(player_type type){
        this.type = type;
        boardArray = new int[10][10];
        shipArray = new int[5];
    }


    /**
     * This method initializes the Player object when a new Game starts.
     * It assigns new Ship objects to the Player's ships and it initializes Points, (total and successful) Shots and active Ships number
     * It also initializes the detail Lists
     */
    public void newGameStarted(){
        carrier = new Ship(ship_type.CARRIER);
        battleship = new Ship(ship_type.BATTLESHIP);
        cruiser = new Ship(ship_type.CRUISER);
        submarine = new Ship(ship_type.SUBMARINE);
        destroyer = new Ship(ship_type.DESTROYER);

        totalPoints = 0;
        successfulShots = 0;
        totalShots = 0;
        activeShips = 5;
        coordinates = FXCollections.observableArrayList();
        results = FXCollections.observableArrayList();
        shipsHit = FXCollections.observableArrayList();
    }

    /**
     *This method is used to load a scenario for this Player 's ship placement.
     *It uses the initializeShipArray method and the validateLoad method.
     * @param ID The ID of the scenario to be loaded
     * @return boolean Returns true if the scenario was successfully loaded.
     * @throws IOException In case there was an error with the validateLoad method.
     */
    public boolean loadScenario(int ID) throws IOException {
        scenarioID = ID;
        String scenarioFile = ("medialab\\" + ((type == player_type.PLAYER) ? "player" : "enemy") +"_" + ID + ".txt");

        initializeShipArray();

        return validateLoad(scenarioFile);
    }

    /**
     * This method is used to initialize the shipArray.
     * Each element is the size of the ship that corresponds to the index.
     * Index 0 for Carrier, 1 for Battleship etc.
     * @see Ship
     */
    public void initializeShipArray() {
        shipArray[0] = 5;
        shipArray[1] = 4;
        shipArray[2] = 3;
        shipArray[3] = 3;
        shipArray[4] = 2;
    }

    /**
     *This method loads the scenario specified and modifies the boardArray based on that scenario.
     * It starts with reading the scenario file line by line, then modifying the boardArray based on the values in it.
     * Each element of the boardArray is initialized at 0. Then if a ship is on that tile, the corresponding tile gets a value
     * in range 1-5, depending on the ship. If a ship is placed out of the board, an Oversize Exception is thrown.
     * If a ship is placed on top of another an OverlapTilesException is thrown. If a ship is placed next to another then an
     * AdjacentTilesException is thrown. Finally, if more than 1 ship of each kind is placed an InvalidCountException is thrown.
     * If any exceptions occur, the load fails and the user gets notified with a corresponding alert.
     * @param scenario The path of the scenario to be loaded.
     * @return boolean Returns true if the scenario is valid and was successfully loaded.
     * @throws IOException In case there is an error with reading the file
     * @see IOException
     * @see OverlapTilesException
     * @see FileNotFoundException
     * @see InvalidCountException
     * @see OversizeException
     * @see AdjacentTilesException
     * @see NumberFormatException
     * @see IllegalArgumentException
     */
    public boolean validateLoad(String scenario) throws IOException{
        int x, y, ship, size, orientation;
        String line;
        String[] values;
        try {
            BufferedReader someBuffer = new BufferedReader(new FileReader(scenario));

            //Initialize boardArray
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    boardArray[i][j] = 0;
                }
            }

            //Read first five lines of file opened
            for (int i = 0; i < 5; i++) {

                line = someBuffer.readLine();
                values = line.split(",");

                if(values.length != 4){
                    throw new IllegalArgumentException("There must be only 4 numbers in each line of the scenario!");
                }

                ship = Integer.parseInt(values[0]);
                x = Integer.parseInt(values[1]);
                y = Integer.parseInt(values[2]);
                orientation = Integer.parseInt(values[3]);

                if (shipArray[ship - 1] == 0) {
                    throw new InvalidCountException("Can't have 2 ships of the same kind!");
                } else {
                    size = shipArray[ship - 1];
                    shipArray[ship - 1] = 0;
                }

                if (orientation == 1) {
                    if ((x > 9) || (x < 0) || (y < 0) || ((y + size - 1) > 9)) {
                        throw new OversizeException("You can't place ships out of the board!");
                    } else {
                        if (y > 0) {
                            if (boardArray[x][y - 1] > 0) {
                                throw new AdjacentTilesException("You can't place 2 ships adjacent to each other!");
                            }
                        }
                        if ((y + size) < 9) {
                            if (boardArray[x][y + size + 1] > 0) {
                                throw new AdjacentTilesException("You can't place 2 ships adjacent to each other!");
                            }
                        }
                        for (int j = 0; j < size; j++) {
                            if (boardArray[x][y + j] > 0) {
                                throw new OverlapTilesException("You can't place 2 ships on each other!");
                            }
                            if (x > 0) {
                                if (boardArray[x - 1][y + j] > 0) {
                                    throw new AdjacentTilesException("You can't place 2 ships adjacent to each other!");
                                }
                            }
                            if (x < 9) {
                                if (boardArray[x + 1][y + j] > 0) {
                                    throw new AdjacentTilesException("You can't place 2 ships adjacent to each other!");
                                }
                            }
                            boardArray[x][y + j] = ship;
                        }
                    }
                } else if (orientation == 2) {
                    if (((x + size - 1) > 9) || (x < 0) || (y < 0) || (y > 9)) {
                        throw new OversizeException("You can't place ships out of the board!");
                    } else {
                        if (x > 0) {
                            if (boardArray[x - 1][y] > 0) {
                                throw new AdjacentTilesException("You can't place 2 ships adjacent to each other!");
                            }
                        }
                        if ((x + size) < 9) {
                            if (boardArray[x + size + 1][y] > 0) {
                                throw new AdjacentTilesException("You can't place 2 ships adjacent to each other!");
                            }
                        }
                        for (int j = 0; j < size; j++) {
                            if (boardArray[x + j][y] > 0) {
                                throw new OverlapTilesException("You can't place 2 ships on each other!");
                            }
                            if (y > 0) {
                                if (boardArray[x + j][y - 1] > 0) {
                                    throw new AdjacentTilesException("You can't place 2 ships adjacent to each other!");
                                }
                            }
                            if (y < 9) {
                                if (boardArray[x + j][y + 1] > 0) {
                                    throw new AdjacentTilesException("You can't place 2 ships adjacent to each other!");
                                }
                            }
                            boardArray[x + j][y] = ship;
                        }
                    }
                }
                else{
                    throw new IllegalArgumentException("Orientation should be either 1 or 2");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(((type == player_type.PLAYER) ? "Player " : "Enemy ") + "Scenario with ID " + scenarioID + " does not exist!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scenario ID error");
            alert.setHeaderText("File not found");
            alert.setContentText(((type == player_type.PLAYER) ? "Player " : "Enemy ") + "Scenario with ID " + scenarioID + " does not exist!");
            alert.showAndWait();
            return false;
        } catch (InputMismatchException e){
            System.out.println(e.getMessage());
            return false;
        }catch (InvalidCountException e) {
            System.out.println("Exception occurred:" + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scenario error");
            alert.setHeaderText("Invalid count");
            alert.setContentText(((type == player_type.PLAYER) ? "Player " : "Enemy ") + "scenario: You can't have more than 1 ships of any kind!");
            alert.showAndWait();
            return false;
        } catch (OversizeException e) {
            System.out.println("Exception occurred:" + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scenario error");
            alert.setHeaderText("Out of bounds");
            alert.setContentText(((type == player_type.PLAYER) ? "Player " : "Enemy ") + "scenario: You can't place ships out of the board!");
            alert.showAndWait();
            return false;
        } catch (OverlapTilesException e) {
            System.out.println("Exception occurred:" + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scenario error");
            alert.setHeaderText("Overlapping ships");
            alert.setContentText(((type == player_type.PLAYER) ? "Player " : "Enemy ") + "scenario: You can't place 2 ships on each other!");
            alert.showAndWait();
            return false;
        } catch (AdjacentTilesException e) {
            System.out.println("Exception occurred:" + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scenario error");
            alert.setHeaderText("Adjacent tiles");
            alert.setContentText(((type == player_type.PLAYER) ? "Player " : "Enemy ") + "scenario: You can't place 2 ships adjacent to each other!");
            alert.showAndWait();
            return false;
        }catch (NumberFormatException e){
            System.out.println("Exception occurred:" + e.getMessage());
            return false;
        }catch(IllegalArgumentException e)
        {
            System.out.println("Exception occurred:" + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scenario error");
            alert.setHeaderText("Error in scenario");
            alert.setContentText(((type == player_type.PLAYER) ? "Player " : "Enemy ") + "scenario: "+ e.getMessage());
            alert.showAndWait();
        }
        return true;
    }

    /**
     * This method updates the Player 's details lists that are used in the Shot History Windows.
     * It adds the row and column to the coordinates list, the message "SUCCESS" or "FAILURE" if the shot was successful and
     * the type of the ship that was hit (or "NONE" if no ship was hit).
     * @param row The row that was shot.
     * @param col The column that was shot.
     * @param ship Reference to the Ship of the Cell that was shot (null if a Ship isn't in that Cell).
     * @see Ship
     * @see Cell
     * @see Game
     * @see MainController
     * @see ShotsHistoryController
     */
    public void updateLists(int row, int col, Ship ship){
        coordinates.add(row + "," + (char) col);
        if(coordinates.size() > 5)
        {
            coordinates.remove(0);
        }
        results.add((ship == null)? "FAILURE": "SUCCESS");
        if(results.size() > 5)
        {
            results.remove(0);
        }
        shipsHit.add((ship == null)? "NONE": ship.getType().toString());
        if(shipsHit.size() > 5)
        {
            shipsHit.remove(0);
        }
    }

    /**
     *Getter function that returns the Player 's type.
     * @return player_type The Object's player type
     */
    public player_type getType() {
        return type;
    }

    /**
     * Getter function that return the Player 's total shots count.
     * @return int Player 's total shots count.
     */
    public int getTotalShots() {
        return totalShots;
    }

    /**
     *Getter function that returns the Player 's successful shots count.
     * @return int Player 's successful shots count.
     */
    public int getSuccessfulShots() {
        return successfulShots;
    }

    /**
     *Getter function that returns the Player 's total points.
     * @return int Player 's total points.
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     *Getter function that returns the Player 's current active ships count (not SUNK).
     * @return int Player 's current active ships count (not SUNK).
     * @see Ship
     */
    public int getActiveShips() {
        return activeShips;
    }

    /**
     * Setter function for Player's total shots count.
     * @param value The value to set the total shots to.
     */
    public void setTotalShots(int value){
        totalShots = value;
    }

    /**
     * Setter function for Player's successful shots count.
     * @param value The value to set the successful shots to.
     */
    public void setSuccessfulShots(int value){
        successfulShots = value;
    }

    /**
     * Setter function for Player's total points count.
     * @param value The value to set the total points to.
     */
    public void setTotalPoints(int value) {
        totalPoints = value;
    }

    /**
     * Setter function for Player's active ships count.
     * @param value The value to set the active ships count to.
     */
    public void setActiveShips(int value){
        activeShips = value;
    }

}
