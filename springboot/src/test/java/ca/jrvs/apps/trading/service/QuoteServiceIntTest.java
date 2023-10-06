package ca.jrvs.apps.trading.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteServiceIntTest {
  @Autowired
  private QuoteService quoteService;

  @Autowired
  private QuoteDao quoteDao;

  @Before
  public void setUp(){
    quoteDao.deleteAll();
  }

  @Test
  public void findIexQuoteByTicker(){
    String ticker = "AAPL";
    IexQuote iexQuote = quoteService.findIexQuoteByTicker(ticker);
    assertNotNull(iexQuote);
    assertEquals(ticker, iexQuote.getSymbol());
  }

  @Test
  public void updateMarketData(){
    List<String> tickers = Arrays.asList("AAPL", "MSFT", "GOOGL");
    quoteService.updateMarketData(tickers);

    for (String ticker : tickers) {
      IexQuote updatedQuote = quoteService.findIexQuoteByTicker(ticker);
      assertNotNull(updatedQuote);
      assertNotNull(updatedQuote.getSymbol());
    }
  }

  @Test
  public void saveQuote(){
    List<String> tickers = new ArrayList<>();
    tickers.add("AAPL");
    tickers.add("GOOGL");

    List<Quote> savedQuotes = quoteService.saveQuotes(tickers);

    assertNotNull(savedQuotes);
    assertEquals(tickers.size(), savedQuotes.size());
  }

  @Test
  public void findAllQuotes(){
    List<Quote> quotes = quoteService.findAllQuotes();
    assertNotNull(quotes);
    assertTrue(quotes.isEmpty());
  }
}
