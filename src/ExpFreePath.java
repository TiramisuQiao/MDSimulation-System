import java.util.List;

public class ExpFreePath {
    public static void main(String[] args) {
        Generator pg = new Generator(100,100,5.23e-23);
        List<Particle> particles = pg.particlesWithMaxwellPDF();
        ParticleCollisionSystem.setL(12);
        ParticleCollisionSystem.simulationFreePath(particles,500,true);
        double MFP = ParticleCollisionSystem.getFreePath();
        System.out.println("Mean Free Path: " + MFP);
        double FTL = ParticleCollisionSystem.getAvgCollisions();
        System.out.println("Free time length " + FTL);
    }
}
