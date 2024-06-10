import java.io.FileNotFoundException;
import java.util.List;

public class ExpReverse {
    public static void main(String[] args) throws FileNotFoundException {
        String dataFilePath = "data/billiards5.txt";
        IOFile rf = new IOFile(dataFilePath);
        List<Particle> particles = rf.readDataFile();
        double r1x = particles.get(0).getPosition().getX();
        double r1y = particles.get(0).getPosition().getY();
        System.out.println("INTX:" + r1x);
        System.out.println("INTY:" + r1y);
        ParticleCollisionSystem.setL(10);
        ParticleCollisionSystem.SimulationReverse(particles,40);

    }
}
