package utils;

public final class Vector2D {
    private Point terminal;

    public static Vector2D xy(double x, double y) {
        return new Vector2D(Point.xy(x, y));
    }

    public static Vector2D polar(double radius, double angle) {
        return new Vector2D(Point.polar(radius, angle));
    }

    public static Vector2D direction(double angle) {
        return polar(1, angle);
    }

    private Vector2D(Point terminal) {
        this.terminal = terminal;
    }

    public double xComponent() { return terminal.x(); }
    public double yComponent() { return terminal.y(); }
    public double magnitude() { return terminal.radius(); }

    public Vector2D unitary() {
        return times(1/magnitude());
    }

    public Vector2D plus(Vector2D v) {
        return Vector2D.xy(xComponent() + v.xComponent(), yComponent() + v.yComponent());
    }

    public Vector2D times(double t) {
        return Vector2D.xy(xComponent() * t, yComponent() * t);
    }

    public String toString() {
        return terminal.toString();
    }

}
