package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class AppConfig {
  private Logger logger = LoggerFactory.getLogger(AppConfig.class);

  @Bean
  public MarketDataConfig marketDataConfig() {
    MarketDataConfig marketDataConfig = new MarketDataConfig();
    Dotenv dotenv = Dotenv.configure().load();
    marketDataConfig.setHost(dotenv.get("HOST"));
    marketDataConfig.setToken(dotenv.get("TOKEN"));
    System.out.println("HOST: "+ dotenv.get("HOST") +" TOKEN"+dotenv.get("TOKEN"));
    return marketDataConfig;
  }

  @Bean
  public HttpClientConnectionManager httpClientConnectionManager() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(50);
    cm.setDefaultMaxPerRoute(50);
    return cm;
  }

  @Bean
  public DataSource datasource(){
    Dotenv dotenv = Dotenv.configure().load();
    String jdbcUrl = "jdbc:postgresql://" +
            dotenv.get("PSQL_HOST") + ":" +
            dotenv.get("PSQL_PORT") + "/" +
            dotenv.get("PSQL_DB");

    String user = dotenv.get("PSQL_USER");
    String password = dotenv.get("PSQL_PASSWORD");
    System.out.println("JDBC URL: "+ jdbcUrl);

    BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setUrl(jdbcUrl);
    basicDataSource.setUsername(user);
    basicDataSource.setPassword(password);
    return basicDataSource;
  }
}
