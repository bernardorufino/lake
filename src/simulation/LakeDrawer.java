package simulation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

import utils.Utils;
import base.Lake;

public class LakeDrawer extends JFrame {
    private final Lake lake;
    private final LakePanel panel;
    private final JLabel status;

    public LakeDrawer(Lake lake) {
        super();
        this.lake = lake;
        panel = new LakePanel(lake);
        status = new JLabel();
        build();
        settings();
    }
    
    private void settings() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }
    
    private void build() {
        setLayout(new BorderLayout(0,0));
        Container pane = getContentPane();
        pane.add(panel, BorderLayout.CENTER);
        setTitle("Lago");
        pane.add(status, BorderLayout.SOUTH);
        pack();
    }
    
    public void refresh(double time) {
        panel.repaint();
        String sep = "      ";
        status.setText(
            "Time: " + Utils.timeFormat(time) + sep +
            "Boats: " + lake.getBoats().size() + sep + 
            "[" + (lake.hasMovement() ? "Running" : "No Movement") + "]"
        );
    }

}
