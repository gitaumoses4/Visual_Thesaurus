package visual.thesaurus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Moses Gitau
 */
public class EastPanel extends JPanel {

    private final GridLayout layout = new GridLayout(4, 1);
    private final MainFrame mainFrame;

    public static final int NOUN = 0;
    public static final int ADJECTIVE = 1;
    public static final int VERB = 2;
    public static final int ADVERB = 3;

    private final String[] classes = {"  NOUNS", "  ADJECTIVES", "  VERBS", "  ADVERBS"};
    private final Color[] colors = {
        new Color(227, 65, 47),
        new Color(216, 171, 49),
        new Color(163, 179, 78),
        new Color(103, 67, 131)};
    private final EastPanelComponent components[] = new EastPanelComponent[4];
    private final Dimension minimumSize = new Dimension(200, 200);

    public EastPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setUpLayout();
        setUpComponents();
        setUpProperties();
    }

    private void setUpLayout() {
        setLayout(layout);
    }

    private void setUpComponents() {
        int index = 0;
        for (String s : classes) {
            components[index] = new EastPanelComponent(s, colors[index]);
            add(components[index]);
            index++;
        }
    }

    private void setUpProperties() {
        setMinimumSize(minimumSize);
        setPreferredSize(minimumSize);
    }

    public void removeAllDefinitions() {
        for (int i = 0; i < components.length; i++) {
            components[i].removeAllMeanings();
            components[i].revalidate();
            components[i].repaint();
            this.repaint();
        }
    }

    public void addDefinition(String def, int type, boolean newWord) {
        if (newWord) {
            removeAllDefinitions();
        }
        components[type].addText(def);
        components[type].revalidate();
        components[type].repaint();
        this.repaint();
    }

    class EastPanelComponent extends JPanel {

        private JLabel title;
        private JCheckBox on;
        private JCheckBox off;
        private final Color color;

        private JPanel content;

        public EastPanelComponent(String title, Color color) {
            this.color = color;
            setLayout(new BorderLayout());
            setUpComponents(title);
        }

        private void setUpComponents(String t) {
            this.title = new JLabel(t);
            JPanel north = new JPanel(new BorderLayout());
            JPanel checkBoxes = new JPanel();

            on = new JCheckBox("On");
            on.setBackground(color);
            on.setSelected(true);
            off = new JCheckBox("Off");
            off.setBackground(color);

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(on);
            buttonGroup.add(off);

            checkBoxes.setBackground(color);
            checkBoxes.add(on);
            checkBoxes.add(off);

            north.setBackground(color);
            north.add(title, BorderLayout.CENTER);
            north.add(checkBoxes, BorderLayout.EAST);

            content = new JPanel();
            BoxLayout y_axis = new BoxLayout(content, BoxLayout.Y_AXIS);
            content.setLayout(y_axis);
            content.setBorder(new EmptyBorder(5, 5, 5, 10));


            add(north, BorderLayout.NORTH);
            add(new JScrollPane(content));
        }

        public void removeAllMeanings() {
            content.removeAll();
        }

        public void addText(String text) {
            if (isOn()) {
                text = "<html>" + text + "<hr/></html>";
                JLabel label = new JLabel(text);
                content.add(label);
            }
        }

        public boolean isOn() {
            return on.isSelected();
        }

        public boolean isOff() {
            return off.isSelected();
        }
    }
}
