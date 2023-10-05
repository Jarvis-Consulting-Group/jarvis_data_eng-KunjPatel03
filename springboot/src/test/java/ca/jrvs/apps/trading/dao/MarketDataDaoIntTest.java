package ca.jrvs.apps.trading.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataRetrievalFailureException;

public class MarketDataDaoIntTest {

  private MarketDataDao dao;

  @Before
  public void init() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(50);
    cm.setDefaultMaxPerRoute(50);
    MarketDataConfig marketDataConfig = new MarketDataConfig();
    Dotenv dotenv = Dotenv.configure().load();
    marketDataConfig.setHost(dotenv.get("HOST"));
    marketDataConfig.setToken(dotenv.get("TOKEN"));
    dao = new MarketDataDao(cm, marketDataConfig);
  }

  @Test
  public void findIexQuotesbyTickers() {
    List<IexQuote> quoteList = dao.findAllById(Arrays.asList("AAPL", "FB"));
    assertEquals(2, quoteList.size());
    assertEquals("AAPL", quoteList.get(0).getSymbol());
  }

  @Test
  public void findByTicker() {
    String ticker = "AAPL";
    Optional<IexQuote> iexQuoteOptional = dao.findById(ticker);
    assertTrue(iexQuoteOptional.isPresent());
    assertEquals(ticker, iexQuoteOptional.get().getSymbol());
  }

  @Test
  public void findByInvalidTicker() {
    String ticker = "INVALID";
    Optional<IexQuote> iexQuoteOptional = dao.findById(ticker);
    assertFalse(iexQuoteOptional.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void findAllByIdWithEmptyTickers() {
    List<IexQuote> quoteList = dao.findAllById(Arrays.asList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void findAllByIdWithInvalidTicker() {
    List<IexQuote> quoteList = dao.findAllById(Arrays.asList("AAPL", "INVALID"));
  }

  @Test(expected = DataRetrievalFailureException.class)
  public void findAllByIdWithInvalidHost() {
    // Assuming an invalid host to simulate an HTTP request failure
    MarketDataConfig marketDataConfig = new MarketDataConfig();
    marketDataConfig.setHost("invalidhost");
    marketDataConfig.setToken("invalidtoken");
    MarketDataDao invalidHostDao = new MarketDataDao(null, marketDataConfig);
    invalidHostDao.findAllById(Arrays.asList("AAPL"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedMethods() {
    dao.existsById("AAPL");
    dao.findAll();
    dao.count();
    dao.deleteById("AAPL");
    dao.delete(new IexQuote());
    dao.deleteAll(Arrays.asList(new IexQuote()));
    dao.deleteAll();
    dao.save(new IexQuote());
    dao.saveAll(Arrays.asList(new IexQuote()));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedFindAll() {
    dao.findAll();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedCount() {
    dao.count();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedDeleteById() {
    dao.deleteById("AAPL");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedDelete() {
    dao.delete(new IexQuote());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedDeleteAllById() {
    dao.deleteAll(Collections.singletonList(new IexQuote()));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedDeleteAll() {
    dao.deleteAll();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedSave() {
    dao.save(new IexQuote());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unsupportedSaveAll() {
    List<IexQuote> quotes = new ArrayList<>();
    quotes.add(new IexQuote());
    dao.saveAll(quotes);
  }

}
