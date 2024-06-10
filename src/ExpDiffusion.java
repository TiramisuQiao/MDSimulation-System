import java.io.FileNotFoundException;
import java.util.List;

public class ExpDiffusion {
    public static void main(String[] args) throws FileNotFoundException {
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 13);
        StdDraw.setYscale(0, 13);
        String dataFilePath = "data/diffusion3.txt";
        IOFile rf = new IOFile(dataFilePath);
        List<Particle> particles = rf.readDataFile();
        ParticleCollisionSystem.setL(13);
        ParticleCollisionSystem.setDrawFreq(2);
        ParticleCollisionSystem.simulation(particles,3000,true);
    }
}
