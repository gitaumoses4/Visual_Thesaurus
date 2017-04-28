package visual.thesaurus;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author Moses Gitau
 */
public class LoadingDialog extends JDialog {

    JProgressBar progress;
    JLabel loading;

    public LoadingDialog(JFrame parent) {
        super(parent);
        loading = new JLabel("<html><h3>Loading dictionaries</h3><br/>Please wait...</html>");

        progress = new JProgressBar(0, 100);
        progress.setIndeterminate(true);
        progress.setPreferredSize(new Dimension(300, 20));

        setSize(400, 150);
        setTitle("Loading");

        setLocationRelativeTo(null);
        setLayout(new FlowLayout());
        add(loading);
        add(progress);
    }

    public void setText(String text) {
        loading.setText("<html><h3>" + text + "</h3><br/>Please wait...</html>");
    }
}
