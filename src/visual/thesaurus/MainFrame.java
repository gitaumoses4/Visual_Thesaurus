package visual.thesaurus;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Moses Gitau
 */
public class MainFrame extends JFrame implements ThreadListener {

    private NorthPanel north; //will be at the top of the frame
    private EastPanel east; //will be on the right
    private CenterPanel center; // will be at the center
    private HashMap<String, ArrayList> synonyms; //hashmap containing dictionary data.
    private HashMap<String, ArrayList[]> dictionary;
    private HashMap<String, HashMap<String, ArrayList>[]> savedDictionaries = new HashMap();

    LoadingDialog loading;

    public MainFrame() {
        setUpWindowsLookAndFeel();
        loading = new LoadingDialog(MainFrame.this);
        setUpProperties();
        setUpComponents();
        readSavedDictionary();
    }

    public void readSavedDictionary() {
        try {
            File file = new File("data.map");
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                savedDictionaries = (HashMap<String, HashMap<String, ArrayList>[]>) objectInputStream.readObject();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveDictionary() {
        try {
            File file = new File("data.map");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOutputStream);
            objectOut.writeObject(savedDictionaries);
            objectOut.flush();
            objectOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Frame components
    private void setUpComponents() {
        north = new NorthPanel(this);
        east = new EastPanel(this);
        center = new CenterPanel(this);
        add(north, BorderLayout.NORTH);
        add(east, BorderLayout.EAST);
        add(center, BorderLayout.CENTER);
    }

    public void addSearchedWord(String word) {
        north.addSearchedWord(word);
    }

    public void searchWord(final String word) {
        loading.setText("Loading... word: " + word);
        Thread t = new Thread() {
            @Override
            public void run() {
                loading.setModal(true);
                loading.setVisible(true);
            }
        };
        t.start();
        Thread loadWord = new Thread() {
            @Override
            public void run() {
                try {
                    if (savedDictionaries.containsKey(word)) {
                        HashMap<String, ArrayList>[] dictionary = savedDictionaries.get(word);
                        loadSynonyms(dictionary[0], word);
                        setWordMeaning(dictionary[1], true);
                    } else {
                        HashMap<String, ArrayList>[] dictionary = DataExtractor.getDictionary(word);

                        loadSynonyms(dictionary[0], word);
                        setWordMeaning(dictionary[1], true);
                        savedDictionaries.put(word, dictionary);
                        saveDictionary();
                    }
                } catch (Exception ex) {
                    if (ex instanceof FileNotFoundException) {
                        JOptionPane.showMessageDialog(null, "No synonyms found for: " + word);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error connecting to the Internet. Try Again.");
                    }
                }
            }
        };
        loadWord.start();
        ThreadProcess threadProcess = new ThreadProcess(loadWord);
        threadProcess.addThreadListener(this);

    }

    private void loadSynonyms(HashMap<String, ArrayList> synonymsMap, String word) {
        if (synonymsMap != null) {
            if (synonymsMap.isEmpty()) {
                center.clearTree();
                loading.setVisible(false);
                return;
            }
            center.drawTree(word, synonymsMap.size());
            for (String s : synonymsMap.keySet()) {
                center.addNode(synonymsMap.get(s), s);
            }
        } else {
            center.clearTree();
        }
    }

    public void setWordMeaning(HashMap<String, ArrayList> definitions, boolean newWord) {
        int type;
        east.removeAllDefinitions();
        for (String s : definitions.keySet()) {
            ArrayList<String> defs = definitions.get(s);
            type = -1;
            if (s.equals("noun")) {
                type = EastPanel.NOUN;
            } else if (s.equals("adjective")) {
                type = EastPanel.ADJECTIVE;
            } else if (s.equals("adverb")) {
                //type = EastPanel.ADVERB;
            } else if (s.equals("verb")) {
                type = EastPanel.VERB;
            }
            for (int i = 0; i < defs.size(); i++) {
                if (i > 0) {
                    newWord = false;
                }
                if (type != -1) {
                    east.addDefinition(defs.get(i), type, newWord);
                }
            }
        }
    }

    public HashMap getDictionary() {
        return this.dictionary;
    }

    public HashMap getSynonyms() {
        return this.synonyms;
    }

    private void setUpWindowsLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
    }

    //Frame properties
    private void setUpProperties() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void dead() {
        loading.setModal(false);
        loading.setVisible(false);
    }

    @Override
    public void alive() {
    }
}
