package visual.thesaurus;

import java.awt.Point;

/**
 *
 * @author Moses Gitau
 */
public class Node extends Point {

    int index = -1;
    String nodeName;

    public Node(int x, int y, int index, String nodeName) {
        super(x, y);
        this.index = index;
        this.nodeName = nodeName;
    }

    public int getIndex() {
        return index;
    }

    public String getNodeName() {
        return this.nodeName;
    }
}
