package base;

import utils.Point;
import utils.Vector2D;

public class Boat {
    public enum State {
        Stuck, Sunk, Moving
    }
    
    public static class InitialPositionNotSet extends RuntimeException { /* Nothing */ }
    public static class DirectionNotSet extends RuntimeException { /* Nothing */ }
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) { super(message); } 
    }

    private State state;
    private String name;
    private Point position;
    private Vector2D direction;
    private double speed;
    private final double radius;
    private Point initialPosition;
    private final double maxSpeed;
    private final double acceleration;

    public Boat(String name, double radius, double initialSpeed, double maxSpeed,
        double acceleration) {
        this.name = name;
        this.radius = radius;
        this.speed = initialSpeed;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.state = State.Moving;
        this.initialPosition = null;
        this.direction = null;
        validate();
    }
    
    private void validate() {
        if (speed > maxSpeed)
            throw new ValidationException("initialSpeed cannot be > maxSpeed");
        if (speed < 0) 
            throw new ValidationException("speed must be >= 0");
        if (acceleration < 0) 
            throw new ValidationException("acceleration must be >= 0");
        if (radius < 1) 
            throw new ValidationException("radius must be >= 1");
    }
    
    /* default */ final void stuck() {
        speed = 0;
        this.state = State.Stuck;
    }

    /* default */ final void sunk() {
        speed = 0;
        this.state = State.Sunk;
    }
    
    public boolean isMoving() { return state == State.Moving; }
    public boolean isStuck() { return state == State.Stuck; }
    public boolean isSunk() { return state == State.Sunk; }
    
    public String getName() { return name; }
    public State getState() { return state; }
    public double getRadius() { return this.radius; }
    public double getSpeed() { return speed; }
    public double getMaxSpeed() { return maxSpeed; }

    public Point getPosition() {
        if (initialPosition == null) throw new InitialPositionNotSet();
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
        if (this.initialPosition == null) initialPosition = position;
    }
    
    public Vector2D getDirection() {
        if (direction == null) throw new DirectionNotSet(); 
        return direction;
    }
    
    public void setDirection(Vector2D direction) {
        this.direction = direction.unitary();
    }
    
    /**
     * Responsavel por mover o barco de um certo intervalo de tempo dt
     * @param dt
     */
    public final void move(double dt) {
        if (state != State.Moving) return;
        double tmax = (maxSpeed - speed) / acceleration;
        double displacement;
        // Se a velocidade ja e maxima e MU
        if (speed == maxSpeed) displacement = speed * dt;
        // Nao ocorrera colisao dentro de dt
        else if (tmax > dt) {
            displacement = speed * dt + acceleration * (dt * dt) / 2;
            speed += acceleration * dt;
        // Ocorre colisao em dt, MUV depois MU
        } else {
            displacement = speed * tmax + acceleration * (tmax * tmax) / 2;
            speed = maxSpeed;
            displacement += speed * (dt - tmax);
        }
        position = getPosition().plus(getDirection().times(displacement));
    }

    public boolean hasIntersection(Boat boat) {
        double distance = boat.getPosition().minus(position).magnitude();
        return distance < radius + boat.getRadius();
    }
    
    public boolean isInside(Point p, double tolerance) {
        return p.minus(position).magnitude() < radius + tolerance;
    }
    
    public boolean isInside(Point p) {
        return isInside(p, 0);
    }

    // Largura e altura maxima
    public double containerWidth() { return 2 * radius; }
    public double containerHeight() { return 2 * radius; }

    // Retorna os limites do barco
    public double minX() { return getPosition().x() - getRadius(); }
    public double maxX() { return getPosition().x() + getRadius(); }
    public double minY() { return getPosition().y() - getRadius(); }
    public double maxY() { return getPosition().y() + getRadius(); }

    public String toString() { 
        return name + ": " + position.toString() + state.toString();
    }

}
