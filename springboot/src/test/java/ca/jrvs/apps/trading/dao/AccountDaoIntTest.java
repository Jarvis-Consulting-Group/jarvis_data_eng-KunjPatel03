package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Trader;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import java.sql.Date;
import java.util.Optional;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class AccountDaoIntTest {

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private TraderDao traderDao;

  private Account savedAccount;
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

    savedAccount = new Account();
    savedAccount.setTraderId(savedTrader.getId());
    savedAccount.setAmount(1000.00);
    accountDao.save(savedAccount);
  }

  @After
  public void deleteOne() {
    accountDao.deleteAll();
    traderDao.deleteAll();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void delete() {
    accountDao.delete(savedAccount);
    assertFalse(accountDao.findById(savedAccount.getId()).isPresent());
  }

  @Test
  public void deleteAll() {
    accountDao.deleteAll();
    assertFalse(accountDao.findById(savedAccount.getId()).isPresent());
  }

  @Test
  public void findByTraderId() {
    Optional<Account> retrievedAccount = accountDao.findByTraderId(savedTrader.getId());
    assertTrue(retrievedAccount.isPresent());
    assertEquals(savedAccount.getId(), retrievedAccount.get().getId());
    assertEquals(savedAccount.getTraderId(), retrievedAccount.get().getTraderId());
    assertEquals(savedAccount.getAmount(), retrievedAccount.get().getAmount(), 0.001);
  }

  @Test
  public void updateOne() {
    double updatedAmount = 1500.00;
    savedAccount.setAmount(updatedAmount);
    int rowsUpdated = accountDao.updateOne(savedAccount);
    assertEquals(1, rowsUpdated);

    Optional<Account> retrievedAccount = accountDao.findById(savedAccount.getId());
    assertTrue(retrievedAccount.isPresent());
    assertEquals(updatedAmount, retrievedAccount.get().getAmount(), 0.001);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void deleteAllIterable() {
    List<Account> accountsToDelete = new ArrayList<>();
    accountsToDelete.add(savedAccount);

    accountDao.deleteAll(accountsToDelete);
    assertFalse(accountDao.findById(savedAccount.getId()).isPresent());
  }
}
