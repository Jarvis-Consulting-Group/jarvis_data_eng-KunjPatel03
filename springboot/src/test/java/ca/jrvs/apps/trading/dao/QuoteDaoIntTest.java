package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Optional;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteDaoIntTest {

  @Autowired
  private QuoteDao quoteDao;

  private Quote savedQuote;

  @Before
  public void insertOne() {
    savedQuote = new Quote();
    savedQuote.setTicker("AAPL");
    savedQuote.setAskPrice(10.0);
    savedQuote.setAskSize(10);
    savedQuote.setBidPrice(10.0);
    savedQuote.setBidSize(10);
    savedQuote.setLastPrice(10.1d);
    quoteDao.save(savedQuote);
  }

  @After
  public void deleteOne() {
    quoteDao.deleteById(savedQuote.getTicker());
  }

  @Test
  public void save() {
    Quote newQuote = new Quote();
    newQuote.setTicker("GOOG");
    newQuote.setAskPrice(1234.0);
    newQuote.setAskSize(11);
    newQuote.setBidPrice(939.0);
    newQuote.setBidSize(13);
    newQuote.setLastPrice(1000.1d);

    quoteDao.save(newQuote);

    // Check if the quote is saved and can be retrieved
    Optional<Quote> retrievedQuote = quoteDao.findById("GOOG");
    assertTrue(retrievedQuote.isPresent());
    assertEquals(newQuote, retrievedQuote.get());
  }

  @Test
  public void findById() {
    Optional<Quote> retrievedQuote = quoteDao.findById("AAPL");
    assertTrue(retrievedQuote.isPresent());
    assertEquals(savedQuote, retrievedQuote.get());
  }

  @Test
  public void findByIdNotFound() {
    Optional<Quote> retrievedQuote = quoteDao.findById("INVALID");
    assertFalse(retrievedQuote.isPresent());
  }

  @Test
  public void existsById() {
    assertTrue(quoteDao.existsById("AAPL"));
  }

  @Test
  public void existsByIdNotFound() {
    assertFalse(quoteDao.existsById("INVALID"));
  }

  @Test
  public void findAll() {
    Iterable<Quote> quotes = quoteDao.findAll();
    assertNotNull(quotes);
    assertTrue(quotes.iterator().hasNext());
  }

  @Test
  public void deleteById() {
    quoteDao.deleteById("AAPL");
    Optional<Quote> retrievedQuote = quoteDao.findById("AAPL");
    assertFalse(retrievedQuote.isPresent());
  }

  @Test
  public void count() {
    long count = quoteDao.count();
    assertEquals(1L, count); // Assuming only one record is inserted in the schema.sql
  }

  @Test
  public void saveAll() {
    Quote newQuote1 = new Quote();
    newQuote1.setTicker("MSFT");
    newQuote1.setAskPrice(200.0);
    newQuote1.setAskSize(20);
    newQuote1.setBidPrice(199.0);
    newQuote1.setBidSize(20);
    newQuote1.setLastPrice(200.1d);

    Quote newQuote2 = new Quote();
    newQuote2.setTicker("AAPL");
    newQuote2.setAskPrice(300.0);
    newQuote2.setAskSize(30);
    newQuote2.setBidPrice(299.0);
    newQuote2.setBidSize(30);
    newQuote2.setLastPrice(300.1d);

    Iterable<Quote> savedQuotes = quoteDao.saveAll(List.of(newQuote1, newQuote2));

    // Check if the quotes are saved and can be retrieved
    Optional<Quote> retrievedQuote1 = quoteDao.findById("MSFT");
    Optional<Quote> retrievedQuote2 = quoteDao.findById("AAPL");
    assertTrue(retrievedQuote1.isPresent());
    assertTrue(retrievedQuote2.isPresent());
    assertEquals(newQuote1, retrievedQuote1.get());
    assertEquals(newQuote2, retrievedQuote2.get());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void delete() {
    Quote quote = new Quote();
    quote.setTicker("AAPL");
    quoteDao.delete(quote);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void deleteAllIterable() {
    Iterable<Quote> quotes = Collections.emptyList();
    quoteDao.deleteAll(quotes);
  }

  @Test
  public void deleteAll() {
    // Insert some quotes into the table
    Quote quote1 = new Quote();
    quote1.setTicker("AAPL");
    quote1.setAskPrice(10.0);
    quote1.setAskSize(10);
    quote1.setBidPrice(10.0);
    quote1.setBidSize(10);
    quote1.setLastPrice(10.1d);
    quoteDao.save(quote1);

    Quote quote2 = new Quote();
    quote2.setTicker("GOOG");
    quote2.setAskPrice(1000.0);
    quote2.setAskSize(10);
    quote2.setBidPrice(999.0);
    quote2.setBidSize(10);
    quote2.setLastPrice(1000.1d);
    quoteDao.save(quote2);

    // Delete all quotes
    quoteDao.deleteAll();

    // Check if the table is empty
    Iterable<Quote> quotes = quoteDao.findAll();
    assertFalse(quotes.iterator().hasNext());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void findAllById() {
    Iterable<String> ids = Collections.emptyList();
    quoteDao.findAllById(ids);
  }

}
