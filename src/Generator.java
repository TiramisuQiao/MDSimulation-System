import java.util.LinkedList;
import java.util.List;

public class Generator {
    final int N;
    List<Particle> particles;
    private static double T;
    private static double m;
    static final double K_BOLTZMANN = 1.380_648_52e-23;
    public Generator(int N,double T,double m) {
        particles = new LinkedList<>();
        this.N = N;
        this.T = T;
        this.m = m;
    }

    public List<Particle> particlesWithMaxwellPDF() {
        for (int i = 0; i < N; i++) {
            double rx = StdRandom.uniformDouble(1,99999)/10000;
            double ry = StdRandom.uniformDouble(1,99999)/10000;
            double sigma = Math.sqrt(K_BOLTZMANN * T / m);
            double vx = StdRandom.gaussian(0,sigma);
            double vy = StdRandom.gaussian(0,sigma);
            double mass = m;
            double radius = 0.01;
            Particle p = new Particle(i,mass,radius,255,255,255);
            Vector velocity = new Vector(vx, vy);
            p.setVelocity(velocity);
            Vector position = new Vector(rx, ry);
            p.setPosition(position);
            particles.add(p);
        }
        return particles;
    }
    public double getSigma(){
        return Math.sqrt(K_BOLTZMANN * T / m);
    }
}