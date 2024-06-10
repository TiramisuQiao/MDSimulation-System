public class Vector {
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

    @Override
    protected Vector clone() {
        return new Vector(this.x, this.y);
    }
}