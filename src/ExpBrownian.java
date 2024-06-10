import java.io.FileNotFoundException;
import java.util.List;

public class ExpBrownian {
    // This is the 1st experiment to simulate the Brownian motion
    public static void main(String[] args) throws FileNotFoundException {
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 5);
        StdDraw.setYscale(0, 5);
        String dataFilePath = "data/brownian.txt";
        IOFile rf = new IOFile(dataFilePath);
        List<Particle> particles = rf.readDataFile();
        ParticleCollisionSystem.setL(5);
        ParticleCollisionSystem.setDrawFreq(2);
        ParticleCollisionSystem.simulation(particles,500,true);
    }
}
