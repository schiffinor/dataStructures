/*
The LifeSimulation class implements a simple game of life simulation. Pretty much like the project requires.
Just a bit cleaner and simpler utilizing methods from LandscapeFrame.
 */

import java.util.Random;
import javax.swing.*;

/**
 * @author      Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
 */
public class LifeSimulation {
    //Initiates the game.
    Landscape game = new Landscape(100, 200, 50);
    //Initiates game frame.
    LandscapeFrame display = new LandscapeFrame(game, 6);

    public static void main(String[] args) throws InterruptedException {
        Random R = new Random();
        //Initiates the game.
        Landscape game = new Landscape(100, 200, R.nextDouble(0,100));
        //Initiates game frame.
        LandscapeFrame display = new LandscapeFrame(game, 6);
        display.gameInit.setPause(false);
        int i = 0;
        while (!display.gameInit.getPaused()) {
            display.saveImage( "data/life_frame_" + String.format( "%03d", i ) + ".png" );
            ((JButton) display.win.getJMenuBar().getComponent(4)).doClick();
            Thread.sleep(300);
            i++;
        }

    }

}
