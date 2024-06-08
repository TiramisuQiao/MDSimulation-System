import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MDSimulation {
    // the Vector is used to help store the data in the particle
    public static class Vector {
        private final double x;
        private final double y;

        public Vector(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double dotProduct(Vector v) {
            return x * v.getX() + y * v.getY();
        }

        public Vector add(double scaler, Vector v) {
            return new Vector(x + scaler * v.getX(), y + scaler * v.getY());
        }
        public double getNormSq(){
            return x * x + y * y;
        }
    }

    public static class Particle implements Cloneable {
        private final int index;
        private final int color_red;
        private final int color_green;
        private final int color_blue;
        private final double mass;
        private final double radius;
        private int count = 0;
        private Vector position;
        private Vector velocity;


        public Particle(int index, double mass, double radius, int color_red, int color_green, int color_blue) {
            this.index = index;
            this.mass = mass;
            this.radius = radius;
            this.color_red = color_red;
            this.color_green = color_green;
            this.color_blue = color_blue;
        }

        public double collidesX(double x) {
            if (velocity.getX() > 0) {
                return ((x - radius - position.getX()) / velocity.getX());
            }
            if (velocity.getX() < 0) {
                return ((radius - position.getX()) / velocity.getX());
            }
            return -1;
        }

        public double collidesY(double y) {
            if (velocity.getY() > 0) {
                return ((y - radius - position.getY()) / velocity.getY());
            }
            if (velocity.getY() < 0) {
                return ((radius - position.getY()) / velocity.getY());
            }
            return -1;
        }

        public double collides(Particle p) {
            Vector dr = new Vector(this.position.getX() - p.position.getX(),
                    this.position.getY() - p.position.getY());
            Vector dv = new Vector(this.velocity.getX() - p.velocity.getX(),
                    this.velocity.getY() - p.velocity.getY());
            double dvdr = dv.dotProduct(dr);
            if (dvdr >= 0) {
                return -1;
            }
            double dvdv = dv.dotProduct(dv);
            double drdr = dr.dotProduct(dr);
            double sigma = this.radius + p.radius;
            double dt = Math.pow(dvdr, 2) - dvdv * (drdr - Math.pow(sigma, 2));
            if (dt < 0) {
                return -1;
            }
            return -((dvdr + Math.sqrt(dt)) / dvdv);

        }

        public void bounceX() {
            this.velocity = new Vector(-velocity.getX(), velocity.getY());
            this.count = this.count + 1;
        }

        public void bounceY() {
            this.velocity = new Vector(velocity.getX(), -velocity.getY());
            this.count = this.count + 1;
        }

        public void bounce(Particle p) {
            Vector dr = new Vector(this.position.getX() - p.position.getX(),
                    this.position.getY() - p.position.getY());
            Vector dv = new Vector(this.velocity.getX() - p.velocity.getX(),
                    this.velocity.getY() - p.velocity.getY());

            double dvdr = dv.dotProduct(dr);

            double j = (2 * mass * p.mass * dvdr) / ((mass + p.mass) * (radius + p.radius));
            double jx = j * dr.getX() / (radius + p.radius);
            double jy = j * dr.getY() / (radius + p.radius);

            this.velocity = new Vector(velocity.getX() - jx / mass,
                    velocity.getY() - jy / mass);
            p.velocity = new Vector(p.velocity.getX() + jx / p.mass,
                    p.velocity.getY() + jy / p.mass);

            this.count = this.count + 1;
            p.count = p.count + 1;
        }

        public int getCollisionCount() {
            return count;
        }

        public void setPosition(Vector position) {
            this.position = position;
        }

        public void setVelocity(Vector velocity) {
            this.velocity = velocity;
        }

        public void move(double time) {
            position = position.add(time, velocity);
        }

        public int getIndex() {
            return index;
        }
        public Vector getVelocity() {
            return velocity;
        }
        public double getMass() {
            return mass;
        }
    }

    public static class Event implements Comparable<Event> {
        private double t;
        private Particle a;
        private Particle b;
        private int count_a = 0;
        private int count_b = 0;

        public Event(double t, Particle a, Particle b) {
            this.t = t;
            this.a = a;
            this.b = b;
            if (a != null) {
                count_a = a.getCollisionCount();
            }
            if (b != null){
                count_b = b.getCollisionCount();
            }
        }

        public double getTime() {
            return t;
        }

        public Particle getParticle1() {
            return a;
        }

        public Particle getParticle2() {
            return b;
        }

        public int compareTo(Event E) {
            return Double.compare(t, E.getTime());
        }

        public boolean wasSuperveningEvent() {
            if (a == null) {
                return b.getCollisionCount() != count_b;
            }
            if (b == null) {
                return a.getCollisionCount() != count_a;
            }
            return a.getCollisionCount() != count_a || b.getCollisionCount() != count_b;
        }
    }

    public static class ParticleCollisionSystem {
        private static PriorityQueue<Event> event_line = new PriorityQueue<>();
        private static double L;
        private static double clock = 0.00;
        private static List<Double> time_interval = new LinkedList<>();
        private static final double K_BOLTZMANN = 1.380_648_52e-23;

        private static void move(List<Particle> particles, double time) {
            for (Particle p : particles) {
                p.move(time);
            }
        }

        private static boolean update(Particle a, Particle b) {
            if (a == null && b == null) {
                throw new IllegalArgumentException();
            }
            if (a != null && b != null) {
                a.bounce(b);
            } else if (a == null) {
                b.bounceY();
                return b.getIndex() != 1;
            } else {
                a.bounceX();
                return a.getIndex() != 1;
            }

            return true;

        }

        private static void predict_wall(Particle p, double t_max) {
            double tx = p.collidesX(L);
            double ty = p.collidesY(L);
            if (tx >= 0 && clock + tx <= t_max) {
                event_line.offer(new Event(clock + tx, p, null));
            }
            if (ty >= 0 && clock + ty <= t_max) {
                event_line.offer(new Event(clock + ty, null, p));
            }

        }

        private static void predict_two(Particle a, Particle b, double t_max) {
            double tt = a.collides(b);
            if (tt >= 0 && clock + tt <= t_max) {
                event_line.offer(new Event(clock + tt, a, b));
            }
        }

        private static void predict(Particle p, List<Particle> particles, double t_max) {
            predict_wall(p, t_max);
            for (Particle p1 : particles) {
                if (p.getIndex() < p1.getIndex()) {
                    predict_two(p, p1, t_max);
                }
            }

        }

        private static void predict(List<Particle> particles, double t_max) {
            for (Particle p : particles) {
                predict(p, particles, t_max);
            }
        }

        public static void simulation(
                List<Particle> particles,
                double box,
                double t_max,
                int e_max
        ) {
            L = box;
            Particle v1 = new Particle(-1, 0, 0, 0, 0, 0);
            Particle v2 = new Particle(0, 0, 0, 0, 0, 0);
            v1.setPosition(new Vector(0, 0));
            v2.setPosition(new Vector(L, L));
            v1.setVelocity(new Vector(0, 0));
            v2.setVelocity(new Vector(0, 0));

            predict(particles,t_max);
            double timer = 0.00;
            int step = 0;
            while (!event_line.isEmpty()) {
                // System.out.println("PASS ++");
                Event nextLevel = event_line.poll();

                assert nextLevel != null;
                if (nextLevel.wasSuperveningEvent()) {
                    continue;
                }
                if (e_max < (1 + step)) {
                    break;
                }
                step = step + 1;

                clock = nextLevel.getTime();

                move(particles, clock - timer);
                boolean next_step = update(nextLevel.a, nextLevel.b);

                time_interval.add(clock - timer);

                if (nextLevel.a != null) {
                    predict(nextLevel.a, particles, t_max);
                }
                if (nextLevel.b != null) {
                    predict(nextLevel.b, particles, t_max);
                }
                if (!next_step) {
                    event_line.clear();
                }

                timer = clock;

            }
            System.out.println("Avg: " + getAvgCollisions() );
            System.out.println("Tmp: " + getTemperature(particles));


        }
        public static double getAvgCollisions(){
            return time_interval.stream().mapToDouble(t -> t).average().orElse(Double.NaN);
        }
        public static double getTemperature(List<Particle> particles){
            double sum = particles.stream()
                    .map(p -> p.getMass() * p.getVelocity().getNormSq())
                    .mapToDouble(Double::doubleValue)
                    .sum();
            return (sum / particles.size()) / (3 * K_BOLTZMANN);
        }
    }

    public static class ReadFromFile {
        private String dataFilePath;
        private int N;
        private Queue<Particle> particles;

        public ReadFromFile(String dataFilePath) {
            this.dataFilePath = dataFilePath;
            particles = new LinkedList<>();
        }

        private boolean readDataFile() {
            File dataFile = new File(dataFilePath);
            Scanner scanner;
            try {
                scanner = new Scanner(dataFile);
            } catch (FileNotFoundException e) {
                System.out.println("No such file " + dataFilePath);
                return false;
            }
            N = scanner.nextInt();
            for (int i = 0; i < N; i++) {
                double rx = scanner.nextDouble();
                double ry = scanner.nextDouble();
                double vx = scanner.nextDouble();
                double vy = scanner.nextDouble();
                double radius = scanner.nextDouble();
                double mass = scanner.nextDouble();
                int color_red = scanner.nextInt();
                int color_green = scanner.nextInt();
                int color_blue = scanner.nextInt();
                Particle p = new Particle(i + 1, radius, mass, color_red, color_green, color_blue);
                p.setPosition(new Vector(rx, ry));
                p.setVelocity(new Vector(vx, vy));
                particles.add(p);
            }
            scanner.close();
            return true;
        }

        public List<Particle> getParticles() {
            return new ArrayList<>(particles);
        }
    }

    public static class ExperimentRunner {
        private static List<Particle> particles;
        String dataFilePath;

        ExperimentRunner(String dataFilePath) {
            ReadFromFile rf = new ReadFromFile(dataFilePath);
            rf.readDataFile();
            particles = rf.getParticles();
        }

        public static void SimulationRunner( // List<Particle> particles,
                                            double L,
                                            double t_max,
                                            int e_max){
            long startTime = System.currentTimeMillis();
            ParticleCollisionSystem.simulation(particles, L, t_max, e_max);
            long stopTime = System.currentTimeMillis();
            long elapseTime = stopTime - startTime;
        }
    }

    public static void main(String[] args) {
        String dataFilePath = "data/brownian.txt";
        ExperimentRunner runner = new ExperimentRunner(dataFilePath);
        ExperimentRunner.SimulationRunner(10,40000,4000000);
    }
}
