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
        double timer = 0;
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
            System.out.println(clock);
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
                    if (p.getIndex() == 1) {
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
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"))) {
                for (Double fp : path_for_single) {
                    writer.write(fp.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


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
    public static List<Vector> getFinalVelocity(){
        return velocity_recording;
    }

}




