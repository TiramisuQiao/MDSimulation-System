import java.util.List;
public class ExpColFreq {
    public static void main(String[] args) {
        Generator pg = new Generator(100,100,5.23e-23);
        List<Particle> particles = pg.particlesWithMaxwellPDF();
        ParticleCollisionSystem.setL(12);
        ParticleCollisionSystem.simulationFreePath(particles,500,false);
        double CF = 1 / ParticleCollisionSystem.getAvgCollisions();
        System.out.println("Collision Frequency: " + CF);
    }
}
