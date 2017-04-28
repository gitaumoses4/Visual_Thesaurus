package visual.thesaurus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author Moses Gitau
 */
public class CenterPanel extends JPanel {

    public final MainFrame mainFrame;
    private final Dimension size = new Dimension(500, 400);
    private String label;
    private ArrayList words;
    private final Font wordFont = new Font("Segoe UI", Font.BOLD, 20);
    private final Font wordsFont = new Font("Segoe UI", Font.PLAIN, 15);
    private final boolean drawTree = false;
    private final ArrayList<Point> points = new ArrayList();

    private int x;
    private int y;

    private JLayeredPane layeredPane;
    private final WordTree wordTree;

    public CenterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setPreferredSize(size);
        setMinimumSize(size);
        setBackground(Color.white);
        wordTree = new WordTree(mainFrame.getSize(), this);
        this.add(wordTree);
    }

    public void drawTree(String name, int numberOfNodes) {
        wordTree.addNew(name, numberOfNodes);
    }

    public void addNode(ArrayList<String> words, String nodeName) {
        wordTree.addNode(words, nodeName);
    }

    public void clearTree() {
        wordTree.removeAll();
    }

    public void repaintFrame() {
        repaint();
        mainFrame.repaint();
    }

    @Override
    public void paint(Graphics gr) {
        super.paint(gr);
        Graphics2D g = (Graphics2D) gr;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
