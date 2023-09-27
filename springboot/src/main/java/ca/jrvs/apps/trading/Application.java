package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.controller.QuoteController;
import ca.jrvs.apps.trading.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * For learning purpose, manually configure DataSource and JDBCTemplate
 */
@SpringBootApplication(exclude = {JdbcTemplateAutoConfiguration.class, DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class})
public class Application {

  private Logger logger = LoggerFactory.getLogger(Application.class);

//  @Value("${app.init.dailyList}")
//  private String[] intDailyList;

  @Autowired
  private QuoteService quoteService;

  public static void main(String [] args) throws Exception{
    SpringApplication app = new SpringApplication(Application.class);
    app.run(args);
  }


}
