import java.util.Random;

public class AdjustableSimulation {
    public static void main(String[] args) {
        Landscape scape = new Landscape(500, 500);
        Random gen = new Random();

        // Creates 100 SocialAgents and 100 AntiSocialAgents
        for (int i = 0; i < scape.agentCount; i++) {
            scape.addAgent(new SocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(), scape.getSocialRadius()));
            scape.addAgent(new AntiSocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(),scape.getAntiSocialRadius()));
        }
        LandscapeFrame display = new LandscapeFrame(scape,1);
    }
}
