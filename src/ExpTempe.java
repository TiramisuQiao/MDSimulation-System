import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class ExpTempe {
    public static void main(String[] args) {
        final double T = 98;
        Generator pg = new Generator(100,T,2.67e-23);
        List<Particle> particles = pg.particlesWithMaxwellPDF();
        ParticleCollisionSystem.setL(10);
        ParticleCollisionSystem.simulation(particles,4000,false);
        double temp = ParticleCollisionSystem.getTemperature(particles);
        System.out.println("The actual temperature is " + T);
        System.out.println("The experiment temperature is " + temp);
        // Read it in to the File:
        List<Double> temperature_interval = new LinkedList<>();
        for(int i = 1 ; i <= 40; i = i + 1){
            ParticleCollisionSystem.simulation(particles,i * 100,false);
            double temp0 = ParticleCollisionSystem.getTemperature(particles);
            temperature_interval.add(temp0);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("Temperature.txt"))) {
            for (Double t: temperature_interval) {
                writer.write(t.toString());
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
