package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.model.domain.config.MarketDataConfig;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  private Logger logger = LoggerFactory.getLogger(AppConfig.class);

  @Bean
  public MarketDataConfig marketDataConfig() {
    MarketDataConfig marketDataConfig = new MarketDataConfig();
    Dotenv dotenv = Dotenv.configure().load();
    marketDataConfig.setHost(dotenv.get("HOST"));
    marketDataConfig.setToken(dotenv.get("TOKEN"));
    return marketDataConfig;
  }

  @Bean
  public HttpClientConnectionManager httpClientConnectionManager() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(50);
    cm.setDefaultMaxPerRoute(50);
    return cm;
  }
}
