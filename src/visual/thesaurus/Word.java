package visual.thesaurus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import javax.swing.JLabel;

/**
 *
 * @author Moses Gitau
 */
public class Word extends JLabel {

    private int direction = -1;
    private Font wordFont = new Font("Segoe UI", Font.BOLD, 20);
    private Font wordsFont = new Font("Segoe UI", Font.PLAIN, 12);
    private Font font;
    private boolean main;
    private Node node;

    public Word(String text, boolean main) {
        super(text);
        this.main=main;
        setOpaque(true);
        setBackground(Color.white);
        font = main ? wordFont : wordsFont;
        setFont(font);
    }
    public boolean isMain(){
        return main;
    }

    public int getDirection() {
        return direction;
    }

    @Override
    public int getWidth() {
        return getFontMetrics(font).stringWidth(getText());
    }

    @Override
    public int getHeight() {
        return font.getSize() + 5;
    }

    public void setDirection(int dir) {
        direction = dir;
    }

    public Point getCenter() {
        int x = getX() + getWidth() / 2;
        int y = getY() + getHeight() / 2;
        return new Point(x, y);
    }

    public void setNode(Node point) {
        this.node=point;
    }
    public Node getNode(){
        return node;
    }
}
