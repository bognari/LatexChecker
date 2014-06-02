package de.tubs.latexTool.core;

import de.tubs.latexTool.core.util.LogHandler;
import org.junit.After;
import org.junit.Before;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tex Tester.
 *
 * @author <Authors Name>
 * @version 1.0
 * @since <pre>Nov 10, 2013</pre>
 */
public class TexTest {
  private static final Logger sLog = Logger.getLogger("de.tubs.latexTool");

  private static StringBuilder write(DocumentTree dt, StringBuilder stringBuilder) {
    stringBuilder.append(String.format("Name:  %s%nLevel: %d%nDatei: %s   %d%n%n", dt.getHeadline(), dt.getLevel(), dt.getPosition().getFile(), dt.getPosition().getLine()));
    for (DocumentTree child : dt.child()) {
      write(child, stringBuilder);
    }
    return stringBuilder;
  }

  @After
  public void after() throws Exception {
  }

  @Before
  public void before() throws Exception {
    sLog.addHandler(new LogHandler());
    sLog.setLevel(Level.ALL);
  }

  //@Test
    /*public void test() throws Exception {
        Logger log2 = mLog;

        App app = new App();

        Api.settings().loadJson("config.json");
        Api.setSetting("TEX_PATH", "test/tex/zusammenfassung.tex");

        app.run();

        long time = System.nanoTime();


        List<Environment> list = Api.getEnvironments("document");

        //String pattern = PartsWL.getPartsPattern().pattern();

        DocumentTree documentTree = Api.getDocumentTreeRoot();

        List<Text> all = Api.allTexts();
        System.out.printf("Time: %.3f%n######################%n", (System.nanoTime() - time) / 1000000.0);


        BufferedWriter writer = Files.newBufferedWriter(Paths.get("bla.txt"), Charset.defaultCharset());

        for (Text text : all) {
            Position positionEntry = text.getPosition();
            //System.out.printf("Satz: %s%nDatei %s   %d%n%n", text.getText(), lineNumberEntry.getmFile(), lineNumberEntry.getLine());
            writer.write(String.format("Satz:  %s%nDatei: %s   %d%n%n", text.getText(), positionEntry.getmFile(), positionEntry.getLine()));
        }
        writer.flush();
        writer.close();

        System.out.print("bla");

        BufferedWriter writer2 = Files.newBufferedWriter(Paths.get("bla2.txt"), Charset.defaultCharset());
        writer2.write(write(documentTree, new StringBuilder()).toString());
        writer2.flush();
        writer2.close();

        System.out.print("bla2");

        List<Command> list2 = Api.getCommands("BookTitle");

        BufferedWriter writer3 = Files.newBufferedWriter(Paths.get("bla3.txt"), Charset.defaultCharset());
        writer3.write(Api.getApp().getTex().getContent());
        writer3.flush();
        writer3.close();

        System.out.println("sd.kjfzdakus");
    }*/
} 
