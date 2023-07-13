package ca.jrvs.apps;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {


  public static void setLogger(Logger logger) {
    Main.logger = logger;
  }

  static Logger logger = LoggerFactory.getLogger(Main.class);

  static String log4jConfigPath = "src/main/conf/log4j.properties";

  public String getLog4jConfigPath() {
    return log4jConfigPath;
  }

  public static void main(String[] args) {
    PropertyConfigurator.configure(Main.log4jConfigPath);
    System.out.println("Hello world!");
    logger.info("Info level log");
  }
}