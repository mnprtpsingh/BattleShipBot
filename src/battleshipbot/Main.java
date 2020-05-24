
package battleshipbot;

import battleship.BattleShip;

/**
 *
 * @author mnprtpsingh
 */
public class Main {
    
    static final int NUMBER_OF_GAMES = 1000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int totalShots = 0;
        System.out.println(BattleShip.version());
        
        for (int game = 0; game < NUMBER_OF_GAMES; game++) {
            
            BattleShip battleShip = new BattleShip();
            BattleShipBot bot = new BattleShipBot(battleShip);

            while (!battleShip.allSunk()) {
                bot.fireShot();
            }
            
            int gameShots = battleShip.totalShotsTaken();
            totalShots += gameShots;
        }
        
        System.out.printf("The Average # of Shots required in %d games to sink all Ships = %.2f\n", NUMBER_OF_GAMES, (double) totalShots / NUMBER_OF_GAMES);
    }
    
}
