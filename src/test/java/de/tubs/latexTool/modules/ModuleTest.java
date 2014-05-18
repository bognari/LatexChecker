package de.tubs.latexTool.modules;

import org.junit.*;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

/**
 * Module Tester.
 *
 * @author <Authors Name>
 * @version 1.0
 * @since <pre>Apr 21, 2014</pre>
 */
public class ModuleTest {

  private Logger log;
  private ThreadPoolExecutor threadPool;

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
  }

  /**
   * Method: getModul(String mName, Map<String, Object> config)
   */
//@Test
  @SuppressWarnings("unchecked")
    /*public void testGetModul() throws Exception {
        long time = System.nanoTime();
        LogHandler logHandler = new LogHandler();
        mLog.addHandler(logHandler);
        mLog.setLevel(Level.ALL);

        Logger log2 = mLog;
        App app = new App();

        //Api.settings().loadJson("config.json");

        Api.setSetting("TEX_PATH", "test/tex/zusammenfassung.tex");

        app.run();

        Map<String, Map> modules = Api.settings().getModules();

        if (modules.isEmpty()) {
            return;
        }

        threadPool = new ThreadPoolExecutor(modules.size(), modules.size(), 10, TimeUnit.SECONDS, new LinkedTransferQueue<Runnable>());
        Timer timer = new Timer(true);
        if (Boolean.valueOf(Api.getSetting("IS_ALIVE"))) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    String s = String.format("Module %d threads are running, (%d / %4$d) are completed and (%d  / %4$d) are queued", threadPool.getActiveCount(), threadPool.getCompletedTaskCount(), threadPool.getQueue().size(), threadPool.getTaskCount());
                    mLog.finest(s);
                    //System.out.println(s);
                    String t = String.format("Module: %3d%%", threadPool.getTaskCount() <= 0 ? 0 : (threadPool.getCompletedTaskCount() * 100) / threadPool.getTaskCount());
                    mLog.finest(t);
                    System.out.println(t);
                }
            }, 100, 500);
        }

        for (String mName : modules.keySet()) {
            if (!mName.startsWith("#")) {
                try {
                    Module module = Module.getModul(mName, modules.get(mName));
                    threadPool.execute(module);
                } catch (IllegalArgumentException e) {
                    mLog.severe(String.format("Can not load Modul %s", mName));
                }
            } else {
                mLog.fine(String.format("skip module: %s", mName));
            }
        }

        threadPool.shutdown();
        threadPool.awaitTermination(15, TimeUnit.MINUTES);
        timer.cancel();

        System.out.printf("Time: %.3f%n######################%n", (System.nanoTime() - time) / 1000000.0);

        String s = "kjhjvhcf";
} */


  /**
   * Method: toString()
   */
  @Test
  public void testToString() throws Exception {
//TODO: Test goes here... 
  }


} 
