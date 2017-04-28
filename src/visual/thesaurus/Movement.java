package visual.thesaurus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author Moses Gitau
 */
public class Movement extends Thread {

    private int startAngle;
    private final int endAngle;
    private final JLabel word;
    private final int x, y;
    private final int radius;
    private final JComponent main;
    private final HashMap points;
    private final int index;

    public Movement(int startAngle, int endAngle, JLabel word, int x, int y, int radius, JComponent main, HashMap points, int index) {
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.word = word;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.main = main;
        this.points = points;
        this.index = index;
    }

    @Override
    public void run() {
        while (startAngle < endAngle) {
            try {
                int _x = x + (int) (Math.cos(Math.toRadians(startAngle)) * radius);
                int _y = y + (int) (Math.sin(Math.toRadians(startAngle)) * radius);
                word.setLocation(_x, _y);
                _x += word.getBounds().width / 2;
                _y += word.getBounds().height / 2;

                points.put(word.getText(), new Point(_x, _y));
                Thread.sleep(1);
                main.repaint();
                startAngle+=5;
            } catch (InterruptedException ex) {
            }
        }
    }
}
