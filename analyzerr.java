import java.io.*;
import java.util.*;

class Symbol {
    String name;
    String type;
    String scope;
    int line;

    Symbol(String n, String t, String s, int l) {
        name = n;
        type = t;
        scope = s;
        line = l;
    }
}

public class LexicalAnalyzer {

    static String[] keywords = {
        "int","float","double","char","if","else","while",
        "for","return","break","continue","void"
    };

    static ArrayList<Symbol> symbolTable = new ArrayList<>();
    static int tokenId = 0;
    static int lineNo = 1;
    static String currentScope = "global";
    static String lastType = "";

    // Check keyword
    static boolean isKeyword(String word) {
        for (String k : keywords)
            if (k.equals(word))
                return true;
        return false;
    }

    // Check if already in symbol table
    static boolean symbolExists(String name) {
        for (Symbol s : symbolTable)
            if (s.name.equals(name))
                return true;
        return false;
    }

    public static void main(String[] args) throws Exception {

        FileInputStream fin = new FileInputStream("input.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fin));

        int ch;
        String word = "";

        System.out.println("\n--- TOKEN TABLE ---");
        System.out.println("ID\tLexeme\t\tType\t\tLine");
        System.out.println("---------------------------------------------");

        while ((ch = br.read()) != -1) {
            char c = (char) ch;

            // Line count
            if (c == '\n') {
                lineNo++;
                continue;
            }

            // Skip spaces
            if (Character.isWhitespace(c))
                continue;

            // ---------- IDENTIFIER / KEYWORD ----------
            if (Character.isLetter(c) || c == '_') {
                word = "" + c;

                while ((ch = br.read()) != -1 &&
                      (Character.isLetterOrDigit((char)ch) || (char)ch == '_')) {
                    word += (char) ch;
                }

                tokenId++;

                if (isKeyword(word)) {
                    System.out.println(tokenId + "\t" + word + "\t\tKEYWORD\t\t" + lineNo);
                    lastType = word;
                } else {
                    System.out.println(tokenId + "\t" + word + "\t\tIDENTIFIER\t" + lineNo);

                    if (!symbolExists(word)) {
                        symbolTable.add(new Symbol(word, lastType, currentScope, lineNo));
                    }
                }

                if (ch != -1)
                    br.reset();
            }

            // ---------- NUMBER ----------
            else if (Character.isDigit(c)) {
                word = "" + c;

                while ((ch = br.read()) != -1 && Character.isDigit((char)ch)) {
                    word += (char) ch;
                }

                tokenId++;
                System.out.println(tokenId + "\t" + word + "\t\tNUMBER\t\t" + lineNo);

                if (ch != -1)
                    br.reset();
            }

            // ---------- OPERATORS ----------
            else if ("+-*/=<>" .indexOf(c) != -1) {
                tokenId++;
                System.out.println(tokenId + "\t" + c + "\t\tOPERATOR\t" + lineNo);
            }

            // ---------- SYMBOLS & SCOPE ----------
            else if ("{}();,".indexOf(c) != -1) {
                tokenId++;
                System.out.println(tokenId + "\t" + c + "\t\tSYMBOL\t\t" + lineNo);

                if (c == '{')
                    currentScope = "local";
                else if (c == '}')
                    currentScope = "global";
            }
        }

        br.close();

        // ---------- PRINT SYMBOL TABLE ----------

        System.out.println("\n--- SYMBOL TABLE ---");
        System.out.println("Name\tType\tScope\tLine");
        System.out.println("--------------------------------");

        for (Symbol s : symbolTable) {
            System.out.println(s.name + "\t" + s.type + "\t" + s.scope + "\t" + s.line);
        }
    }
}
