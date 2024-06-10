import java.util.List;

public class ExpPress {
    public static void main(String[] args) {
        double L = 100;
        double volume = L * L;
        final double K_B = 1.380649e-23;
        Generator pg = new Generator(100,100,5.23e-23);
        List<Particle> particles = pg.particlesWithMaxwellPDF();
        int N = particles.size();
        double density = N / volume;
        ParticleCollisionSystem.setL(L);
        double temperature = ParticleCollisionSystem.getTemperature(particles);
        double virialSum = ParticleCollisionSystem.SimulationPressure(particles,4000);

        double idealGasPressure = density * K_B * temperature;

        double virialPressure = (1.0 / (2 * volume)) * virialSum;

        double pressure = idealGasPressure + virialPressure;

        System.out.println("Pressure: " + pressure);
    }
}
