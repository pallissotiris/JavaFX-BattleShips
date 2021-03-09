package application;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;

import java.util.Random;

public class Game {
    private final MainController mainController;

    private boolean lastShotHit;
    private boolean upDirectionAvailable;
    private boolean downDirectionAvailable;
    private boolean leftDirectionAvailable;

    private int row;
    private int col;

    private int upOffset;
    private int downOffset;
    private int leftOffset;
    private int rightOffset;

    Cell upCell;
    Cell downCell;
    Cell leftCell;
    Cell rightCell;

    Player player;
    Player enemy;

    public Game(Main main) {
        this.mainController = main.mainController;
        this.player = main.player;
        this.enemy = main.enemy;

        setDirections();
    }

    public void startGame(int who) {
        if (who == 1) {
            makeEnemyMove();
        }
    }

    public void makePlayerMove(Cell cell) {
        checkCell(cell, cell.getShip(), player, enemy);
        if (enemy.getTotalShots() != 40) {
            makeEnemyMove();
        }
    }

    private void makeEnemyMove() {
        mainController.disableInput(true);
        boolean searchAgain = true;

        Cell cell;
        Random rand = new Random();
        ObservableList<Node> children = mainController.playerGrid.getChildren();
        if (!lastShotHit) {
            while (searchAgain) {
                row = rand.nextInt(10);
                col = rand.nextInt(10);
                cell = searchGrid(row, col, children);
                upCell = searchGrid(row - 1, col, children);
                downCell = searchGrid(row + 1, col, children);
                leftCell = searchGrid(row, col - 1, children);
                rightCell = searchGrid(row, col + 1, children);
                try {
                    if (cell != null) {
                        if (!cell.isHit && checkAdjacentCells()) {
                            cell.isHit = true;
                            searchAgain = false;
                            enemy.updateLists(row + 1, 65 + col, cell.getShip());
                            if (checkCell(cell, cell.getShip(), enemy, player)) {
                                lastShotHit = true;
                                setDirections();
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } else {
            upCell = searchGrid(row - upOffset, col, children);
            downCell = searchGrid(row + downOffset, col, children);
            leftCell = searchGrid(row, col - leftOffset, children);
            rightCell = searchGrid(row, col + rightOffset, children);
            if (upCell != null && !upCell.isHit && upDirectionAvailable) {
                enemy.updateLists(row - upOffset + 1,65 + col, upCell.getShip());
                if (checkCell(upCell, upCell.getShip(), enemy, player)) {
                    leftDirectionAvailable = false;
                    upOffset++;
                } else {
                    upDirectionAvailable = false;
                }
            } else if (downCell != null && !downCell.isHit && downDirectionAvailable) {
                enemy.updateLists(row + downOffset + 1,65 + col, downCell.getShip());
                if (checkCell(downCell, downCell.getShip(), enemy, player)) {
                    leftDirectionAvailable = false;
                    downOffset++;
                } else {
                    downDirectionAvailable = false;
                }

            } else if (leftCell != null && !leftCell.isHit && leftDirectionAvailable) {
                enemy.updateLists(row + 1,65 + col - leftOffset, leftCell.getShip());
                if (checkCell(leftCell, leftCell.getShip(), enemy, player)) {
                    upDirectionAvailable = false;
                    downDirectionAvailable = false;
                    leftOffset++;
                } else {
                    leftDirectionAvailable = false;
                }
            } else {
                if (rightCell != null) {
                    enemy.updateLists(row + 1,65 + col + rightOffset, rightCell.getShip());
                    if (checkCell(rightCell, rightCell.getShip(), enemy, player)) {
                        upDirectionAvailable = false;
                        downDirectionAvailable = false;
                        rightOffset++;
                    }
                }
            }
        }
        mainController.disableInput(false);
    }

    private boolean checkAdjacentCells() {
        return !(upCell != null && upCell.isHit && upCell.getShip() != null) && !(downCell != null && downCell.isHit && downCell.getShip() != null) && !(leftCell != null && leftCell.isHit && leftCell.getShip() != null) && !(rightCell != null && rightCell.isHit && rightCell.getShip() != null);
    }

    private Cell searchGrid(int row, int col, ObservableList<Node> children) {
        if (row < 0 || row > 9 || col < 0 || col > 9) {
            return null;
        } else {
            boolean flag = true;
            for (Node node : children) {
                if (flag) {
                    flag = false;
                    continue;
                }
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                    return (Cell) node;
                }
            }
        }
        return null;
    }

    private boolean checkCell(Cell cell, Ship ship, Player caller, Player opponent) {
        boolean result;
        if (ship != null) {
            ship.hit();
            if(mainController.enemyShipsScreenController != null)
            {
                mainController.enemyShipsScreenController.setLabels();
            }
            if (ship.getState() == ship_state.SUNK) {
                if (caller.getType() == player_type.ENEMY) {
                    lastShotHit = false;
                }
                caller.setTotalPoints(caller.getTotalPoints() + ship.getSinkPoints());
                opponent.setActiveShips(opponent.getActiveShips() - 1);
                if (opponent.getActiveShips() == 0) {
                    mainController.showWinner(caller);
                }
            }
            caller.setTotalPoints(caller.getTotalPoints() + ship.getHitPoints());
            caller.setSuccessfulShots(caller.getSuccessfulShots() + 1);
            cell.setFill(Paint.valueOf("#f2132a"));
            result = true;
        } else {
            cell.setFill(Paint.valueOf("#ffffff"));
            result = false;
        }
        caller.setTotalShots(caller.getTotalShots() + 1);
        if (caller.getTotalShots() == 40 && opponent.getTotalShots() == 40) {
            mainController.showWinner();
        }
        mainController.updateLabels();
        return result;
    }

    private void setDirections() {
        upDirectionAvailable = true;
        downDirectionAvailable = true;
        leftDirectionAvailable = true;
        upOffset = 1;
        downOffset = 1;
        leftOffset = 1;
        rightOffset = 1;
    }
}
