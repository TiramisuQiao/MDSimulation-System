//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//
//
//public class MDSimulation {
//    public static void main(String[] args) {
//        Scanner input = new Scanner(System.in);
//        String options = input.nextLine();
//        // String options = "Free path and free time";
//        if (options.equals("Brownian motion")) {
//            boolean drawFig = true;
//            if (drawFig) {
//                StdDraw.enableDoubleBuffering();
//                StdDraw.setXscale(0, 4);
//                StdDraw.setYscale(0, 4);
//            }
//            String dataFilePath = "data/brownian.txt";
//            ExperimentRunner runner = new ExperimentRunner(dataFilePath);
//            ExperimentRunner.SimulationRunner(4, 400, drawFig);
//        } else if (options.equals("Free path and free time")) {
//            boolean WriteFile = true;
//            String dataFilePath = "data/brownian.txt";
//            ExperimentRunner runner = new ExperimentRunner(dataFilePath);
//            ExperimentRunner.SimulationRunnerFreePath(20, 4000, WriteFile);
//            double meanFreePath = ExperimentRunner.getFreePath();
//            System.out.println("Mean Free Path: " + meanFreePath);
//            double meanFreeTime = ExperimentRunner.getAvgCollisions();
//            System.out.println("Mean Free Time: " + meanFreeTime);
//
//        } else if (options.equals("Collision frequency")) {
//            boolean drawFig = false;
//            String dataFilePath = "data/brownian.txt";
//            ExperimentRunner runner = new ExperimentRunner(dataFilePath);
//            ExperimentRunner.SimulationRunner(10, 4000, drawFig);
//            double intervalBetweenCollisions = ExperimentRunner.getAvgCollisions();
//            double CollisionFreq = 1 / intervalBetweenCollisions;
//            System.out.println("The Collision frequency is " + CollisionFreq);
//        } else if (options.equals("Root mean-square velocity")) {
//            boolean WriteFile = false;
//            String dataFilePath = "data/brownian.txt";
//            ExperimentRunner runner = new ExperimentRunner(dataFilePath);
//            ExperimentRunner.SimulationRunnerFreePath(10, 4000, WriteFile);
//            double meanFreePath = ExperimentRunner.getFreePath();
//            double intervalBetweenCollisions = ExperimentRunner.getAvgCollisions();
//            double CollisionFreq = 1 / intervalBetweenCollisions;
//            double RMS_velocity = meanFreePath * CollisionFreq;
//            System.out.println("Root mean-square velocity is " + RMS_velocity);
//        } else if (options.equals("Root mean-square velocity-Temperature")) {
//            final double T = 298.15;
//            double RMS_velocity = ExperimentRunner.getRootMeanSquare(T);
//            System.out.println("Root mean-square velocity is " + RMS_velocity);
//        } else if (options.equals("Maxwell-Boltzmann distribution")) {
//            boolean drawFig = false;
//            Generator pg = new Generator(100,98,5.23e-23);
//            List<Particle> particles = new LinkedList<>();
//            particles = pg.particlesWithMaxwellPDF();
//            ParticleCollisionSystem pcs = new ParticleCollisionSystem(12);
//            ParticleCollisionSystem.simulation(particles,4000,drawFig);
//            List<Vector> recordings = ParticleCollisionSystem.getFinalVelocity();
//
//
//            List<Double> velocities = new ArrayList<>();
//            for(Vector v : recordings) {
//                velocities.add(Math.sqrt(v.getNormSq()));
//            }
//            double[] veloArray = velocities.stream().mapToDouble(Double::doubleValue).toArray();
//            double mean = StdStats.mean(veloArray);
//            double sigma = StdStats.stddev(veloArray);
//            final double CONST_SIGMA = pg.getSigma();
//            final double EXP_MU = CONST_SIGMA * Math.sqrt(Math.PI / 2);
//            final double EXP_SIGMA = (2 - Math.PI / 2 ) * Math.pow(CONST_SIGMA,2);
//            System.out.println("Except average: " + EXP_MU);
//            System.out.println("Except sigma: " + EXP_SIGMA);
//            System.out.println("Calculated average: " + mean);
//            System.out.println("Calculated sigma: " + sigma);
//        } else if (options.equals("Pressure")){
//
//        } else if(options.equals("Temperature-Maxwell")){
//            boolean drawFig = false;
//            double Exp_temp = 98.0;
//            Generator pg = new Generator(100,Exp_temp,5.23e-23);
//            List<Particle> particles = new LinkedList<>();
//            particles = pg.particlesWithMaxwellPDF();
//            ParticleCollisionSystem pcs = new ParticleCollisionSystem(12);
//            ParticleCollisionSystem.simulation(particles,4000,drawFig);
//            double temperature = ParticleCollisionSystem.getTemperature(particles);
//            System.out.println("Expected temperature: " + Exp_temp);
//            System.out.println("Calculated temperature: " + temperature);
//        } else if (options.equals("Temperature-Plot")){
//            double Exp_temp = 98.0;
//            Generator pg = new Generator(100,Exp_temp,5.23e-23);
//            List<Particle> particles = new LinkedList<>();
//            particles = pg.particlesWithMaxwellPDF();
//            List<Double> temperature_interval = new LinkedList<>();
//            ParticleCollisionSystem pcs = new ParticleCollisionSystem(12);
//            boolean drawFig = false;
//            for (int i = 0; i < 4000; i++) {
//                if(i % 100 == 0){
//                    ParticleCollisionSystem.simulation(particles,i,drawFig);
//                    double temperature = ParticleCollisionSystem.getTemperature(particles);
//                    temperature_interval.add(temperature);
//                }
//            }
//            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("Temperature.txt"))) {
//                for (Double t: temperature_interval) {
//                    writer.write(t.toString());
//                    writer.newLine();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } else if (options.equals("Diffusion3")){
//            boolean drawFig = true;
//            if (drawFig) {
//                StdDraw.enableDoubleBuffering();
//                StdDraw.setXscale(0, 50);
//                StdDraw.setYscale(0, 50);
//            }
//            String dataFilePath = "data/diffusion3.txt";
//            ExperimentRunner runner = new ExperimentRunner(dataFilePath);
//            ExperimentRunner.SimulationRunner(50, 3000, drawFig);
//        } else if (options.equals("Diffusion")){
//            boolean drawFig = true;
//            if (drawFig) {
//                StdDraw.enableDoubleBuffering();
//                StdDraw.setXscale(0, 1);
//                StdDraw.setYscale(0, 1);
//            }
//            String dataFilePath = "data/diffusion.txt";
//            ExperimentRunner runner = new ExperimentRunner(dataFilePath);
//            ExperimentRunner.SimulationRunner(1, 3000, drawFig);
//        }
//
//    }
//}
