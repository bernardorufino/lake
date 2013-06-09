package utils;

public final class Point {
    private final double x, y;
    
    public static Point xy(double x, double y) {
        return new Point(x, y);
    }
    
    public static Point polar(double radius, double angle) {
        return Point.xy(
            radius * Math.cos(angle), 
            radius * Math.sin(angle)
        );
    }
    
    public static Point origin() {
        return xy(0, 0);
    }
    
    private Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double x() { return x; }
    public double y() { return y; }
    
    public double radius() { return Math.sqrt(x*x + y*y); }
    
    public double angle() {
        double angle = Math.atan(y/x);
        if (x < 0) angle += Math.PI;
        return angle;
    }
    
    public Point addX(double delta) { return Point.xy(x + delta, y); }
    public Point addY(double delta) { return Point.xy(x, y + delta); }    
    
    public Point plus(Vector2D v) {
        return Point.xy(x() + v.xComponent(), y() + v.yComponent());
    }
    
    public Vector2D minus(Point p) {
        return Vector2D.xy(x - p.x(), y - p.y());
    }
    
    public String toString() {
        return "(" + Utils.format(x, 2) + "; " + Utils.format(y, 2) + ")";
    }

}
