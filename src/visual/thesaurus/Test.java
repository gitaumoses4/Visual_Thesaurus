package visual.thesaurus;

import java.awt.Point;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Moses Gitau
 */
public class Test {

    private static int index = 0;

    public Test() {
    }

    public static String readWebpage(String fileName) {
        String data = "";
        try {
            File file = new File(fileName);
            FileInputStream fileInput = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInput);
            String s;
            while ((s = dataInputStream.readLine()) != null) {
                data += s;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public static ArrayList<String> getSe2Divs(String webpage) {
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
        return divs;
    }

    public static void getFromDivs(ArrayList<String> divContent) {
        for (String s : divContent) {
            String search = "<div>";
            int count = 0;
            for (int i = 0; i < s.length() - search.length(); i++) {
                String match = s.substring(i, i + search.length());
                if (search.equals(match)) {
                    count++;
                    if (count == 2) {
                        i += search.length();
                        String strong = "<strong>";
                        String m = s.substring(i, i + strong.length());
                        while (!m.equals(strong)) {
                            i++;
                            m = s.substring(i, i + strong.length());
                        }
                        i += strong.length();
                        String name = "";
                        char c = s.charAt(i);
                        while (c != '<') {
                            name += c;
                            i++;
                            c = s.charAt(i);
                        }
                        System.out.println(name.toUpperCase());
                    }
                    if (count >= 2) {
                        String findA = "<a";
                        String mA = s.substring(i, i + findA.length());
                        while (i < s.length() - mA.length()) {
                            mA = s.substring(i, i + findA.length());
                            if (mA.equals(findA)) {
                                char c = s.charAt(i);
                                while (c != '>') {
                                    i++;
                                    c = s.charAt(i);
                                }
                                i++;
                                String word = "";
                                c = s.charAt(i);
                                while (c != '<') {
                                    i++;
                                    word += c;
                                    c = s.charAt(i);
                                }
                                System.out.println("\t->" + word);
                            }
                            i++;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String webpage = readWebpage("page.html");
        ArrayList<String> divs = getSe2Divs(webpage);
        getFromDivs(divs);
    }
}
