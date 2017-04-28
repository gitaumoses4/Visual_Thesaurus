package visual.thesaurus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Moses Gitau
 */
public class NorthPanel extends JPanel implements ActionListener {

    private JButton next;
    private JButton back;
    private JTextField searchField;
    private JButton searchButton;
    private BoxLayout layout;
    private final MainFrame mainFrame;
    private final ArrayList<String> searchedWords = new ArrayList();
    private int index = 0;

    public NorthPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setUpBorder();
        setPanelLayout();
        setUpComponents();
    }

    public void clear() {
        searchField.setText("");
    }

    private void setPanelLayout() {
        layout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(layout);
    }

    private void setUpComponents() {
        next = new JButton("Next");
        next.addActionListener(this);
        next.setEnabled(false);
        back = new JButton("Back");
        back.setEnabled(false);
        back.addActionListener(this);
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        add(Box.createHorizontalStrut(20));
        add(back);
        add(next);
        add(Box.createHorizontalStrut(20));
        add(searchField);
        add(searchButton);
        add(Box.createHorizontalStrut(20));
    }

    private void setUpBorder() {
        Border border = new EmptyBorder(10, 2, 10, 2);
        setBorder(border);
    }

    public void addSearchedWord(String text) {
        searchedWords.add(text);
        index = searchedWords.size() - 1;
        if (index > 0) {
            back.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String text = this.searchField.getText();
            if (!text.isEmpty()) {
                if (searchedWords.size() > 0) {
                    back.setEnabled(true);
                    mainFrame.searchWord(text);
                    searchedWords.add(text);
                } else {
                    back.setEnabled(false);
                    mainFrame.searchWord(text);
                    searchedWords.add(text);
                }
            }
        } else if (e.getSource() == back) {
            if (index > 0) {
                index -= 1;
                String text = searchedWords.get(index);
                searchField.setText(text);
                mainFrame.searchWord(text);
                next.setEnabled(true);
            } else {
                back.setEnabled(false);
                if (index < searchedWords.size()) {
                    next.setEnabled(true);
                }
            }
        } else if (e.getSource() == next) {
            if (index < searchedWords.size() - 1) {
                index++;
                String text = searchedWords.get(index);
                searchField.setText(text);
                mainFrame.searchWord(text);
                back.setEnabled(true);
            } else {
                next.setEnabled(false);
                if (index > 0) {
                    back.setEnabled(true);
                }
            }
        }
    }
}
