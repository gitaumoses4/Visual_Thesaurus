package visual.thesaurus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Moses Gitau
 */
public class DataExtractor {

    private static String synonymsHtmlContent = new String();
    private static String definitionsHtmlContent = new String();
    private static String definitionContent = new String();
    private static String definitionUrl = "http://www.oxforddictionaries.com/definition/english/";
    private static String synonymUrl = "http://www.oxforddictionaries.com/definition/english-thesaurus/";

    public static HashMap[] getDictionary(String word) throws Exception {
        word = word.replace(' ', '-');
        HashMap<String, ArrayList>[] dictionary = new HashMap[2];
        HashMap<String, ArrayList> synonyms;

        URL synonymsLink = new URL(synonymUrl + word);
        BufferedReader synonymsReader = new BufferedReader(new InputStreamReader(synonymsLink.openStream()));
        String s;
        while ((s = synonymsReader.readLine()) != null) {
            System.out.println(s);
            synonymsHtmlContent += s;
        }
        synonyms = startExtract(synonymsHtmlContent);

        HashMap<String, ArrayList> definitions;
        URL definitionsLink = new URL(definitionUrl + word);
        BufferedReader definitionsReader = new BufferedReader(new InputStreamReader(definitionsLink.openStream()));
        s = null;
        while ((s = definitionsReader.readLine()) != null) {
            System.out.println(s);
            definitionsHtmlContent += s;
        }

        definitions = getDefinitions(definitionsHtmlContent);
        dictionary[0] = synonyms;
        dictionary[1] = definitions;
        return dictionary;
    }

    private static HashMap<String, ArrayList> startExtract(String webpage) {
        webpage = webpage.toLowerCase();
        String search = "<div class=\"se2\">";
        ArrayList divs = new ArrayList();
        for (int i = 0; i < webpage.length() - search.length(); i++) {
            String match = webpage.substring(i, i + search.length());
            if (match.equals(search)) {
                i += search.length() - 1;
                String div = "</div>";
                int count = 0;
                String divContent = "";
                while (count < 7) {
                    String mat = webpage.substring(i, i + div.length());
                    if (mat.equals(div)) {
                        count++;
                    }
                    i++;
                    divContent += webpage.charAt(i);
                }
                divs.add(divContent);
            }
        }
        return getFromDivs(divs);
    }

    private static HashMap<String, ArrayList> getFromDivs(ArrayList<String> divContent) {
        HashMap<String, ArrayList> dictionary = new HashMap();
        for (String s : divContent) {
            String search = "<div>";
            int count = 0;
            for (int i = 0; i < s.length() - search.length(); i++) {
                String match = s.substring(i, i + search.length());
                String name = "";
                if (search.equals(match)) {
                    count++;
                    if (count == 2) {
                        i += search.length();
                        String strong = "<strong>";
                        String m = s.substring(i, i + strong.length());
                        while (!m.equals(strong) && i < s.length()) {
                            i++;
                            m = s.substring(i, i + strong.length());
                        }
                        i += strong.length();
                        char c = s.charAt(i);
                        while (c != '<' && i < s.length()) {
                            name += c;
                            i++;
                            c = s.charAt(i);
                        }
                    }
                    ArrayList words = new ArrayList();
                    if (count >= 2) {
                        String findA = "<a";
                        String mA = s.substring(i, i + findA.length());
                        while (i < s.length() - mA.length()) {
                            mA = s.substring(i, i + findA.length());
                            if (mA.equals(findA)) {
                                char c = s.charAt(i);
                                while (c != '>' && i < s.length()) {
                                    i++;
                                    c = s.charAt(i);
                                }
                                i++;
                                String word = "";
                                c = s.charAt(i);
                                while (c != '<' && i < s.length()) {
                                    i++;
                                    word += c;
                                    c = s.charAt(i);
                                }
                                if (!wordIsSpace(word)) {
                                    words.add(word);
                                }
                            }
                            i++;
                        }
                        dictionary.put(name, words);
                    }
                }

            }
        }
        return dictionary;
    }

    private static boolean wordIsSpace(String word) {
        int spaceCount = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == ' ') {
                spaceCount++;
            }
        }
        return spaceCount == word.length();
    }

    private static HashMap<String, ArrayList> getDefinitions(String webpage) {
        HashMap<String, ArrayList> definitions = new HashMap();
        String search = "class=\"partOfSpeech\">";
        String search2 = "class=\"definition\">";
        for (int i = 0; i < webpage.length() - search.length(); i++) {
            String match = webpage.substring(i, i + search.length());
            if (match.equals(search)) {
                i += search.length();
                char c = webpage.charAt(i);
                String part = "";
                while (c != '<' && i < webpage.length()) {
                    part += c;
                    i++;
                    c = webpage.charAt(i);
                }
                match = webpage.substring(i, i + search.length());
                ArrayList defs = new ArrayList();
                String match2;
                while (!match.equals(search) && i < webpage.length() - search.length()) {
                    match2 = webpage.substring(i, i + search2.length());
                    match = webpage.substring(i, i + search.length());
                    if (match2.equals(search2)) {
                        i += search.length() - 2;
                        String defEnd = "</span>";
                        String def = "";
                        String defMatch = "";
                        c = webpage.charAt(i);
                        while (!defMatch.equals(defEnd) && i < webpage.length()) {
                            defMatch = webpage.substring(i, i + defEnd.length());
                            def += c;
                            i++;
                            c = webpage.charAt(i);
                        }
                        if (def.length() > 10) {
                            defs.add(def);
                        }
                    }
                    i++;
                }
                i -= search.length();
                if (!defs.isEmpty()) {
                    definitions.put(part, defs);
                }
            }
        }
        return definitions;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(DataExtractor.getDictionary("home")[0]);
    }
}
