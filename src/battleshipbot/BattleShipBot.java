
package battleshipbot;

import java.awt.Point;
import battleship.CellState;
import battleship.BattleShip;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mnprtpsingh
 */
public class BattleShipBot {
    
    private final int gameSize;
    private final int numberOfShip;
    private final BattleShip battleShip;
    private final Random random;
    private final CellState[][] sea;
    private final double[][] prob;
    private final HashMap<Integer, Integer> shipsSunk;
    private int totalHits;
    
    public BattleShipBot(BattleShip b) {
        battleShip = b;
        gameSize = b.BOARDSIZE;
        random = new Random();

        sea = new CellState[gameSize][gameSize];
        prob = new double[gameSize][gameSize];
        for (int i = 0; i < gameSize; i++) {
            for (int j = 0; j < gameSize; j++) {
                sea[i][j] = CellState.Empty;
                prob[i][j] = 1.0 / 100;
            }
        }

        numberOfShip = battleShip.shipSizes().length;
        shipsSunk = new HashMap<>();
        for (int i = 0; i < numberOfShip; i++) {
            shipsSunk.put(battleShip.shipSizes()[i], 0);
        }
        totalHits = 0;
    }
    
    public boolean fireShot() {
        ArrayList<Point> cells = this.getCellsWithMaxProbablity();
        int x = random.nextInt(cells.size());
        Point target = cells.get(x);

        boolean hit = battleShip.shoot(target);
        if (hit) {
            this.sea[target.x][target.y] = CellState.Hit;
            this.totalHits++;
        } else {
            this.sea[target.x][target.y] = CellState.Miss;
        }

        this.setSunkShips();
        this.updateProbablities();
        return hit;
    }

    private ArrayList<Point> getCellsWithMaxProbablity() {
        double max = 0;
        ArrayList<Point> cells = new ArrayList<>();
        for (int i = 0; i < this.gameSize; i++) {
            for (int j = 0; j < this.gameSize; j++) {
                if (this.sea[i][j] != CellState.Empty) {
                    continue;
                }
                if (max < this.prob[i][j]) {
                    max = this.prob[i][j];
                    cells.clear();
                    cells.add(new Point(i, j));
                } else if (max == prob[i][j]) {
                    cells.add(new Point(i, j));
                }
            }
        }
        return cells;
    }

    private void updateProbablities() {

        double[][] arr = new double[this.gameSize][this.gameSize];
        double sum = 0;
        for (int i = 0; i < this.gameSize; i++) {
            for (int j = 0; j < this.gameSize; j++) {
                arr[i][j] = 0;
            }
        }

        for (int i = 0; i < this.gameSize; i++) {
            for (int j = 0; j < this.gameSize; j++) {

                // loop's for each ship in game
                for (int s = 0; s < this.numberOfShip; s++) {
                    int len = this.battleShip.shipSizes()[s];
                    if (this.shipsSunk.get(len) == 2) {
                        continue;
                    }
                    if (len != 3 && this.shipsSunk.get(len) == 1) {
                        continue;
                    }

                    // for horizontal ship orientation
                    boolean b = true;
                    boolean c = false;
                    double t = 0;
                    for (int k = 0; k < len && j + k < this.gameSize; k++) {
                        if (this.sea[i][j + k] == CellState.Miss) {
                            b = false;
                            break;
                        }
                        if (k == len - 1) {
                            c = true;
                        }
                        if (this.sea[i][j + k] == CellState.Empty) {
                            t++;
                        }
                    }
                    if (c) {
                        double p = (b) ? 2 * (len - t + 1) : -2.0 / len;
                        for (int k = 0; k < len; k++) {
                            if (this.sea[i][j + k] == CellState.Empty) {
                                arr[i][j + k] += p;
                            }
                        }
                        sum += p * t;
                    }

                    // for vertical ship orientation
                    b = true;
                    c = false;
                    t = 0;
                    for (int k = 0; k < len && i + k < this.gameSize; k++) {
                        if (this.sea[i + k][j] == CellState.Miss) {
                            b = false;
                            break;
                        }
                        if (k == len - 1) {
                            c = true;
                        }
                        if (this.sea[i + k][j] == CellState.Empty) {
                            t++;
                        }
                    }
                    if (c) {
                        double p = (b) ? 2 * (len - t + 1) : -2.0 / len;
                        for (int k = 0; k < len; k++) {
                            if (this.sea[i + k][j] == CellState.Empty) {
                                arr[i + k][j] += p;
                            }
                        }
                        sum += p * t;
                    }
                }
            }
        }

        // calculate probabilities
        for (int i = 0; i < this.gameSize; i++) {
            for (int j = 0; j < this.gameSize; j++) {
                this.prob[i][j] = arr[i][j] / sum;
            }
        }
    }

    private void setSunkShips() {
        int hits = this.totalHits;
        int shipsSunk = this.battleShip.numberOfShipsSunk();
        if (hits == 2 && shipsSunk == 1) {
            this.shipsSunk.put(2, 1);
            this.markShips();
        } else if (hits == 5 && shipsSunk == 2) {
            this.shipsSunk.put(2, 1);
            this.shipsSunk.put(3, 1);
            this.markShips();
        } else if (hits == 8 && shipsSunk == 3) {
            this.shipsSunk.put(2, 1);
            this.shipsSunk.put(3, 2);
            this.markShips();
        } else if (hits == 12 && shipsSunk == 4) {
            this.shipsSunk.put(2, 1);
            this.shipsSunk.put(3, 2);
            this.shipsSunk.put(4, 1);
            this.markShips();
        }
    }

    private void markShips() {
        for (int i = 0; i < this.gameSize; i++) {
            for (int j = 0; j < this.gameSize; j++) {
                if (this.sea[i][j] == CellState.Hit) {
                    this.sea[i][j] = CellState.Miss;
                }
            }
        }
    }
}
