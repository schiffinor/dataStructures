import java.util.Random;

/**
 * The `AdjustableSimulation` class represents a simulation program that creates a landscape
 * and populates it with both social and anti-social agents.
 * The simulation runs and displays the agents' behavior within the landscape.
 * <p>
 * However, additionally, this class is completely customizable in terms of many simulation settings.
 * Up to 20,000 concurrent agents can be added to the simulation and run relatively quickly.
 * This is a result of the kind-of 2-D HashSet implementation in the sectorMap.
 */
public class AdjustableSimulation {
    public static void main(String[] args) {
        //Create a landscape with dimensions 500x500
        Landscape scape = new Landscape(500, 500);

        //Create a random number generator
        Random gen = new Random();

        //Populate the landscape with 100 SocialAgents and 100 AntiSocialAgents
        for (int i = 0; i < scape.agentCount; i++) {
            scape.addAgent(new SocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(), scape.getSocialRadius()));
            scape.addAgent(new AntiSocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(),scape.getAntiSocialRadius()));
        }
        //Create a display frame for the landscape and set the game scale to 1.
        LandscapeFrame display = new LandscapeFrame(scape,1);
    }
}
