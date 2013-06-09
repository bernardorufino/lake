package base;

import java.util.ArrayList;
import java.util.List;

import utils.Point;
import utils.Vector2D;

public class Lake {
    private final double width;
    private final double height;
    private final List<Boat> boats;
    private double maxSpeed;
    private double minRadius;
    private List<Boat> sunkBoats;
    
    public static class BoatGreaterThanLakeException extends RuntimeException { /* Nothing */ }
    
    public Lake(double width, double height) {
        this.width = width;
        this.height = height;
        this.boats = new ArrayList<Boat>();
        sunkBoats = new ArrayList<Boat>();
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }
    
    /***
     * Faz o tempo andar de timeDelta
     * @param timeDelta
     */
    public final void runTime(double timeDelta) {
        if (boats.isEmpty()) return;
        // Calcula o nomero de iteracoes maximo e depois faz os marcos se moverem
        // de um tempo correspondente ao resto da divisao
        double step = minRadius / maxSpeed;
        int times = (int) (timeDelta / step);
        double remainder = timeDelta % step;
        for (int i = 1; i <= times + 1; i++) {
            for (Boat boat : boats) {
                boat.move(
                    (i < times + 1)
                    ? step
                    : remainder
                );
                // Se o barco nao bateu checa se existe colisao
                if (!boat.isSunk()) checkPreviousBoatsColision(boat);
            }
        }
        sunkBoats();
        for (Boat boat : boats) checkBoatLocation(boat);
    }
    
    public void addBoat(Boat boat, Point position, Vector2D direction) {
        boat.setPosition(position);
        boat.setDirection(direction);
        double speed = boat.getMaxSpeed();
        double radius = boat.getRadius();
        if (boats.isEmpty() || radius < minRadius) minRadius = radius;
        if (boats.isEmpty() || speed > maxSpeed) maxSpeed = speed;
        checkBoatDimensions(boat);
        checkBoatLocation(boat);
        boats.add(boat);
        checkPreviousBoatsColision(boat);
    }
    
    public boolean hasMovement() {
        boolean hasMovement = false;
        for (Boat b : boats) if (b.isMoving()) hasMovement = true;
        return hasMovement;
    }
    
    public List<Boat> getBoats() {
        return boats;
    }
    
    public boolean isInside(Point p) {
        return (-width / 2 < p.x() && p.x() < width / 2) &&
            (-height / 2 < p.y() && p.y() < height / 2);
    }
    
    // Checa se o barco cabe no lago
    private void checkBoatDimensions(Boat boat) {
        if (boat.containerWidth() > width ||
            boat.containerHeight() > height)
            throw new BoatGreaterThanLakeException();
    }
    
    // Checa se o barco esta dentro dos limites do lago
    private void checkBoatLocation(Boat boat) {
        if (!boat.isMoving()) return;
        double lowerLimitX = - this.width / 2;
        double upperLimitX = this.width / 2;
        double lowerLimitY = - this.height / 2;
        double upperLimitY = this.height / 2;
        Point position = boat.getPosition();
        if (boat.minX() < lowerLimitX) {
            boat.stuck();
            boat.setPosition(position.addX(lowerLimitX - boat.minX()));
        } else if (boat.maxX() > upperLimitX) {
            boat.stuck();
            boat.setPosition(position.addX(upperLimitX - boat.maxX()));
        }
        if (boat.minY() < lowerLimitY) {
            boat.stuck();
            boat.setPosition(position.addY(lowerLimitY - boat.minY()));
        } else if (boat.maxY() > upperLimitY) {
            boat.stuck();
            boat.setPosition(position.addY(upperLimitY - boat.maxY()));
        }
    }
    
    // Verifica colisao de boat com os barcos anteriores na lista de barcos
    private void checkPreviousBoatsColision(Boat boat) {
        List<Boat> previousBoats = boats.subList(0, boats.lastIndexOf(boat));
        for (Boat b : previousBoats) {
            // Se os barcos tiverem interseccao e um deles nao estiver afundado, 
            // marca para afundar
            if (!b.isSunk() && boat.hasIntersection(b)) {
                sunkBoats.add(boat);
                sunkBoats.add(b);
                // Reposiciona o barco para que ele fique na posicao inicial da batida
                // -2 pixels to ensure contact since there is int rounding for pixel purposes
                double distanceAfterCrash = boat.getRadius() + b.getRadius() - 2;
                Vector2D d = boat
                    .getPosition()
                    .minus(b.getPosition())
                    .unitary()
                    .times(distanceAfterCrash);
                Point p = b.getPosition().plus(d);
                boat.setPosition(p);
                break;
            }
        }
    }
    
    private void sunkBoats() {
        while (!sunkBoats.isEmpty()) {
            Boat boat = sunkBoats.remove(0);
            boats.remove(boat);
            boats.add(0, boat);
            boat.sunk();
        }
    }
    
}
