package simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import utils.Point;
import utils.Utils;
import utils.Vector2D;
import base.Boat;
import base.Lake;

public class LakePanel extends JPanel {
    private final static Color MOVING_BOAT_COLOR   = new Color(56, 56, 56);
    private final static Color SUNK_BOAT_COLOR     = new Color(24, 94, 163);
    private final static Color STUCK_BOAT_COLOR    = new Color(197, 37, 41);
    private final static Color SELECTED_BOAT_COLOR = new Color(35, 172, 11);
    private final static Color LAKE_COLOR          = new Color(196, 224, 234);
    private final static Color TEXT_COLOR          = new Color(0, 0, 0);
    private final static Color LINE_COLOR          = new Color(180, 215, 225);
    
    private Boat selectedBoat = null;
    private boolean boatSelected = false;
    private final Lake lake;
    
    public LakePanel(Lake lake) {
        super();
        this.lake = lake;
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { handleMousePressed(e); }
            public void mouseReleased(MouseEvent e) { handleMouseReleased(e); }
        });
        setBackground(LAKE_COLOR);
    }
    
    private boolean selectBoat(Point p) {
        for (Boat boat : lake.getBoats()) {
            double tolerance = 10 * 10 / (5 + boat.getRadius());
            if (boat.isInside(p, tolerance) && boat.isMoving()) {
                selectedBoat = boat;
                return true;
            }
        }
        return false;
    }
    
    private void handleMousePressed(MouseEvent e) {
        Point p = toLakeRef(Point.xy(e.getX(), e.getY()));
        boatSelected = selectBoat(p); // More readable
    }

    private void handleMouseReleased(MouseEvent e) {
        Point p = toLakeRef(Point.xy(e.getX(), e.getY()));
        if (!boatSelected && selectedBoat != null) {
            Vector2D direction = p.minus(selectedBoat.getPosition());
            selectedBoat.setDirection(direction);
        }
    }
    
    public Dimension getPreferredSize() {
        // Some untracked padding in width, ugly fix
        return new Dimension((int) Math.ceil(lake.getWidth()) - 10, (int) Math.ceil(lake.getHeight()) + 6);
    }

    private Color getBoatColor(Boat boat) {
        switch (boat.getState()) {
            case Moving: default:
                return (boat == selectedBoat) ? SELECTED_BOAT_COLOR : MOVING_BOAT_COLOR; 
            case Sunk: return SUNK_BOAT_COLOR; 
            case Stuck: return STUCK_BOAT_COLOR;
        }
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(LINE_COLOR);
        g.drawLine((int) lake.getWidth() / 2, 0, (int) lake.getWidth() / 2, (int) lake.getHeight());
        g.drawLine(0, (int) lake.getHeight() / 2, (int) lake.getWidth(), (int) lake.getHeight() / 2);
        for (Boat boat : lake.getBoats()) {
            Point p = toPanelRef(boat.getPosition());
            int radius = (int) boat.getRadius();
            int diameter = (int) (2 * boat.getRadius());
            int x = (int) p.x();
            int y = (int) p.y();
            
            // Draw label above boat
            String label = boat.getName();
            if (boat.isMoving()) label += " @ " + Utils.format(boat.getSpeed(), 1) + " px/s";
            int offset = label.length() * 5 / 2;
            g.setColor(TEXT_COLOR);
            g.drawString(label, x - offset, y - radius - 5);
            
            // Draw boat with color according to boat state
            g.setColor(getBoatColor(boat));
            g.fillOval(x - radius, y - radius, diameter, diameter);
        }
    }

    private Point toPanelRef(Point p) {
        return Point.xy(lake.getWidth() / 2 + p.x(), lake.getHeight() / 2 - p.y());
    }

    private Point toLakeRef(Point p) {
        return Point.xy(p.x() - lake.getWidth() / 2, lake.getHeight() / 2 - p.y());
    }
}