package de.tubs.latexTool.core.util;

import de.tubs.latexTool.core.App;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Util Tester.
 *
 * @author <Authors Name>
 * @version 1.0
 * @since <pre>Apr 12, 2014</pre>
 */
public class MiscTest {
  private App mApp;

  @After
  public void after() throws Exception {
  }

  @Before
  public void before() throws Exception {
    mApp = new App();
    mApp.SettingObject().parse("-l de".split(" "));
    mApp.SettingObject().loadJson("config.json");
  }

  /**
   * Method: getSentences(String input)
   */
  @Test
  public void testGetSentences() throws Exception {
//TODO: Test goes here...
  }

  /**
   * Method: masking(String input)
   */
  @Test
  public void testMaskingAbb() throws Exception {
//TODO: Test goes here...
  }

  /**
   * Method: maskingEnvironment(String input)
   */
  @Test
  public void testMaskingEnvironment() throws Exception {
    String test = "\\begin{align*}\n" +
            "l_{s,t} &= c(e_1) + \\dots + c(e_n)\\\\\n" +
            "\t\t&= -log(r(e_1)) - \\dots - log(r(e_n))\\\\\n" +
            "\t\t&= -log(r(e_1) \\cdot \\dots \\cdot r(e_n))\n" +
            "\\end{align*}";

    String expected = "                                                                                                                                                     ";
    String result = Misc.maskingEnvironment(test);
    assertEquals(expected, result);

    // ##########################################################

    test = "\\subsection*{Shift-Operationen}\n" +
            "\n" +
            "Damit nicht jedes mal binär gerechnet werden muss hier ein paar Formeln.\\\\\n" +
            "\n" +
            "Ein Linksshift $x << y$ verschiebt man die Zahl $x$ in Binärdarstellung um $y$ stellen. Somit ergibt sich die Formel\n" +
            "\\begin{displaymath}\n" +
            "x \\cdot 2^y.\n" +
            "\\end{displaymath} Als Beispiel nehmen wir den Aufruf $9_{10} << 3_{10}$ = $000\\;1001_{2} << 3_{10} = 100\\;1000_{2} = 72_{10}$ und mit der Formel ergibt sich $9 \\cdot 2^3 = 9 \\cdot 8 = 72$. Falls jedoch das Ergebnis größer als der maximale Wert des Datentyps ist, ist das Ergebnis der Formel falsch!\\\\\n" +
            "\n" +
            "Beim Rechtsshift $x >> y$ verschiebt man die Zahl $x$ in Binärdarstellung um $y$ stellen. Hier ergeben sich zwei Formeln. Für den Fall $x > 0$ gilt\n" +
            "\\begin{displaymath}\n" +
            "\\left\\lfloor x \\cdot \\left(\\frac{1}{2}\\right)^y\\right\\rfloor \\mbox{ (abrunden)}\n" +
            "\\end{displaymath}\n" +
            "und für den Fall $x \\leq 0$ gilt \n" +
            "\\begin{displaymath}\n" +
            "\\left\\lceil x \\cdot \\left(\\frac{1}{2}\\right)^y\\right\\rceil \\mbox{ (aufrunden).}\n" +
            "\\end{displaymath}Beispiel $9_{10} >> 2_{10}$ = $1001_{2} >> 2_{10} = 10_{2} = 2_{10}$ und mit der Formel ergibt sich $\\left\\lfloor 9 \\cdot \\left(\\frac{1}{2}\\right)^2 \\right\\rfloor =  \\left\\lfloor 9 \\cdot \\frac{1}{4} \\right\\rfloor = 2$. Bei $-9_{10} >> 2_{10}$ (Byte) = $1111\\;0111_{2} >> 2_{10} = 1111\\;1101_{2} = -3_{10}$ und mit der Formel ergibt sich $\\left\\lceil -9 \\cdot \\left(\\frac{1}{2}\\right)^2 \\right\\rceil =  \\left\\lceil -9 \\cdot \\frac{1}{4} \\right\\rceil = -3$.\\\\";

    expected = "\\subsection*{Shift-Operationen}\n" +
            "\n" +
            "Damit nicht jedes mal binär gerechnet werden muss hier ein paar Formeln.\\\\\n" +
            "\n" +
            "Ein Linksshift $x << y$ verschiebt man die Zahl $x$ in Binärdarstellung um $y$ stellen. Somit ergibt sich die Formel\n" +
            "$                  \n" +
            "x \\cdot 2^y$.                  Als Beispiel nehmen wir den Aufruf $9_{10} << 3_{10}$ = $000\\;1001_{2} << 3_{10} = 100\\;1000_{2} = 72_{10}$ und mit der Formel ergibt sich $9 \\cdot 2^3 = 9 \\cdot 8 = 72$. Falls jedoch das Ergebnis größer als der maximale Wert des Datentyps ist, ist das Ergebnis der Formel falsch!\\\\\n" +
            "\n" +
            "Beim Rechtsshift $x >> y$ verschiebt man die Zahl $x$ in Binärdarstellung um $y$ stellen. Hier ergeben sich zwei Formeln. Für den Fall $x > 0$ gilt\n" +
            "$                  \n" +
            "\\left\\lfloor x \\cdot \\left(\\frac{1}{2}\\right)^y\\right\\rfloor \\mbox{ (abrunden)}$                 \n" +
            "und für den Fall $x \\leq 0$ gilt \n" +
            "$                  \n" +
            "\\left\\lceil x \\cdot \\left(\\frac{1}{2}\\right)^y\\right\\rceil \\mbox{ (aufrunden).}$                 Beispiel $9_{10} >> 2_{10}$ = $1001_{2} >> 2_{10} = 10_{2} = 2_{10}$ und mit der Formel ergibt sich $\\left\\lfloor 9 \\cdot \\left(\\frac{1}{2}\\right)^2 \\right\\rfloor =  \\left\\lfloor 9 \\cdot \\frac{1}{4} \\right\\rfloor = 2$. Bei $-9_{10} >> 2_{10}$ (Byte) = $1111\\;0111_{2} >> 2_{10} = 1111\\;1101_{2} = -3_{10}$ und mit der Formel ergibt sich $\\left\\lceil -9 \\cdot \\left(\\frac{1}{2}\\right)^2 \\right\\rceil =  \\left\\lceil -9 \\cdot \\frac{1}{4} \\right\\rceil = -3$.\\\\";
    result = Misc.maskingEnvironment(test);
    assertEquals(expected, result);

    // ##########################################################

    test = "\\begin{frame}\n" +
            "\\frametitle{Ausdrücke}\n" +
            "\\begin{block}{\\vspace*{-3ex}}\n" +
            "elementare Ausdrücke bzw. Grundterme setzten sich zusammen aus:\n" +
            "\\begin{itemize}\n" +
            "  \\item Konstanten wie z.B. Zahlen (\\lstinline|10|, \\lstinline|9.8|), Zeichen (\\lstinline|'a'|, \\lstinline|'Z'|), \\ldots\n" +
            "  \\item Andere Funktionen \\lstinline|sin|, \\lstinline|+|, \\lstinline|*|, \\ldots\n" +
            "\\end{itemize}\n" +
            "\\end{block}\n" +
            "\\begin{alertblock}{Infixnotation}\n" +
            "Funktionszeichen: \\lstinline|3 + 4| $\\equiv$ \\lstinline|(+) 3 4|\\\\\n" +
            "Funktionsname: \\lstinline|mod 100 4| $\\equiv$ \\lstinline|100 `mod` 4|\n" +
            "\\end{alertblock}\n" +
            "\\end{frame}";
    expected = "             \n" +
            "\\frametitle{Ausdrücke}\n" +
            "                             \n" +
            "elementare Ausdrücke bzw. Grundterme setzten sich zusammen aus:\n" +
            "                                                                                                                                                                                                                                                  \n" +
            "                                 \n" +
            "Funktionszeichen: \\lstinline|3 + 4| $\\equiv$ \\lstinline|(+) 3 4|\\\\\n" +
            "Funktionsname: \\lstinline|mod 100 4| $\\equiv$ \\lstinline|100 `mod` 4|                             ";
    result = Misc.maskingEnvironment(test);
    assertEquals(expected, result);

    // ##########################################################

    test = "\\begin{frame}[fragile]\n" +
            "\\frametitle{Ausdrücke} \n" +
            "\\begin{exampleblock}{Elementarer Ausdruck}\n" +
            "\\lstinline|plus = 10 + 30|\n" +
            "\\end{exampleblock}\n" +
            "\\begin{exampleblock}{Ausdruck}\n" +
            "\\lstinline|plus' a b = a + b|\n" +
            "\\end{exampleblock}\n" +
            "\\begin{exampleblock}{Lambda ($\\lambda$)-Ausdruck}\n" +
            "\\lstinline|plus'' =  \\\\a -> \\\\b -> a + b|\\\\\n" +
            "Morgen kommt mehr zum Thema $\\lambda$-Ausdrücke\n" +
            "\\end{exampleblock}\n" +
            "\\end{frame}";
    expected = "                      \n" +
            "\\frametitle{Ausdrücke} \n" +
            "                                          \n" +
            "\\lstinline|plus = 10 + 30|                   \n" +
            "                              \n" +
            "\\lstinline|plus' a b = a + b|                   \n" +
            "                                                 \n" +
            "\\lstinline|plus'' =  \\\\a -> \\\\b -> a + b|\\\\\n" +
            "Morgen kommt mehr zum Thema $\\lambda$-Ausdrücke                               ";
    result = Misc.maskingEnvironment(test);
    assertEquals(expected, result);

    // ##########################################################

    test = "\\begin{frame}\n" +
            "\\frametitle{Paradigmen}\n" +
            "\\begin{block}{\\vspace*{-3ex}}\n" +
            "\\begin{itemize}\n" +
            "  \\item Programmierparadigma\\\\\n" +
            "\t\tgenerelle Sicht bei der Modellierung und Lösung eines Problems\n" +
            "  \\item Klassische Unterscheidung\n" +
            "  \\begin{itemize}\n" +
            "    \\item Imperative Sprachen\\\\\n" +
            "    \t \\textbf{\"`Wie\"'} findet die Lösung statt\\\\\n" +
            "    \t Folge von Anweisungen zur Problemlösung  \n" +
            "    \\item Deklarative Sprachen \\\\\n" +
            "    \t \\textbf{\"`Was\"'} ist die Lösung \\\\\n" +
            "    \t deklarative Beschreibung der Lösung bzw. des Problems\n" +
            "  \\end{itemize}\n" +
            "\\end{itemize}\n" +
            "\\end{block}\n" +
            "\\end{frame}";
    expected = "             \n" +
            "\\frametitle{Paradigmen}\n" +
            "                             \n" +
            "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 ";
    result = Misc.maskingEnvironment(test);
    assertEquals(expected, result);
  }

  /**
   * Method: maskingLatex(String input)
   */
  @Test
  public void testMaskingLatex() throws Exception {
    String test = "\\section{Das funktionale Paradigma}\n" +
            "\\begin{frame}\n" +
            "\\frametitle{Paradigmen}\n" +
            "\\begin{block}{\\vspace*{-3ex}}\n" +
            "\\begin{itemize}\n" +
            "  \\item Programmierparadigma\\\\\n" +
            "\t\tgenerelle Sicht bei der Modellierung und Lösung eines Problems\n" +
            "  \\item Klassische Unterscheidung\n" +
            "  \\begin{itemize}\n" +
            "    \\item Imperative Sprachen\\\\\n" +
            "    \t \\textbf{\"`Wie\"'} findet die Lösung statt\\\\\n" +
            "    \t Folge von Anweisungen zur Problemlösung  \n" +
            "    \\item Deklarative Sprachen \\\\\n" +
            "    \t \\textbf{\"`Was\"'} ist die Lösung \\\\\n" +
            "    \t deklarative Beschreibung der Lösung bzw. des Problems\n" +
            "  \\end{itemize}\n" +
            "\\end{itemize}\n" +
            "\\end{block}\n" +
            "\\end{frame}";

    String expected = "\\section{Das funktionale Paradigma}\n" +
            "\\begin{frame}\n" +
            "\\frametitle{Paradigmen}\n" +
            "\\begin{block}{\\vspace*{-3ex}}\n" +
            "\\begin{itemize}\n" +
            "  \\item Programmierparadigma\n" +
            "\n" +
            "\n" +
            "\t\tgenerelle Sicht bei der Modellierung und Lösung eines Problems\n" +
            "  \\item Klassische Unterscheidung\n" +
            "  \\begin{itemize}\n" +
            "    \\item Imperative Sprachen\n" +
            "\n" +
            "\n" +
            "    \t            \"Wie\" findet die Lösung statt\n" +
            "\n" +
            "\n" +
            "    \t Folge von Anweisungen zur Problemlösung  \n" +
            "    \\item Deklarative Sprachen \n" +
            "\n" +
            "\n" +
            "    \t            \"Was\" ist die Lösung \n" +
            "\n" +
            "\n" +
            "    \t deklarative Beschreibung der Lösung bzw. des Problems\n" +
            "  \\end{itemize}\n" +
            "\\end{itemize}\n" +
            "\\end{block}\n" +
            "\\end{frame}";
    String result = Misc.maskingLatex(test, true);
    assertEquals(expected, result);

    test = "Beschreiben Sie das Rückgabeobjekt der Methode \\lstinline|wasPassiert(int[] a, int[] b)| in Abhängigkeit von\n" +
            "den Parametern \\lstinline|a| und \\lstinline|b|. (Exceptions sollen mit angegeben werden, obwohl es nicht in der Aufgabe stand)\\\\" +
            "\\cite[S. 15]{lin1973}. An dieser Stelle\\footnote[42]{Dies ist die Fußnote.} erscheint eine\n" +
            "  Fußnote.";
    expected = "Beschreiben Sie das Rückgabeobjekt der Methode             wasPassiert(int[] a, int[] b) in Abhängigkeit von\n" +
            "den Parametern             a und             b. (Exceptions sollen mit angegeben werden, obwohl es nicht in der Aufgabe stand)\n" +
            "\n" +
            "              lin1973. An dieser Stelle\\footnote[42]{Dies ist die Fußnote.} erscheint eine\n" +
            "  Fußnote.";
    result = Misc.maskingLatex(test, true);
    assertEquals(expected, result);
  }

  /**
   * Method: noSingleLF(String input)
   */
  @Test
  public void testNoSingleLF() throws Exception {
    String test = "";

  }

  /**
   * Method: getCommand(MatchResult matchResult, String content)
   */
  @Test
  public void testReadLatexCommand() throws Exception {
//TODO: Test goes here...
  }

  /**
   * Method: removeLatex(String input)
   */
  @Test
  public void testRemoveLatex() throws Exception {
    String test = "\\section{Haskell}\n" +
            "\\begin{frame}\n" +
            "\\frametitle{Haskell}\n" +
            "\\begin{block}{\\vspace*{-3ex}}\n" +
            "\\begin{itemize}\n" +
            "  \\item 1990 als Haskell 1.0 veröffentlicht\n" +
            "  \\item Aktuelle Version Haskell 2010\n" +
            "  \\item An Haskell 2014 (Preview) wird \"`gearbeitet\"'\n" +
            "\\end{itemize}\n" +
            "\\end{block}\n" +
            "\\end{frame}\n" +
            "\n" +
            "\\begin{frame}[fragile]\n" +
            "\\frametitle{Hello World} \n" +
            "\\begin{lstlisting}\n" +
            "module Main where\n" +
            " \n" +
            "main :: IO ()\n" +
            "main = putStrLn \"Hello, World!\"\n" +
            "\\end{lstlisting}\n" +
            "\\pause\n" +
            "\\begin{block}{Ausgabe}\n" +
            "\\begin{center}\n" +
            "Hello, World!\n" +
            "\\end{center}\n" +
            "\\end{block}\n" +
            "\\pause\n" +
            "\\begin{center}\n" +
            "weiteres in den Übungen\n" +
            "\\end{center}\n" +
            "\\end{frame}\n" +
            "\n" +
            "\\begin{frame}\n" +
            "\\frametitle{Haskell Compiler}\n" +
            "\\begin{block}{\\vspace*{-3ex}}\n" +
            "\\begin{itemize}\n" +
            "  \\item Hugs (Haskell User's Gofer System) \\\\ implementiert Haskell 98 \\\\ seit ca 6 Jahren nicht weiterentwickelt\n" +
            "  \\item Yhc (York Haskell Compiler) \\\\ implementiert Haskell 98 \\\\ Projekt eingestellt\n" +
            "  \\item GHC (Glasgow Haskell Compiler) \\\\ implementiert Haskell 98 / 2010 \\\\ weit verbreitster Haskell Compiler \\\\ \n" +
            "  besitzt den GHCi als Haskell Interpreter \\\\ in den Übungen werden wir hauptsächlich mit dem Interpreter arbeiten\n" +
            "\\end{itemize}\n" +
            "\\end{block}\n" +
            "\\end{frame}";
    String result = Misc.removeLatex(test);
    String expected = "                 \n" +
            "             \n" +
            "                    \n" +
            "                             \n" +
            "               \n" +
            "        1990 als Haskell 1.0 veröffentlicht\n" +
            "        Aktuelle Version Haskell 2010\n" +
            "        An Haskell 2014 (Preview) wird \"`gearbeitet\"'\n" +
            "             \n" +
            "           \n" +
            "           \n" +
            "\n" +
            "                      \n" +
            "                         \n" +
            "                  \n" +
            "module Main where\n" +
            " \n" +
            "main :: IO ()\n" +
            "main = putStrLn \"Hello, World!\"\n" +
            "                \n" +
            "      \n" +
            "                      \n" +
            "              \n" +
            "Hello, World!\n" +
            "            \n" +
            "           \n" +
            "      \n" +
            "              \n" +
            "weiteres in den Übungen\n" +
            "            \n" +
            "           \n" +
            "\n" +
            "             \n" +
            "                             \n" +
            "                             \n" +
            "               \n" +
            "        Hugs (Haskell User's Gofer System)    implementiert Haskell 98    seit ca 6 Jahren nicht weiterentwickelt\n" +
            "        Yhc (York Haskell Compiler)    implementiert Haskell 98    Projekt eingestellt\n" +
            "        GHC (Glasgow Haskell Compiler)    implementiert Haskell 98 / 2010    weit verbreitster Haskell Compiler    \n" +
            "  besitzt den GHCi als Haskell Interpreter    in den Übungen werden wir hauptsächlich mit dem Interpreter arbeiten\n" +
            "             \n" +
            "           \n" +
            "           ";

    assertEquals(expected, result);

    test = "\\begin{minipage}[hbt]{7cm}\n" +
            "\t\\centering\n" +
            "$\\begin{array}{r|r|r|r}\n" +
            "\\text{Aufruf} & f(x, y)\t& \\text{Term} \t\t\t& \\text{Wert}\t\\\\ \\hline\n" +
            "1\t\t\t  & f(3,-1) & 3 * f(1, 0) - f(3, 0)\t& 2\\\\\n" +
            "2\t\t\t  & f(1,0) \t& 2\t\t\t\t\t\t& 2\\\\\n" +
            "3\t\t\t  & f(3,0) \t& 3 * f(1, 1) - f(3, 1)\t& 4\\\\\n" +
            "4\t\t\t  & f(1,1)  & 2\t\t\t\t\t\t& 2\\\\\n" +
            "5\t\t\t  & f(3,1)  & 3 * f(1, 2) - f(3, 2)\t& 2\\\\\n" +
            "6\t\t\t  & f(1,2)  & 2\t\t\t\t\t\t& 2\\\\\n" +
            "7\t\t\t  & f(3,2)  & 3 * f(1, 3) - f(3, 3)\t& 4\\\\\n" +
            "8\t\t\t  & f(1,3)  & 2\t\t\t\t\t\t& 2\\\\\n" +
            "9\t\t\t  & f(3,3)  & 3 * f(1, 4) - f(3, 4)\t& 2\\\\\n" +
            "10\t\t\t  & f(1,4)  & 1\t\t\t\t\t\t& 1\\\\\n" +
            "11\t\t\t  & f(3,4)  & 1\t\t\t\t\t\t& 1\\\\\n" +
            "\\end{array}$\n" +
            "\\end{minipage}\n" +
            "\\hfill\n" +
            "\\begin{minipage}[hbt]{7cm}\n" +
            "\t\\Tree [.f(3,-1) [.f(1,0) 2 ] [.f(3,0) [.f(1,1) 2 ] [.f(3,1) [.f(1,2) 2 ] [.f(3,3) [.f(1,3) 2 ] [.f(3,3) [.f(1,4) 1 ] [.f(3,4) 1 ] ] ] ] ] ]\n" +
            "\\end{minipage}\n" +
            "                              \n" +
            "Somit ist \\lstinline|f(3,-1)| = 2, die Anzahl der Aufrufe ist 11 (Tabelle) und die maximale Rekursionstiefe ist 6 weil:\n" +
            "$f(3,-1) \\to f(3,0) \\to f(3,1) \\to f(3,2) \\to f(3,3) \\to f(3,4)$ hat eine Tiefe von 6.\n" +
            "\\newpage\n" +
            "\\mysection{Objektorientierung}\n" +
            "Gegeben sei die Klasse \\textbf{Rational} zur Darstellung von\n" +
            "positiven rationalen Zahlen, d. h. von Zahlen $q \\in \\mathbb{Q}, q > 0$. Die Methode \\lstinline|add| soll den\n" +
            "Parameter \\lstinline|r| zur aktuellen Zahl addieren. Die Methode \\lstinline|compareTo| soll rationale Zahlen\n" +
            "ihrer Größe nach vergleichen.";

    result = Misc.removeLatex(test);
    expected = "                          \n" +
            "\t          \n" +
            "$                      \n" +
            "              & f(x, y)\t&             \t\t\t&            \t         \n" +
            "1\t\t\t  & f(3,-1) & 3 * f(1, 0) - f(3, 0)\t& 2  \n" +
            "2\t\t\t  & f(1,0) \t& 2\t\t\t\t\t\t& 2  \n" +
            "3\t\t\t  & f(3,0) \t& 3 * f(1, 1) - f(3, 1)\t& 4  \n" +
            "4\t\t\t  & f(1,1)  & 2\t\t\t\t\t\t& 2  \n" +
            "5\t\t\t  & f(3,1)  & 3 * f(1, 2) - f(3, 2)\t& 2  \n" +
            "6\t\t\t  & f(1,2)  & 2\t\t\t\t\t\t& 2  \n" +
            "7\t\t\t  & f(3,2)  & 3 * f(1, 3) - f(3, 3)\t& 4  \n" +
            "8\t\t\t  & f(1,3)  & 2\t\t\t\t\t\t& 2  \n" +
            "9\t\t\t  & f(3,3)  & 3 * f(1, 4) - f(3, 4)\t& 2  \n" +
            "10\t\t\t  & f(1,4)  & 1\t\t\t\t\t\t& 1  \n" +
            "11\t\t\t  & f(3,4)  & 1\t\t\t\t\t\t& 1  \n" +
            "           $\n" +
            "              \n" +
            "      \n" +
            "                          \n" +
            "\t      [.f(3,-1) [.f(1,0) 2 ] [.f(3,0) [.f(1,1) 2 ] [.f(3,1) [.f(1,2) 2 ] [.f(3,3) [.f(1,3) 2 ] [.f(3,3) [.f(1,4) 1 ] [.f(3,4) 1 ] ] ] ] ] ]\n" +
            "              \n" +
            "                              \n" +
            "Somit ist                     = 2, die Anzahl der Aufrufe ist 11 (Tabelle) und die maximale Rekursionstiefe ist 6 weil:\n" +
            "$f(3,-1)     f(3,0)     f(3,1)     f(3,2)     f(3,3)     f(3,4)$ hat eine Tiefe von 6.\n" +
            "        \n" +
            "                              \n" +
            "Gegeben sei die Klasse                   zur Darstellung von\n" +
            "positiven rationalen Zahlen, d. h. von Zahlen $q               , q > 0$. Die Methode                 soll den\n" +
            "Parameter               zur aktuellen Zahl addieren. Die Methode                       soll rationale Zahlen\n" +
            "ihrer Größe nach vergleichen.";
    assertEquals(expected, result);
  }

  @Test
  public void testRemoveReNew() throws Exception {
    String test = "\\makeatletter\n" +
            "\\let\\beamer@writeslidentry@miniframeson=\\beamer@writeslidentry\n" +
            "\\def\\beamer@writeslidentry@miniframesoff{\n" +
            "  \\expandafter\\beamer@ifempty\\expandafter{\\beamer@framestartpage}{}\n" +
            "  {\n" +
            "    \\clearpage\\beamer@notesactions\n" +
            "  }\n" +
            "}\n" +
            "\\newcommand*{\\miniframeson}{\\let\\beamer@writeslidentry=\\beamer@writeslidentry@miniframeson}\n" +
            "\\newcommand*{\\miniframesoff}{\\let\\beamer@writeslidentry=\\beamer@writeslidentry@miniframesoff}\n" +
            "\\makeatother\n" +
            "\n" +
            "\\defbeamertemplate*{section in toc}{my theme}\n" +
            "{\\leavevmode\\leftskip=0.5em\\large{\\usebeamercolor[fg]{titlelike}} \\inserttocsection\\par}\n" +
            "\n" +
            "\\defbeamertemplate*{subsection in toc}{my theme}\n" +
            "{\\leavevmode\\leftskip=2em\\normalsize{\\usebeamercolor[fg]{titlelike}} \\inserttocsubsection\\par}\n" +
            "\n" +
            "\\defbeamertemplate*{subsubsection in toc}{my theme}\n" +
            "{\\leavevmode\\leftskip=3.5em\\normalsize\\usebeamerfont{subsection in toc}\\usebeamerfont{subsubsection in toc}\\inserttocsubsubsection\\par}\n";
    String result = Misc.removeReNew(test);
    String expected = "\\makeatletter\n" +
            "\\let\\beamer@writeslidentry@miniframeson=\\beamer@writeslidentry\n" +
            "\\def\\beamer@writeslidentry@miniframesoff{\n" +
            "  \\expandafter\\beamer@ifempty\\expandafter{\\beamer@framestartpage}{}\n" +
            "  {\n" +
            "    \\clearpage\\beamer@notesactions\n" +
            "  }\n" +
            "}\n" +
            "                                                                                           \n" +
            "                                                                                             \n" +
            "\\makeatother\n" +
            "\n" +
            "\\defbeamertemplate*{section in toc}{my theme}\n" +
            "{\\leavevmode\\leftskip=0.5em\\large{\\usebeamercolor[fg]{titlelike}} \\inserttocsection\\par}\n" +
            "\n" +
            "\\defbeamertemplate*{subsection in toc}{my theme}\n" +
            "{\\leavevmode\\leftskip=2em\\normalsize{\\usebeamercolor[fg]{titlelike}} \\inserttocsubsection\\par}\n" +
            "\n" +
            "\\defbeamertemplate*{subsubsection in toc}{my theme}\n" +
            "{\\leavevmode\\leftskip=3.5em\\normalsize\\usebeamerfont{subsection in toc}\\usebeamerfont{subsubsection in toc}\\inserttocsubsubsection\\par}\n";

    assertEquals(expected, result);

    test = "\\usepackage{hyperref}\n" +
            "\n" +
            "\\newcommand{\\srcDir}{java/}\n" +
            "\\renewcommand{\\thesection{Aufgabe: \\arabic{section}}}\n" +
            "\\renewcommand{\\thesubsection{\\alph{subsection})}}\n" +
            "\\renewcommand{\\thesubsubsection{\\roman{subsubsection})}}\n" +
            "\n" +
            "\\newcommand{\\mychapter}[1]{\n" +
            "   \\phantomsection\n" +
            "   \\renewcommand{\\rightmark}{#1}\\chapter{#1}\n" +
            "}\n" +
            "\n" +
            "\\newcommand{\\mysection}[1]{\n" +
            "   \\phantomsection\n" +
            "   \\renewcommand{\\leftmark}{#1} \\section{#1}\n" +
            "}\n" +
            "\n" +
            "\\renewcommand{\\chaptermark}[1]{ \\markboth{#1}{} }\n" +
            "\\renewcommand{\\sectionmark}[1]{ \\markright{\\thesection \\; (#1)}{} }\n" +
            "\n" +
            "\n" +
            "\\title{Programmieren I Klausurzusammenfassung}\n" +
            "\\date{\\today}\n";
    result = Misc.removeReNew(test);
    expected = "\\usepackage{hyperref}\n" +
            "\n" +
            "                           \n" +
            "                                                     \n" +
            "                                                 \n" +
            "                                                        \n" +
            "\n" +
            "                                                                                             \n" +
            "\n" +
            "                                                                                             \n" +
            "\n" +
            "                                                 \n" +
            "                                                                   \n" +
            "\n" +
            "\n" +
            "\\title{Programmieren I Klausurzusammenfassung}\n" +
            "\\date{\\today}\n";

    assertEquals(expected, result);

  }

  /**
   * Method: sortNewline(String input)
   */
  @Test
  public void testSortNewline() throws Exception {
    //TODO: Test goes here...
  }

  /**
   * Method: sortWhitespaces(String input, String endings)
   */
  @Test
  public void testSortWhitespaces() throws Exception {
    String test = "Gegeben seien die Methode\n" +
            "\\begin{lstlisting}\n" +
            "static int wasPassiert(int[][] a) {\n" +
            "\tint i = 0,\n" +
            "\tl = a[0].length;\n" +
            "\tfor (int j=1; j<a.length; j++) {\n" +
            "\t\tif (a[j].length>=l) {\n" +
            "\t\t\ti = j;\n" +
            "\t\t\tl = a[j].length;\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tint s = 0;\n" +
            "\tfor (int x : a[i]) s += x;\n" +
            "\treturn s;\n" +
            "}\n" +
            "\\end{lstlisting}\n" +
            "sowie das Programmfragment\n" +
            "\\begin{lstlisting}[firstnumber=last]\n" +
            "int[][] a = {{},{-1,3,5},{0},{},{-1,-2,-5},{1,4}};\n" +
            "System.out.println(\"Ergebnis: \"+wasPassiert(a));\n" +
            "\\end{lstlisting}\n" +
            "\\subsection{}\n" +
            "Welchen Wert berechnet die Methode \\lstinline|wasPassiert(int[][] a)|? \n" +
            "Geben Sie den Wert in Abhängigkeit vom Parameter \\lstinline|a| an. \n" +
            "Welche \\textbf{Exceptions} können ggf# ausgelöst werden? \n" +
            "\\newline\n" +
            "\n" +
            "\\textbf{Lösung}:\n" +
            "\\begin{itemize}\n" +
            "  \\item Die Methode erhält ein zweidimensionales Array \\lstinline|int[][] a|\n" +
            "  \\item wir holen uns die Länge des ersten Teilarrays \\lstinline|l = a[0].length|\n" +
            "  \\item wir vergleichen jedes Teilarray \\lstinline|a[j]| mit der momentan bekannten Maximallänge \\lstinline|l|\n" +
            "  \\item wir haben nach der ersten \\textbf{for}-Schleife den Index des letzten längsten Teilarrays \\lstinline|a[i]|\n" +
            "  \\item in der zweiten \\textbf{for}-Schleife summieren wir alle Werte von \\lstinline|a[i]| auf und geben die Summe aus\n" +
            "  \\item falls \\lstinline|a = null| (oder ein Teilarray) ist, wird in Zeile 3 eine \\textbf{NullPointerException} ausgelöst.\n" +
            "  \\item falls \\lstinline|a = {}| ist, also keine Elemente enthält, wird in Zeile 3 eine \\textbf{ArrayIndexOutOfBoundsException} ausgelöst.\n" +
            "\\end{itemize}\n" +
            "\n" +
            "\\subsection{}\n" +
            "Wie lautet die Ausgabe des Programmfragments?\\\\\n" +
            "\n" +
            "\\textbf{Lösung}:\n" +
            "\\begin{itemize}\n" +
            "  \\item \\lstinline|l = 3|\n" +
            "  \\item \\lstinline|i = 4|\n" +
            "  \\item \\lstinline|s = 0 + (-1) + (-2) + (-5)|\n" +
            "  \\item Die Ausgabe ist dann: Ergebnis: -8\n" +
            "\\end{itemize}\n" +
            "\\newpage";

    String result = Misc.maskingLatex(test, true);
    String expected = "Gegeben seien die Methode\n" +
            "\\begin{lstlisting}\n" +
            "static int wasPassiert(int[][] a) {\n" +
            "\tint i = 0,\n" +
            "\tl = a[0].length;\n" +
            "\tfor (int j=1; j<a.length; j++) {\n" +
            "\t\tif (a[j].length>=l) {\n" +
            "\t\t\ti = j;\n" +
            "\t\t\tl = a[j].length;\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tint s = 0;\n" +
            "\tfor (int x : a[i]) s += x;\n" +
            "\treturn s;\n" +
            "}\n" +
            "\\end{lstlisting}\n" +
            "sowie das Programmfragment\n" +
            "\\begin{lstlisting}[firstnumber=last]\n" +
            "int[][] a = {{},{-1,3,5},{0},{},{-1,-2,-5},{1,4}};\n" +
            "System.out.println(\"Ergebnis: \"+wasPassiert(a));\n" +
            "\\end{lstlisting}\n" +
            "\\subsection{}\n" +
            "Welchen Wert berechnet die Methode             wasPassiert(int[][] a)? \n" +
            "Geben Sie den Wert in Abhängigkeit vom Parameter             a an. \n" +
            "Welche          Exceptions können ggf# ausgelöst werden? \n" +
            "\\newline\n" +
            "\n" +
            "         Lösung:\n" +
            "\\begin{itemize}\n" +
            "  \\item Die Methode erhält ein zweidimensionales Array             int[][] a\n" +
            "  \\item wir holen uns die Länge des ersten Teilarrays             l = a[0].length\n" +
            "  \\item wir vergleichen jedes Teilarray             a[j] mit der momentan bekannten Maximallänge             l\n" +
            "  \\item wir haben nach der ersten          for-Schleife den Index des letzten längsten Teilarrays             a[i]\n" +
            "  \\item in der zweiten          for-Schleife summieren wir alle Werte von             a[i] auf und geben die Summe aus\n" +
            "  \\item falls             a = null (oder ein Teilarray) ist, wird in Zeile 3 eine          NullPointerException ausgelöst.\n" +
            "  \\item falls             a = {} ist, also keine Elemente enthält, wird in Zeile 3 eine          ArrayIndexOutOfBoundsException ausgelöst.\n" +
            "\\end{itemize}\n" +
            "\n" +
            "\\subsection{}\n" +
            "Wie lautet die Ausgabe des Programmfragments?\n" +
            "\n" +
            "\n" +
            "\n" +
            "         Lösung:\n" +
            "\\begin{itemize}\n" +
            "  \\item             l = 3\n" +
            "  \\item             i = 4\n" +
            "  \\item             s = 0 + (-1) + (-2) + (-5)\n" +
            "  \\item Die Ausgabe ist dann: Ergebnis: -8\n" +
            "\\end{itemize}\n" +
            "\\newpage";
    assertEquals(expected, result);

    test = result;
    result = Misc.maskingEnvironment(test);
    expected = "Gegeben seien die Methode\n" +
            "                                                                                                                                                                                                                                                         \n" +
            "sowie das Programmfragment\n" +
            "                                                                                                                                                         \n" +
            "\\subsection{}\n" +
            "Welchen Wert berechnet die Methode             wasPassiert(int[][] a)? \n" +
            "Geben Sie den Wert in Abhängigkeit vom Parameter             a an. \n" +
            "Welche          Exceptions können ggf# ausgelöst werden? \n" +
            "\\newline\n" +
            "\n" +
            "         Lösung:\n" +
            "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           \n" +
            "\n" +
            "\\subsection{}\n" +
            "Wie lautet die Ausgabe des Programmfragments?\n" +
            "\n" +
            "\n" +
            "\n" +
            "         Lösung:\n" +
            "                                                                                                                                                                           \n" +
            "\\newpage";
    assertEquals(expected, result);

    test = result;
    result = Misc.removeLatex(test);
    expected = "Gegeben seien die Methode\n" +
            "                                                                                                                                                                                                                                                         \n" +
            "sowie das Programmfragment\n" +
            "                                                                                                                                                         \n" +
            "             \n" +
            "Welchen Wert berechnet die Methode             wasPassiert(int[][] a)? \n" +
            "Geben Sie den Wert in Abhängigkeit vom Parameter             a an. \n" +
            "Welche          Exceptions können ggf# ausgelöst werden? \n" +
            "        \n" +
            "\n" +
            "         Lösung:\n" +
            "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           \n" +
            "\n" +
            "             \n" +
            "Wie lautet die Ausgabe des Programmfragments?\n" +
            "\n" +
            "\n" +
            "\n" +
            "         Lösung:\n" +
            "                                                                                                                                                                           \n" +
            "        ";
    assertEquals(expected, result);

    test = result.replaceAll("\\n|\\t", " ");
    result = Misc.sortWhitespaces(test, ".!?");
    expected = "Gegeben seien die Methode sowie das Programmfragment Welchen Wert berechnet die Methode wasPassiert(int[][] a)?                                                                                                                                                                                                                                                                                                                                                                                                                                                Geben Sie den Wert in Abhängigkeit vom Parameter a an.              Welche Exceptions können ggf# ausgelöst werden?                              Lösung: Wie lautet die Ausgabe des Programmfragments?                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        Lösung:                                                                                                                                                                                     ";
    assertEquals(expected, result);

    test = "Gegeben sei die Klasse \\textbf{Rational} zur Darstellung von\n" +
            "positiven rationalen Zahlen, d. h. von Zahlen $q \\in \\mathbb{Q}, q > 0$. Die Methode \\lstinline|add| soll den\n" +
            "Parameter \\lstinline|r| zur aktuellen Zahl addieren. Die Methode \\lstinline|compareTo| soll rationale Zahlen\n" +
            "ihrer Größe nach vergleichen.\n" +
            "\n" +
            "\\begin{lstlisting}\n" +
            "class Rational extends Comparable {\n" +
            "\tprivate int x, y;\n" +
            "\tpublic Rational(int x, int y) {\n" +
            "\t\tif (x<=0 || y<=0) throw new IllegalArgumentException();\n" +
            "\t\tg = ggt(x,y);\n" +
            "\t\tthis.x = x/g;\n" +
            "\t\tthis.y = y/g;\n" +
            "\t}\n" +
            "\tprivate static int ggt(int a, int b) {\n" +
            "\t\twhile ((b != 0)) {\n" +
            "\t\t\tint r = a % b;\n" +
            "\t\t\ta = b;\n" +
            "\t\t\tb = r;\n" +
            "\t\t}\n" +
            "\t\treturn a;\n" +
            "\t}\n" +
            "\tpublic Rational add(Rational r) {\n" +
            "\t\tint z = x*r.y+r.x*y,\n" +
            "\t\t\tn = y*r.x;\n" +
            "\t\treturn Rational(z, n);\n" +
            "\t}\n" +
            "\tpublic String toString() {\n" +
            "\t\treturn \"(\"+x+\"/\"+y+\")\";\n" +
            "\t}\n" +
            "\tpublic int compareTo(Object o) {\n" +
            "\t\treturn x*o.y-o.x*y;\n" +
            "\t}\n" +
            "}\n" +
            "\\end{lstlisting}\n" +
            "Außerdem sei das folgende Programmfragment gegeben:\n" +
            "\\begin{lstlisting}[firstnumber=last]\n" +
            "\tRational r1 = new Rational(2,3),\n" +
            "\t         r2 = new Rational(5,4);\n" +
            "\tSystem.out.println(add(r1,r2));\n" +
            "\tSystem.out.println(r1.compareTo(r2));\n" +
            "\\end{lstlisting}\n" +
            "\\subsection{}\n" +
            "Die Programmzeilen 1 bis 32 enthalten Fehler. Markieren Sie die Fehler im Programmtext. Achtung: Für Programmstellen, die nicht zu einem Fehler führen, aber als fehlerhaft gekennzeichnet sind, werden Punkte abgezogen.\\\\\n" +
            "\n" +
            "\\textbf{Lösung}:\n" +
            "\\begin{itemize}\n" +
            "  \\item Zeile 1: \\lstinline|extends| erweitert Klassen, aber wir benötigen \\lstinline|implements| für die Implementierung von \\lstinline|Comparable|. \\\\\\lstinline|class Rational implements Comparable|\n" +
            "  \\item Zeile 5: die Variable g hat muss vor dem Benutzen deklariert werden. \\\\\\lstinline|int g = ggt(x,y);|\n" +
            "  \\item Zeile 19: logische Fehler, da sich der Nenner ($n$) aus $Nenner * Nenner$ ergibt und nicht $Nenner * Zähler$. \\\\\\lstinline|n = y*r.y;|\n" +
            "  \\item Zeile 20: Es wird der \\lstinline|new| Operator zum erzeugen neuer Objekte benötigt. \\\\ \\lstinline|return new Rational(z, n);|\n" +
            "  \\item Zeile 26: Der Parameter ist vom Typ \\lstinline|Object| und muss erst nach Rational gecastet werden.\\\\ \\lstinline|int z = x*((Rational) o).y-((Rational) o).x*y;|\n" +
            "  \\item Zeile 31: Die Methode \\lstinline|add| ist eine Methode der Klasse Rational und benötigt einen Parameter vom Typ Rational.\\\\ \\lstinline|System.out.println(r1.add(r2));|\n" +
            "\\end{itemize}\n" +
            "\n" +
            "\\subsection{}\n" +
            "Geben Sie die korrigierten Zeilen an. Was gibt das korrigierte Programm aus?\\\\\n" +
            "\n" +
            "\\textbf{Lösung}:\n" +
            "\\begin{itemize}\n" +
            "  \\item Die \\lstinline|add| Methode berechnet: $\\frac{2}{3} + \\frac{5}{4} = \\frac{2 \\cdot 4 + 5 \\cdot 3}{12} = \\frac{23}{12}$\n" +
            "  \\item Die \\lstinline|compareTo| Methode berechnet: $2 \\cdot 4 - 5 \\cdot 3 = 8 - 15 = -7$\n" +
            "  \\item Somit ist die Ausgabe: \\\\ \n" +
            "  \\tab (23/12)\\\\\n" +
            "  \\tab -7\n" +
            "\\end{itemize}\n" +
            "\n" +
            "\\newpage";

    test = test.replaceAll("\\t", " ");
    test = Misc.noSingleLF(test);
    test = Misc.sortNewline(test);
    test = Misc.maskingLatex(test, true);
    test = Misc.maskingEnvironment(test);
    test = Misc.removeLatex(test);
    test = Abbreviation.masking(test);
    test = Misc.sortWhitespaces(test, ".!?");

    result = test;

    expected = "Gegeben sei die Klasse Rational zur Darstellung von positiven rationalen Zahlen, d# h# von Zahlen $q , q > 0$.                        Die Methode add soll den Parameter r zur aktuellen Zahl addieren.                         Die Methode compareTo soll rationale Zahlen ihrer Größe nach vergleichen.            \n" +
            "\n" +
            "Außerdem sei das folgende Programmfragment gegeben: Die Programmzeilen 1 bis 32 enthalten Fehler.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          Markieren Sie die Fehler im Programmtext. Achtung: Für Programmstellen, die nicht zu einem Fehler führen, aber als fehlerhaft gekennzeichnet sind, werden Punkte abgezogen# \n" +
            "\n" +
            "Lösung: \n" +
            "\n" +
            "Geben Sie die korrigierten Zeilen an.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          Was gibt das korrigierte Programm aus?  \n" +
            "\n" +
            "Lösung: \n" +
            "\n" +
            "                                                                                                                                                                                                                                                                                                                                     ";
    assertEquals(expected, result);
  }

  /**
   * Method: unmasking(String input)
   */
  @Test
  public void testUnMaskingAbb() throws Exception {
    //TODO: Test goes here...
  }

} 
