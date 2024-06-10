import java.util.List;

public class ExpRMS {
    public static void main(String[] args) {
        final double T = 98;
        final double R = 8.314;
        final double M = 0.0319998;
        double RMS_velocity = Math.sqrt((3 * R *  T)/M);
        System.out.println("Root mean-square(calculation) velocity is " + RMS_velocity);
        Generator pg = new Generator(1000,98,2.67e-23);
        List<Particle> particles = pg.particlesWithMaxwellPDF();
        ParticleCollisionSystem.setL(10);
        ParticleCollisionSystem.simulationFreePath(particles,0.02,true);
        double MFP = ParticleCollisionSystem.getFreePath();
        System.out.println("Mean Free Path: " + MFP);
        double CF = 1 / ParticleCollisionSystem.getAvgCollisions();
        System.out.println("Collision Frequency: " + CF);
        double RMS_experiment = MFP * CF;
        System.out.println("Root mean-square(experiment) velocity is " + RMS_experiment);
    }
}
