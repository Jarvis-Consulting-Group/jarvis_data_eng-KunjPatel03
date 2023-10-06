package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Trader;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.sql.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class TraderDaoIntTest {

  @Autowired
  private TraderDao traderDao;

  private Trader savedTrader;

  @Before
  public void insertOne() {
    savedTrader = new Trader();
    savedTrader.setFirstName("FirstNameTest");
    savedTrader.setLastName("LastNameTest");
    savedTrader.setCountry("Canada");
    savedTrader.setEmail("firstlast@gmail.com");
    savedTrader.setDob(new Date(1234, 12, 12));
    traderDao.save(savedTrader);
  }

  @After
  public void deleteOne() {
    traderDao.deleteAll();
  }

  @Test
  public void findAllById() {
    List<Trader> traders = Lists.newArrayList(traderDao.findAllById(Arrays.asList(savedTrader.getId(), -1)));

    assertEquals(1, traders.size());
    assertEquals(savedTrader.getCountry(), traders.get(0).getCountry());
  }

  @Test
  public void deleteAll() {
    traderDao.deleteAll();
    List<Trader> traders = Lists.newArrayList(traderDao.findAll());

    assertEquals(0, traders.size());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void updateOneUnsupported() {
    Trader trader = new Trader();
    trader.setId(savedTrader.getId());
    trader.setFirstName("UpdatedFirstName");
    trader.setLastName("UpdatedLastName");
    trader.setCountry("USA");
    trader.setEmail("updatedemail@gmail.com");
    trader.setDob(new Date(5678, 5, 5));

    traderDao.updateOne(trader);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void deleteUnsupported() {
    Trader trader = new Trader();
    trader.setId(savedTrader.getId());

    traderDao.delete(trader);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void deleteAllUnsupported() {
    List<Trader> traders = Arrays.asList(savedTrader);

    traderDao.deleteAll(traders);
  }

}
