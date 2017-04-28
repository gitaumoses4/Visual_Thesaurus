package visual.thesaurus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

/**
 *
 * @author Moses Gitau
 */
public class WordTree extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {

    int centerX;
    int centerY;

    private JLayeredPane layeredPane;
    private Graphics2D g;

    private HashMap<Point, HashMap> nodePoints = new HashMap();

    private int dragX = 0, dragY = 0;
    private final CenterPanel center;
    private boolean isNode;
    private int nodeIndex = 0;
    private int selectedNode = -1;
    private int nodeAngle;
    private ArrayList<String> nodes = new ArrayList();
    private double zoomValue= 1.0;

    public WordTree(Dimension size, CenterPanel center) {
        this.center = center;
        centerX = size.width / 2 + 1000;
        centerY = size.height / 2;
        setPreferredSize(new Dimension(2000, 2000));
        setMinimumSize(new Dimension(2000, 2000));
        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
    }

    public void addNew(String word, int numberOfNodes) {
        removeAll();
        nodeAngle = 360 / numberOfNodes;
        nodeIndex = 0;
        nodePoints.clear();
        addRoot(word);
        repaint();
    }

    public void addRoot(String text) {
        Word root = new Word(text, true);
        root.addMouseListener(this);
        root.addMouseMotionListener(this);

        root.setBounds(centerX, centerY, root.getWidth(), root.getHeight());
        centerX += root.getBounds().width / 2;
        centerY += root.getBounds().height / 2;
        add(root);
        repaint();
    }

    public void addNode(ArrayList<String> words, String nodeName) {
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).isEmpty()) {
                words.remove(i);
            }
        }
        int degrees = nodeIndex * nodeAngle;
        this.nodes.add(nodeName);
        int radius = generateRandomRadius() + 200;
        int x = centerX + ((int) (Math.cos(Math.toRadians(degrees)) * radius));
        int y = centerY + ((int) (Math.sin(Math.toRadians(degrees)) * radius));
        HashMap points = new HashMap();
        nodePoints.put(new Node(x, y, nodeIndex, nodeName), points);
        nodeIndex++;
        repaint();

        int wordsRadius = ((int) (Math.random() * 100)) + 100;
        Word[] labels = new Word[words.size()];
        for (int i = 0; i < words.size(); i++) {
            int _x = x + ((int) (Math.cos(Math.toRadians(degrees)) * wordsRadius));
            int _y = y + ((int) (Math.sin(Math.toRadians(degrees)) * wordsRadius));
            Word word = new Word(words.get(i), false);
            word.addMouseMotionListener(this);
            word.addMouseListener(this);
            word.setNode(new Node(x, y, nodeIndex - 1, nodeName));
            word.setBounds(_x, _y, word.getWidth(), word.getHeight());
            _x += word.getBounds().width / 2;
            _y += word.getBounds().height / 2;
            points.put(word.getText(), new Point(_x, _y));
            labels[i] = word;
            add(word);
        }
        separateWords(labels, wordsRadius, x, y, points);
    }

    private void separateWords(Word[] labels, int wordsRadius, int x, int y, HashMap points) {
        int bestAngle = 360 / labels.length;
        for (int i = 0; i < labels.length; i++) {
            labels[i].setDirection(bestAngle * i);
            Movement m = new Movement(0, bestAngle * i, labels[i], x, y, wordsRadius, this, points, i);
            m.start();
        }
        MoveAway m = new MoveAway(labels, points, this, new Point(centerX, centerY), wordsRadius);
        m.start();

    }

    private int generateRandomRadius() {
        return ((int) (Math.random() * 100)) + 20;
    }

    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        g = (Graphics2D) gr;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Object[] nodeP = nodePoints.keySet().toArray();
        for (Object object : nodeP) {
            Node point = (Node) object;
            g.setColor(Color.red);
            g.drawLine(centerX, centerY, point.x, point.y);
            HashMap wordPoints = nodePoints.get(object);
            Object keys[] = wordPoints.keySet().toArray();
            for (int i = 0; i < wordPoints.size(); i++) {
                g.setColor(Color.black);
                Point p = ((Point) wordPoints.get(keys[i]));
                g.drawLine(point.x, point.y, p.x, p.y);
            }
            g.setColor(Color.red);
            g.fillOval(point.x - 10, point.y - 10, 20, 20);
            g.setColor(Color.black);
            g.drawOval(point.x - 10, point.y - 10, 20, 20);

            g.setColor(Color.blue);
            g.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g.drawString(((Node) point).getNodeName(), point.x, point.y);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof JLabel) {
            Thread search = new Thread() {
                @Override
                public void run() {
                    newWord(e);
                }
            };
            search.start();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragX = e.getLocationOnScreen().x;
        dragY = e.getLocationOnScreen().y;
        if (isNode(e)) {
            this.isNode = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.isNode = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() == this) {
            if (!isNode) {
                moveCanvas(e);
            } else {
                checkNode(e);
            }
        } else if (e.getSource() instanceof JLabel) {
            moveWord(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private void moveCanvas(MouseEvent e) {
        int diffX = dragX - e.getLocationOnScreen().x;
        int diffY = dragY - e.getLocationOnScreen().y;

        Component c[] = this.getComponents();
        for (int i = 0; i < this.getComponentCount(); i++) {
            c[i].setLocation(c[i].getLocation().x - diffX, c[i].getLocation().y - diffY);
        }
        Object o[] = nodePoints.keySet().toArray();
        HashMap<Point, HashMap> temp = new HashMap();
        for (Object object : o) {
            Point p = (Point) object;
            HashMap l = nodePoints.get(object);
            p.setLocation(p.getX() - diffX, p.getY() - diffY);
            for (Object point : l.keySet()) {
                Point pp = (Point) l.get(point);
                pp.setLocation(pp.getX() - diffX, pp.getY() - diffY);
            }
            temp.put(p, l);
        }
        this.nodePoints = temp;
        this.centerX -= diffX;
        this.centerY -= diffY;
        dragX = e.getLocationOnScreen().x;
        dragY = e.getLocationOnScreen().y;
        redraw();
    }

    private void redraw() {
        repaint();
        revalidate();
        center.repaintFrame();
    }

    private void moveWord(MouseEvent e) {
        int diffX = dragX - e.getLocationOnScreen().x;
        int diffY = dragY - e.getLocationOnScreen().y;

        Word label = (Word) e.getSource();
        int angle = (int) Math.toDegrees(Math.atan((1.0 * diffY) / diffX));
        label.setDirection(angle);
        if (label.isMain()) {
            centerX -= diffX;
            centerY -= diffY;
            label.setLocation(centerX - label.getWidth() / 2, centerY - label.getHeight() / 2);
        } else {
            Object o[] = nodePoints.keySet().toArray();
            HashMap<Point, HashMap> temp = new HashMap();
            for (Object object : o) {
                Point p = (Point) object;
                HashMap l = nodePoints.get(object);
                for (Object point : l.keySet()) {
                    Point pp = (Point) l.get(point);
                    if (label.getText().equals(point)) {
                        pp.setLocation(pp.getX() - diffX, pp.getY() - diffY);
                    }
                }
                temp.put(p, l);
            }
            this.nodePoints = temp;
            label.setLocation(label.getX() - diffX, label.getY() - diffY);
        }
        dragX = e.getLocationOnScreen().x;
        dragY = e.getLocationOnScreen().y;
        redraw();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int units = e.getUnitsToScroll() * 2;
        this.zoomValue=1-(1.0/units);
        
        redraw();
    }

    private void newWord(MouseEvent e) {
        if (!(((JLabel) e.getSource()).getText()).isEmpty()) {
            center.mainFrame.searchWord(((JLabel) e.getSource()).getText());
            center.mainFrame.addSearchedWord(((JLabel) e.getSource()).getText());
        }
    }

    private boolean isNode(MouseEvent e) {
        boolean isNode = false;
        Object o[] = nodePoints.keySet().toArray();
        int index = 0;
        for (Object object : o) {
            Node p = (Node) object;
            Rectangle bounds = new Rectangle(p.x - 10, p.y - 10, 20, 20);
            if (bounds.contains(e.getPoint())) {
                isNode = true;
                this.selectedNode = p.getIndex();
            }
            index++;
        }
        return isNode;
    }

    private void checkNode(MouseEvent e) {
        int diffX = dragX - e.getLocationOnScreen().x;
        int diffY = dragY - e.getLocationOnScreen().y;

        Component c[] = this.getComponents();
        Object o[] = nodePoints.keySet().toArray();
        HashMap<Point, HashMap> temp = new HashMap();
        for (Object object : o) {
            Node p = (Node) object;
            HashMap<String, Point> l = new HashMap(nodePoints.get(p));
            if (p.getIndex() == this.selectedNode) {
                for (String name : l.keySet()) {
                    Point point = l.get(name);
                    point.setLocation(point.getX() - diffX, point.getY() - diffY);
                }
                for (int i = 0; i < this.getComponentCount(); i++) {
                    Word word = (Word) c[i];
                    if (word.getNode() != null) {
                        if (word.getNode().getIndex() == this.selectedNode) {
                            c[i].setLocation(c[i].getLocation().x - diffX, c[i].getLocation().y - diffY);
                        }
                        word.setNode(new Node(word.getNode().x - diffX, word.getNode().y - diffY, word.getNode().getIndex(), word.getNode().getNodeName()));
                    }
                }
                p.setLocation(p.getX() - diffX, p.getY() - diffY);
            }
            temp.put(p, l);
        }
        this.nodePoints = temp;
        dragX = e.getLocationOnScreen().x;
        dragY = e.getLocationOnScreen().y;
        redraw();
    }
}
