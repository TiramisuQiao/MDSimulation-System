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

        public double getNormSq() {
            return x * x + y * y;
        }
    }

    public static class Particle {
        private final int index;
        private final double mass;
        private final double radius;
        private int count = 0;
        private Vector position;
        private Vector velocity;
        private int color_red;
        private int color_green;
        private int color_blue;


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
            return 1000000;
        }

        public double collidesY(double y) {
            if (velocity.getY() > 0) {
                return ((y - radius - position.getY()) / velocity.getY());
            }
            if (velocity.getY() < 0) {
                return ((radius - position.getY()) / velocity.getY());
            }
            return Double.POSITIVE_INFINITY;
        }

        public double collides(Particle p) {
            if (this == p) {
                return Double.POSITIVE_INFINITY;
            }
            Vector dr = new Vector(this.position.getX() - p.position.getX(),
                    this.position.getY() - p.position.getY());
            Vector dv = new Vector(this.velocity.getX() - p.velocity.getX(),
                    this.velocity.getY() - p.velocity.getY());
            double dvdr = dv.dotProduct(dr);
            if (dvdr >= 0) {
                return Double.POSITIVE_INFINITY;
            }
            double dvdv = dv.dotProduct(dv);
            double drdr = dr.dotProduct(dr);
            double sigma = this.radius + p.radius;
            double dt = Math.pow(dvdr, 2) - dvdv * (drdr - Math.pow(sigma, 2));
            if (dt < 0) {
                return Double.POSITIVE_INFINITY;
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

        public int getColor_red() {
            return color_red;
        }

        public int getColor_green() {
            return color_green;
        }

        public int getColor_blue() {
            return color_blue;
        }

        public Vector getPosition() {
            return position;
        }

        public double getRadius() {
            return radius;
        }
    }

    public static class Event implements Comparable<Event> {
        private final double t;
        private final Particle a;
        private final Particle b;
        private int count_a = 0;
        private int count_b = 0;

        public Event(double t, Particle a, Particle b) {
            this.t = t;
            this.a = a;
            this.b = b;
            if (a != null) {
                count_a = a.getCollisionCount();
            }
            if (b != null) {
                count_b = b.getCollisionCount();
            }
        }

        public double getTime() {
            return t;
        }

//        public Particle getParticle1() {
//            return a;
//        }
//
//        public Particle getParticle2() {
//            return b;
//        }

        public int compareTo(Event E) {
            return Double.compare(t, E.getTime());
        }

        public boolean wasSuperveningEvent() {
            if (a != null && a.getCollisionCount() != count_a) {
                return true;
            }
            return b != null && b.getCollisionCount() != count_b;
        }

        public Particle getParticleA() {
            return a;
        }

        public Particle getParticleB() {
            return b;
        }
    }

    public static class ParticleCollisionSystem {
        private static final PriorityQueue<Event> event_line = new PriorityQueue<>();
        private static double L;
        private static double clock = 0.00;
        private static final List<Double> time_interval = new LinkedList<>();
        private static final double K_BOLTZMANN = 1.380_648_52e-23;

        public ParticleCollisionSystem(double L) {
            ParticleCollisionSystem.L = L;
        }

        private static void update(Particle p1, List<Particle> particles) {
            if (p1 == null) return;

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
            if (drawFig){
                event_line.offer(new Event(0, null, null));
            }
            while (!event_line.isEmpty()) {
                Event e = event_line.poll();
                if(clock >= t_max){
                    break;
                }
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
                    } else if (a == null && b != null) {
                        b.bounceY();
                    } else if (a == null && b == null) {
                        getDraw(particles, 0.5,drawFig);
                    }
                    update(a,particles);
                    update(b,particles);

                }
            }

        }


        public static void getDraw(List<Particle> particles, double drawFreq,boolean drawFig) {
            if (!drawFig){
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
            return (sum / particles.size()) / (3 * K_BOLTZMANN);
        }
    }

    public static class ReadFromFile {
        private final String dataFilePath;
        private final List<Particle> particles;

        public ReadFromFile(String dataFilePath) {
            this.dataFilePath = dataFilePath;
            particles = new LinkedList<>();
        }

        private void readDataFile() {
            File dataFile = new File(dataFilePath);
            Scanner scanner;
            try {
                scanner = new Scanner(dataFile);
            } catch (FileNotFoundException e) {
                System.out.println("No such file " + dataFilePath);
                return;
            }
            int n = scanner.nextInt();
            for (int i = 0; i < n; i++) {
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
        }

        public List<Particle> getParticles() {
            return new ArrayList<>(particles);
        }
    }

    public static class ExperimentRunner {
        private static List<Particle> particles;

        ExperimentRunner(String dataFilePath) {
            ReadFromFile rf = new ReadFromFile(dataFilePath);
            rf.readDataFile();
            particles = rf.getParticles();
        }

        public static void SimulationRunner( // List<Particle> particles,
                                             double L,
                                             double t_max,
                                             boolean drawFig){
            ParticleCollisionSystem pcs = new ParticleCollisionSystem(L);
            long startTime = System.currentTimeMillis();
            ParticleCollisionSystem.simulation(particles, t_max,drawFig);
            long stopTime = System.currentTimeMillis();
            long elapseTime = stopTime - startTime;
        }

        public static double getSystemTemperature() {
            return ParticleCollisionSystem.getTemperature(particles);
        }

        public static double getAvgCollisions() {
            return ParticleCollisionSystem.getAvgCollisions();
        }
    }

    public static void main(String[] args) {
        String options = "Brownian motion";
        if ( options.equals("Brownian motion")){
            boolean drawFig = false;
            if (drawFig){
                StdDraw.enableDoubleBuffering();
                StdDraw.setXscale(0,10);
                StdDraw.setYscale(0,10);
            }
            String dataFilePath = "data/brownian.txt";
            ExperimentRunner runner = new ExperimentRunner(dataFilePath);
            ExperimentRunner.SimulationRunner(10, 40,false);
        }

    }
}
