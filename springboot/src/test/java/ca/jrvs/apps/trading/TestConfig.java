package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import io.github.cdimascio.dotenv.Dotenv;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"ca.jrvs.apps.trading.dao","ca.jrvs.apps.trading.service"})
public class TestConfig {

    @Bean
    public MarketDataConfig marketDataConfig(){
      MarketDataConfig marketDataConfig = new MarketDataConfig();
      Dotenv dotenv = Dotenv.configure().load();
      marketDataConfig.setHost(dotenv.get("HOST"));
      marketDataConfig.setToken(dotenv.get("TOKEN"));
      return marketDataConfig;
    }

  @Bean
  public DataSource datasource(){
    Dotenv dotenv = Dotenv.configure().load();
    System.out.println("Creating apacheDataSource");
    String jdbcUrl = "jdbc:postgresql://" +
        dotenv.get("PSQL_HOST") + ":" +
        dotenv.get("PSQL_PORT") + "/" +
        dotenv.get("PSQL_DB_TEST");

    String user = dotenv.get("PSQL_USER");
    String password = dotenv.get("PSQL_PASSWORD");

    BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setUrl(jdbcUrl);
    basicDataSource.setUsername(user);
    basicDataSource.setPassword(password);
    return basicDataSource;
  }

  @Bean
  public HttpClientConnectionManager httpClientConnectionManager() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(50);
    cm.setDefaultMaxPerRoute(50);
    return cm;
  }
}
