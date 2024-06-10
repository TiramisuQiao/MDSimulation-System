import java.util.List;

public class ExpMaxwell {
    public static void main(String[] args) {
        Generator pg = new Generator(100,298,2.67e-23);
        List<Particle> particles = pg.particlesWithMaxwellPDF();

        double SIGMA = pg.getSigma();
        double cal_mu = SIGMA * Math.sqrt(Math.PI / 2);
        double cal_sigma = (2 - Math.PI / 2 ) * Math.pow(SIGMA,2);
        System.out.println("Calculated mu: " + cal_mu);
        System.out.println("Calculated sigma: " + cal_sigma);

        ParticleCollisionSystem.setL(10);
        ParticleCollisionSystem.simulation(particles,4000,false);

        double[] a = new double[particles.size()];
        a = ParticleCollisionSystem.getFinalVelocity();

        double exp_mu = StdStats.mean(a);
        double exp_sigma = StdStats.varp(a);
        System.out.println("Experiment mu: " + exp_mu);
        System.out.println("Experiment sigma: " + exp_sigma);

    }
}
