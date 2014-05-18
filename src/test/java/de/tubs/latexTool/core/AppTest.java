package de.tubs.latexTool.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * App Tester.
 *
 * @author <Authors Name>
 * @version 1.0
 * @since <pre>Apr 6, 2014</pre>
 */
public class AppTest {

  @After
  public void after() throws Exception {
  }

  @Before
  public void before() throws Exception {
  }

  /**
   * Method: getTex()
   */
  @Test
  public void testGetTex() throws Exception {

    StringBuilder stringBuilder = new StringBuilder("xxxxXxxxxXxxxxXxxxx");
    String a = stringBuilder.toString();
    stringBuilder.insert(4, "OOOO");
    String b = stringBuilder.toString();
    stringBuilder.replace(3, 9, "88888");
    String c = stringBuilder.toString();
    System.out.print(' ');
//TODO: Test goes here...
  }

  /**
   * Method: loadConfig(String path)
   */
  @Test
  public void testLoadConfig() throws Exception {
//TODO: Test goes here...
  }

  /**
   * Method: loadTex()
   */
  @Test
  public void testLoadTex() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = App.getClass().getMethod("loadTex");
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
  }

  /**
   * Method: main(String[] args)
   */
  @Test
  public void testMain() throws Exception {
    String a = "bla";
    String b = "bla";
    String c = new String("bla");

    System.out.print("bla");

  }

  /**
   * Method: runModules()
   */
  @Test
  public void testRunModules() throws Exception {
//TODO: Test goes here...
/*
try {
   Method method = App.getClass().getMethod("runModules");
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
  }

  /**
   * Method: SettingObject()
   */
  @Test
  public void testSettingObject() throws Exception {
//TODO: Test goes here...
  }

} 
