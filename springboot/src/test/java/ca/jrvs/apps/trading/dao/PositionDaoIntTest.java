package ca.jrvs.apps.trading.dao;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.service.QuoteService;
import java.sql.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class PositionDaoIntTest {


  @Autowired
  private PositionDao positionDao;

  @Autowired
  private SecurityOrderDao securityOrderDao;

  @Autowired
  private AccountDao accountDao;

  private int accountId;

  @Autowired
  private TraderDao traderDao;

  @Autowired
  private QuoteService quoteService;

  @Before
  public void init() {
    quoteService.saveQuote("AAPL");
    quoteService.saveQuote("FB");

    Trader savedTrader = new Trader();
    savedTrader.setFirstName("FirstNameTest");
    savedTrader.setLastName("LastNameTest");
    savedTrader.setCountry("Canada");
    savedTrader.setEmail("firstlast@gmail.com");
    savedTrader.setDob(new Date(1234, 12, 12));
    traderDao.save(savedTrader);

    Account savedAccount = new Account();
    savedAccount.setTraderId(savedTrader.getId());
    savedAccount.setAmount(2000.00);
    accountDao.save(savedAccount);
    accountId = savedAccount.getId();

    SecurityOrder savedOrder = new SecurityOrder();
    savedOrder.setAccountId(accountId);
    savedOrder.setTicker("AAPL");
    savedOrder.setSize(4);
    savedOrder.setStatus("FILLED");

    securityOrderDao.save(savedOrder);

    SecurityOrder newOrder = new SecurityOrder();
    newOrder.setAccountId(accountId);
    newOrder.setTicker("AAPL");
    newOrder.setSize(2);
    newOrder.setStatus("FILLED");

    securityOrderDao.save(newOrder);

    SecurityOrder invalidOrder = new SecurityOrder();
    invalidOrder.setAccountId(accountId);
    invalidOrder.setTicker("AAPL");
    invalidOrder.setSize(1);
    invalidOrder.setStatus("CANCELLED");

    securityOrderDao.save(invalidOrder);
  }

  @After
  public void delete() {
    securityOrderDao.deleteAll();
    accountDao.deleteAll();
    traderDao.deleteAll();
  }

  @Test
  public void existsById() {
    assertTrue(positionDao.existsById(accountId));
    assertFalse(positionDao.existsById(accountId+1));
  }

  @Test
  public void findByAccountIdAndTicker() {
    Long position = positionDao.findByIdAndTicker(accountId, "AAPL");
    assertEquals(Optional.ofNullable(position), 4);

    try {
      positionDao.findByIdAndTicker(accountId, "FB");
    } catch (IncorrectResultSizeDataAccessException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void findAllByAccountId() {
    SecurityOrder newOrder = new SecurityOrder();
    newOrder.setAccountId(accountId);
    newOrder.setTicker("FB");
    newOrder.setSize(7);
    newOrder.setStatus("FILLED");

    securityOrderDao.save(newOrder);

    List<Position> positions = positionDao.findAllByAccountId(accountId);
    for (Position position : positions) {
      assertEquals(position.getAccountId(), accountId);
      if (position.getTicker().equals("AAPL")) {
        assertEquals(position.getPosition(), 4);
      } else if (position.getTicker().equals("FB")) {
        assertEquals(position.getPosition(), 7);
      } else {
        fail();
      }
    }

    assertEquals(positionDao.count(), 2);
  }

}
