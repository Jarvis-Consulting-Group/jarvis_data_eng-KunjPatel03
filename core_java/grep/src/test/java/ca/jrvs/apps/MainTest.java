package ca.jrvs.apps;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

public class MainTest {

  private static Logger logger;


  @Before
  public void setup() {
    logger = Mockito.mock(Logger.class);
    Main.setLogger(logger);
  }

  @Test
  public void testMain() {
    Main.main(null);
    verify(logger).info("Info level log");
  }

  @Test
  public void testHelloWorldMessage() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    Main.main(null);

    String consoleOutput = outputStream.toString().trim();
    assertEquals("Hello world!", consoleOutput);

    System.setOut(originalOut);
  }


  @Test
  public void testLog4jConfigurationPath() {
    Main main = new Main();
    Main.main(null);

    String expectedConfigPath = "src/main/conf/log4j.properties";
    String actualConfigPath = main.getLog4jConfigPath();

    assertEquals(expectedConfigPath, actualConfigPath);
  }


  @Test
  public void testLoggerInfoLevel() {
    Logger logger = Mockito.mock(Logger.class);

    Main.setLogger(logger);

    Main.main(null);

    verify(logger).info("Info level log");
  }


}