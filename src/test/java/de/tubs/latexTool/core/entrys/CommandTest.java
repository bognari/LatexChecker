package de.tubs.latexTool.core.entrys;

import de.tubs.latexTool.core.App;
import de.tubs.latexTool.core.Tex;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Command Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mai 11, 2014</pre>
 */
public class CommandTest {

  private App mApp;

  @AfterClass
  public static void testCleanup() {
    // Teardown for data used by the unit tests
  }

  @BeforeClass
  public static void testSetup() {
  }

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
   * Method: equals(Object o)
   */
  @Test
  public void testEquals() throws Exception {
//TODO: Test goes here...
  }

  /**
   * Method: getArgs()
   */
  @Test
  public void testGetArgs() throws Exception {
//TODO: Test goes here... 
  }

  /**
   * Method: getArgsString(char delimer)
   */
  @Test
  public void testGetArgsString() throws Exception {
//TODO: Test goes here... 
  }

  /**
   * Method: getEnd()
   */
  @Test
  public void testGetEnd() throws Exception {
//TODO: Test goes here... 
  }

  /**
   * Method: getLength()
   */
  @Test
  public void testGetLength() throws Exception {
//TODO: Test goes here...
  }

  /**
   * Method: getName()
   */
  @Test
  public void testGetName() throws Exception {
//TODO: Test goes here...
  }

  /**
   * Method: getPosition()
   */
  @Test
  public void testGetPosition() throws Exception {
//TODO: Test goes here...
  }

  /**
   * Method: getStart()
   */
  @Test
  public void testGetStart() throws Exception {
//TODO: Test goes here...
  }

  /**
   * Method: hashCode()
   */
  @Test
  public void testHashCode() throws Exception {
//TODO: Test goes here... 
  }

  /**
   * Method: getCommand(final String command, final int start, final String input)
   */
  @SuppressWarnings("LocalCanBeFinal")
  @Test
  public void testReadCommandSM() throws Exception {
//TODO: Test goes here...
    String content = "\\makeatletter\n" +
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
            "{\\leavevmode\\leftskip=3.5em\\normalsize\\usebeamerfont{subsection in toc}\\usebeamerfont{subsubsection in toc}\\inserttocsubsubsection\\par}\n" +
            "Funktionsname: \\lstinline|mod 100 4| $\\equiv$ \\lstinline|100 `mod` 4|\n" +
            "    \t \\textbf[dfsgt]  {\"`Was\"'}[dgsft] ist die Lösung \\\\\n";
    Tex tex = new Tex(content);

    List<Command> commandList = tex.getCommands("newcommand\\*|large");

    //char a = content.charAt(commandList.get(0).getStart());
    //char b = content.charAt(commandList.get(0).getEnd());

    assert !commandList.isEmpty();
    assertTrue('\\' == content.charAt(commandList.get(0).getStart()));
    assertTrue('}' == content.charAt(commandList.get(0).getEnd()));
    assertTrue('\\' == content.charAt(commandList.get(1).getStart()));
    assertTrue('}' == content.charAt(commandList.get(1).getEnd()));
    assertTrue('\\' == content.charAt(commandList.get(2).getStart()));
    assertTrue('}' == content.charAt(commandList.get(2).getEnd()));

    commandList = tex.getCommands("lstinline|textbf");
    assert !commandList.isEmpty();

    content = "\\begin{itemize}\n" +
            "\n" +
            "\\item  \\qualityReq{10}{Es sollen die Suchanfragen der Bücher stets korrekt ausgegeben werden.}\n" +
            "\\item  \\qualityReq{20}{Die Bibliotheksoftware soll übersichtlich und einfach zu erfassen sein.}\n" +
            "\\item  \\qualityReq{30}{Die Bibliotheksoftware soll einfach zu erlernen sein.}\n" +
            "\\item  \\qualityReq{40}{Die Bibliotheksoftware soll intuitiv und einfach zu bedienen sein.}\n" +
            "\\item  \\qualityReq{50}{Die Bibliotheksoftware soll ressourcensparend sein und in einem sehr guten Verhältnis zum Leistungsniveau\n" +
            "stehen.}\n" +
            "\\item  \\qualityReq{60}{Die Bibliotheksoftware soll fehlertolerant in Bedien- und Eingabefehlern sein.}\n" +
            "\\item  \\qualityReq{70}{Die Bibliotheksoftware soll nicht länger als 2 Sekunden Antwortzeit benötigen.}\n" +
            "\\item  \\qualityReq{80}{Die Bibliotheksoftware soll einen reibungslosen Austausch von Daten mit der Datenbank gewährleisten.}\n" +
            "\n" +
            "\n" +
            "\\end{itemize}";

    commandList = tex.getCommands("");
    assert !commandList.isEmpty();


  }

  /**
   * Method: toString()
   */
  @Test
  public void testToString() throws Exception {
//TODO: Test goes here... 
  }


} 
