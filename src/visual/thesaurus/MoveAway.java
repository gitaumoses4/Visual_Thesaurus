package visual.thesaurus;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import javax.swing.JComponent;

/**
 *
 * @author Moses Gitau
 */
public class MoveAway extends Thread {

    private final Word[] labels;
    private final HashMap hashMap;
    private final JComponent main;
    private final Point center;
    private final int radius;

    public MoveAway(Word[] labels, HashMap hashMap, JComponent main, Point center, int radius) {
        this.labels = labels;
        this.hashMap = hashMap;
        this.main = main;
        this.center = center;
        this.radius = radius;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (int j = 0; j < labels.length; j++) {
                    Word mainLabel = labels[j];
                    for (int i = 0; i < labels.length; i++) {
                        Word otherLabel = labels[i];
                        if (otherLabel != mainLabel) {
                            Rectangle mainRect = mainLabel.getBounds();
                            Rectangle otherRect = otherLabel.getBounds();
                            if (mainRect.intersects(otherRect)) {
                                Point mainCenter = new Point(mainRect.x + mainRect.width / 2, mainRect.y + mainRect.height / 2);
                                Point otherCenter = new Point(otherRect.x + otherRect.width / 2, otherRect.y + otherRect.height / 2);

                                int distance = (int) mainCenter.distance(otherCenter);
                                int x = (otherCenter.x - mainCenter.x);
                                int y = (otherCenter.y - mainCenter.y);
                                int maxDistance = distance + 100;
                                int minDistance = distance - 100;
                                int option;
                                int INCREASE = 1;
                                int DECREASE = 2;
                                while (mainRect.intersects(otherRect)) {
                                    try {
                                        option = INCREASE;
                                        if (distance == maxDistance) {
                                            option = DECREASE;
                                        } else if (distance == minDistance) {
                                            option = INCREASE;
                                        }
                                        distance = option == INCREASE ? distance + 1 : distance - 1;

                                        int newCX = otherLabel.getCenter().x + (int) (distance * (Math.cos(Math.toRadians(mainLabel.getDirection()))));
                                        int newCY = otherLabel.getCenter().y + (int) (distance * (Math.sin(Math.toRadians(mainLabel.getDirection()))));
                                        int newX = newCX - (mainRect.width / 2);
                                        int newY = newCY - (mainRect.height / 2);

                                        hashMap.put(mainLabel.getText(), new Point(newCX, newCY));
                                        mainLabel.setLocation(newX, newY);
                                        
                                        mainRect = mainLabel.getBounds();
                                        otherRect = otherLabel.getBounds();
                                        Thread.sleep(10);
                                        main.repaint();
                                    } catch (InterruptedException ex) {
                                    }
                                }
                            }
                        }
                    }
                    main.repaint();
                    Thread.sleep(2);
                }
            } catch (InterruptedException ex) {
            }
        }
    }
}
