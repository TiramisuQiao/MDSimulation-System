public class Particle {
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
        if (this == p) {
            return Double.POSITIVE_INFINITY;
        }
        Vector dr = new Vector(this.position.getX() - p.getPosition().getX(),
                this.position.getY() - p.getPosition().getY());
        Vector dv = new Vector(this.velocity.getX() - p.getVelocity().getX(),
                this.velocity.getY() - p.getVelocity().getY());
        double dvdr = dv.dotProduct(dr);
        if (dvdr >= 0) {
            return Double.POSITIVE_INFINITY;
        }
        double dvdv = dv.dotProduct(dv);
        double drdr = dr.dotProduct(dr);
        double sigma = this.radius + p.getRadius();
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
        Vector dr = new Vector(this.position.getX() - p.getPosition().getX(),
                this.position.getY() - p.getPosition().getY());
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

    public Particle deepCopy() {
        Particle p =  new Particle(index,mass,radius,color_red,color_green,color_blue);
        p.setVelocity(velocity.clone());
        p.setPosition(position.clone());
        return p;
    }
}
