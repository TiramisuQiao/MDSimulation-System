import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ParticleCollisionSystem {
    private static final PriorityQueue<Event> event_line = new PriorityQueue<>();
    private static double L;
    private static double clock = 0.00;
    private static final List<Double> time_interval = new LinkedList<>();
    private static final List<Double> distance_interval = new LinkedList<>();
    private static final List<Double> path_for_single = new LinkedList<>();
    private static final List<Vector> velocity_recording = new LinkedList<>();
    private static final double K_BOLTZMANN = 1.380_648_52e-23;
    private static double drawFreq = 2;
    public static void setL(double l) {
        L = l;
    }
    public static void setDrawFreq(double f) {
        drawFreq = f;
    }


    private static void update(Particle p1, List<Particle> particles) {
        if (p1 == null) {
            return;
        }

        for (Particle p : particles) {
            double dt = p1.collides(p);
            if (dt != Double.POSITIVE_INFINITY)
                event_line.offer(new Event(clock + dt, p1, p));
        }
        event_line.offer(new Event(clock + p1.collidesX(L), p1, null));
        event_line.offer(new Event(clock + p1.collidesY(L), null, p1));
    }
    public static void simulation(List<Particle> particles,
                                  double t_max,
                                  boolean drawFig
    ) {
        for (Particle p : particles) {
            update(p, particles);
        }
        if (drawFig) {
            event_line.offer(new Event(0, null, null));
        }
        while (!event_line.isEmpty()) {
            Event e = event_line.poll();
            if (clock >= t_max) {
                break;
            }
            // System.out.println(clock);
            if (!e.wasSuperveningEvent()) {
                Particle a = e.getParticleA();
                Particle b = e.getParticleB();
                double interval = e.getTime() - clock;
                time_interval.add(interval);
                for (Particle p : particles) {
                    p.move(interval);
                }
                clock = e.getTime();
                if(a != null && b != null) {
                    a.bounce(b);
                }else if(a != null && b == null) {
                    a.bounceX();
                }else if(a == null && b != null) {
                    b.bounceY();
                }else if(a == null && b == null) {
                    getDraw(particles, drawFreq, drawFig);
                }
                update(a, particles);
                update(b, particles);

            }
        }
        for (Particle p: particles) {
            velocity_recording.add(p.getVelocity());
        }

    }

    public static void simulationFreePath(List<Particle> particles,
                                          double t_max,
                                          boolean writeFile) {
        for (Particle p : particles) {
            update(p, particles);
        }
        while (!event_line.isEmpty()) {
            Event e = event_line.poll();
            if (clock >= t_max) {
                break;
            }
            // System.out.println(clock);
            if (!e.wasSuperveningEvent()) {
                Particle a = e.getParticleA();
                Particle b = e.getParticleB();
                double interval = e.getTime() - clock;
                time_interval.add(interval);
                for (Particle p : particles) {
                    Vector oldPosition = p.getPosition().clone();
                    p.move(interval);
                    Vector dr = new Vector(p.getPosition().getX() - oldPosition.getX(),
                            p.getPosition().getY() - oldPosition.getY());
                    double euclidean_distance = Math.sqrt(dr.getNormSq());
                    distance_interval.add(euclidean_distance);
                    if (p.getIndex() == 1 && clock > 0 ) {
                        path_for_single.add(euclidean_distance);
                    }
                }
                clock = e.getTime();
                if (a != null && b != null) {
                    a.bounce(b);
                } else if (a != null && b == null) {
                    a.bounceX();
                } else {
                    b.bounceY();
                }
                update(a, particles);
                update(b, particles);

            }
        }
        if (writeFile) {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("freepath.txt"))) {
                for (Double fp : path_for_single) {
                    writer.write(fp.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    public static void SimulationReverse(List<Particle> particles,
                                         int steps_max){
        int steps = 0;
        for (Particle p : particles) {
            update(p, particles);
        }
        while (!event_line.isEmpty()) {
            Event e = event_line.poll();
            // System.out.println(clock);
            if(steps >= steps_max ) {
                break;
            }
            steps += 1;
            if (!e.wasSuperveningEvent()) {
                Particle a = e.getParticleA();
                Particle b = e.getParticleB();
                double interval = e.getTime() - clock;
                time_interval.add(interval);
                for (Particle p : particles) {
                    p.move(interval);
                }
                clock = e.getTime();

                if (a != null && b != null) {
                    a.bounce(b);
                } else if (a != null && b == null) {
                    a.bounceX();
                } else {
                    b.bounceY();
                }
                update(a, particles);
                update(b, particles);

            }
        }
        System.out.println("steps: " + steps);
        System.out.println("Reverse all the particles");
        for(Particle p : particles) {
            double rev_x = -p.getVelocity().getX();
            double rev_y = -p.getVelocity().getY();
            p.setVelocity(new Vector(rev_x, rev_y));
        }

        int rev_steps = 0;
        event_line.clear();
        clock = 0;
        for (Particle p : particles) {
            update(p, particles);
        }
        while (!event_line.isEmpty()) {
            Event e = event_line.poll();
            // System.out.println(clock);
            if(rev_steps >= steps_max ) {
                break;
            }
            rev_steps = rev_steps + 1;
            if (!e.wasSuperveningEvent()) {
                Particle a = e.getParticleA();
                Particle b = e.getParticleB();
                double interval = e.getTime() - clock;
                time_interval.add(interval);
                for (Particle p : particles) {
                    p.move(interval);
                }
                clock = e.getTime();
                if (a != null && b != null) {
                    a.bounce(b);
                } else if (a != null && b == null) {
                    a.bounceX();
                } else {
                    b.bounceY();
                }
                update(a, particles);
                update(b, particles);

            }
        }
        System.out.println("Reverse steps: " + rev_steps);
        double r1xx = particles.get(0).getPosition().getX();
        double r1yy = particles.get(0).getPosition().getY();
        System.out.println("After Reverse 1st Particles Position x: " + r1xx);
        System.out.println("After Reverse 1st Particles Position y: " + r1yy);


    }
    public static double SimulationPressure(List<Particle> particles,
                                          double t_max){
        double virialSum = 0;
        for (Particle p : particles)
        {
            update(p, particles);
        }
        while (!event_line.isEmpty()) {
            Event e = event_line.poll();
            if (clock >= t_max) {
                break;
            }
            // System.out.println(clock);
            if (!e.wasSuperveningEvent()) {
                Particle a = e.getParticleA();
                Particle b = e.getParticleB();
                double interval = e.getTime() - clock;
                time_interval.add(interval);
                for (Particle p : particles) {
                    p.move(interval);
                }
                List<Particle> deepCopiedParticles = deepCopyParticles(particles);
                clock = e.getTime();
                if(a != null && b != null) {
                    a.bounce(b);
                }else if(a != null && b == null) {
                    a.bounceX();
                }else if(a == null && b != null) {
                    b.bounceY();
                }else if(a == null && b == null) {
                    getDraw(particles, drawFreq, false);
                }
                update(a, particles);
                update(b, particles);
                virialSum += calculateVirialTerm(deepCopiedParticles, a, b);
            }
        }
        for (Particle p: particles) {
            velocity_recording.add(p.getVelocity());
        }
        return virialSum;
    }


    private static double calculateVirialTerm(List<Particle> particles, Particle a, Particle b) {
        double virialTerm = 0.0;
        if (a != null && b != null) {
            Vector r_ij = new Vector(b.getPosition().getX() - a.getPosition().getX(),
                    b.getPosition().getY() - a.getPosition().getY());
            Vector deltaP = new Vector(b.getVelocity().getX() - a.getVelocity().getX(),
                    b.getVelocity().getY() - a.getVelocity().getY());
            virialTerm = r_ij.dotProduct(deltaP);
        }
        return virialTerm;
    }

    private static List<Particle> deepCopyParticles(List<Particle> particles) {
        List<Particle> copiedParticles = new ArrayList<>();
        for (Particle p : particles) {
            copiedParticles.add(p.deepCopy());
        }
        return copiedParticles;
    }
    public static List<Particle> getReverse(List<Particle> particles) {
        for(Particle p : particles) {
            double rev_x = -p.getVelocity().getX();
            double rev_y = -p.getVelocity().getY();
            p.setVelocity(new Vector(rev_x, rev_y));
        }
        return particles;
    }


    public static void getDraw(List<Particle> particles, double drawFreq, boolean drawFig) {
        if (!drawFig) {
            return;
        }
        StdDraw.clear();
        for (Particle p : particles) {
            StdDraw.setPenColor(p.getColor_red(), p.getColor_green(), p.getColor_blue());
            double rx = p.getPosition().getX();
            double ry = p.getPosition().getY();
            double r = p.getRadius();
            StdDraw.filledCircle(rx, ry, r);
        }
        StdDraw.show();
        StdDraw.pause(20);
        event_line.offer(new Event(clock + 1.0 / drawFreq, null, null));
    }

    public static double getAvgCollisions() {
        return time_interval.stream().mapToDouble(t -> t).average().orElse(Double.NaN);
    }

    public static double getTemperature(List<Particle> particles) {
        double sum = particles.stream()
                .map(p -> p.getMass() * p.getVelocity().getNormSq())
                .mapToDouble(Double::doubleValue)
                .sum();
        return (sum / particles.size()) / (2 * K_BOLTZMANN);
    }

    public static double getFreePath() {
        return distance_interval.stream().mapToDouble(d -> d).average().orElse(Double.NaN);
    }
    public static double[] getFinalVelocity(){
        // return velocity_recording;
        double[] a = new double[velocity_recording.size() + 1];
        for(int i = 0; i < velocity_recording.size(); i++){
            a[i] = Math.sqrt(velocity_recording.get(i).getNormSq());
        }
        return a;
    }

}





