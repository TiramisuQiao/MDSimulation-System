import java.io.FileNotFoundException;
import java.util.List;

public class ExpDiffusion {
    public static void main(String[] args) throws FileNotFoundException {
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        String dataFilePath = "data/diffusion2.txt";
        IOFile rf = new IOFile(dataFilePath);
        List<Particle> particles = rf.readDataFile();
        ParticleCollisionSystem.setL(1);
        ParticleCollisionSystem.setDrawFreq(10);
        ParticleCollisionSystem.simulation(particles,3000,true);
    }
}
