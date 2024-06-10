public class Event implements Comparable<Event> {
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