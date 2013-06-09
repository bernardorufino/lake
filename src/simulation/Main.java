package simulation;

import utils.Point;
import utils.Utils;
import utils.Vector2D;
import base.Boat;
import base.Lake;

public class Main {
    private static Lake lake;
    private static LakeDrawer drawer;
    
    public static class FrameRateTooHighException extends RuntimeException { /* Nothing */ }
    
    public static void main(String[] args) {
        lake = new Lake(800, 600);
        randomBoats(10);
//        boat();
//        cornersToCenter();
        drawer = new LakeDrawer(lake);
        simulate(60);
    }
    
    public static void boat() {
        Boat boat = new Boat("Neo", 10, 10, 100, 1);
        lake.addBoat(boat, Point.xy(0, -10), Vector2D.xy(0, 1));
        boat = new Boat("Old", 10, 10, 100, 1);
        lake.addBoat(boat, Point.xy(-100, 0), Vector2D.xy(1, 0));
    }
    
    /**
     * Gera n barcos aleatorios e os adiciona em posicoes aleatorias do lago
     * @param n
     */
    public static void randomBoats(int n) {
        double factor = 2;
        for (int i = 1; i <= n; i++) {
            double initialSpeed = Utils.random(0, 10) * factor;
            double finalSpeed = (2 + Utils.random(15, 50) * initialSpeed) * factor;
            double acceleration = (finalSpeed - initialSpeed) / Utils.random(25, 30) * factor;
            Boat boat = new Boat("Boat " + i, Utils.random(10, 20), initialSpeed, finalSpeed, acceleration);
            Vector2D direction = Vector2D.direction(Utils.random(0, 2 * Math.PI));
            lake.addBoat(boat, randomPoint(), direction);
        }
    }
    
    /**
     * Gera barcos nos 4 cantos do lago apontando para o centro
     */
    public static void cornersToCenter() {
        double radius = 15;
        Point[] corners = { // Top Right -> Top Left -> Bottom Left -> Bottom Right
            Point.xy( lake.getWidth() / 2 - radius,  lake.getHeight() / 2 - radius),
            Point.xy(-lake.getWidth() / 2 + radius,  lake.getHeight() / 2 - radius),
            Point.xy(-lake.getWidth() / 2 + radius, -lake.getHeight() / 2 + radius),
            Point.xy( lake.getWidth() / 2 - radius, -lake.getHeight() / 2 + radius)
        };
        for (int i = 0; i < 4; i++) {
            Boat boat = new Boat("Boat " + i, radius, 10, 200, 100);
            Vector2D direction = Point.origin().minus(corners[i]);
            lake.addBoat(boat, corners[i], direction);
        }
    }
    
    /**
     * Simula os barcos no lago com frame rate fps
     * @param fps
     */
    public static void simulate(double fps) {
        double delta = 1 / fps;
        long refreshInterval = (long) (1000 * delta);
        if (refreshInterval < 1) throw new FrameRateTooHighException();
        drawer.refresh(0);
        double time;
        for (time = delta; lake.hasMovement(); time += delta) {
            // Nos segundos redondos imprime situacao dos barcos
//            if (Math.abs(time - Math.round(time)) < 0.001) printLakeStatus(time);
            try { Thread.sleep(refreshInterval); } catch (InterruptedException e) { /* Ignore */ }
            lake.runTime(delta);
            drawer.refresh(time);
        }
//        printLakeStatus(time);
    }
    
    private static void printLakeStatus(double time) {
        System.out.println("Time = " + Utils.timeFormat(time));
        for (Boat boat : lake.getBoats()) {
            System.out.println(
                boat.getName() + ": " +
                "posicao = " + boat.getPosition() + " " +
                "velocidade = " + Utils.format(boat.getSpeed(), 2) + " " +
                "direcao = " + boat.getDirection() + " " + 
                "status = " + boatState(boat)
            );
        }
    }
    
    private static String boatState(Boat boat) {
        switch (boat.getState()) {
            case Moving: default:
                return "Movimento";
            case Stuck: return "Encalhado";
            case Sunk: return "Afundado";
        }
    }
    
    /**
     * Retorna um ponto aleatorio dentro do lago
     */
    private static Point randomPoint() {
        Point p = Point.xy(
            Utils.normal(0, lake.getWidth() / 4),
            Utils.normal(0, lake.getHeight() / 4)
        );
        return lake.isInside(p) ? p : randomPoint();
    }
}
