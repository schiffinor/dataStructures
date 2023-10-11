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
public class AgentSimulation {
    //Initiates the game.
    final Landscape game = new Landscape(100, 200);
    //Initiates game frame.
    LandscapeFrame display = new LandscapeFrame(game, 1);

    public static void main(String[] args) throws InterruptedException {
        Landscape scape = new Landscape(500, 500);
        Random gen = new Random();
        //Initiates the game.
        //Initiates game frame.
        // Creates 100 SocialAgents and 100 AntiSocialAgents
        for (int i = 0; i < 100; i++) {
            scape.addAgent(new SocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(), 30));
            scape.addAgent(new AntiSocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(), 22));
        }
        LandscapeFrame display = new LandscapeFrame(scape,1);
        display.gameInit.setPause(false);
        int i = 0;
        while (!display.gameInit.getPaused()) {
            display.saveImage( "data/life_frame_" + String.format( "%03d", i ) + ".png" );
            ((JButton) display.win.getJMenuBar().getComponent(3)).doClick();
            Thread.sleep(50);
            i++;
        }

    }

}