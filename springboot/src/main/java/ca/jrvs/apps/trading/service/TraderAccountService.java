package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraderAccountService {

  private final TraderDao traderDao;
  private final AccountDao accountDao;
  private final PositionDao  positionDao;
  private final SecurityOrderDao securityOrderDao;

  @Autowired
  public TraderAccountService(TraderDao traderDao, AccountDao accountDao, PositionDao positionDao, SecurityOrderDao securityOrderDao){
    this.traderDao = traderDao;
    this.accountDao = accountDao;
    this.positionDao = positionDao;
    this.securityOrderDao = securityOrderDao;
  }

  /**
   * Create a new trader and initialize a new account with 0 amount.
   * -validate user input (all fields must be nonempty)
   * -create a trader
   * -create an account
   * -create, setup, and return new traderAccountView
   *
   * Assumption: to simplify the logic, each trader has only one account where traderId == accountId
   *
   * @param trader cannot be null. All fields cannot be null except for id
   * @return traderAccountView
   * @throws IllegalArgumentException if a trader has null fields or id is not null.
   */
  public TraderAccountView createTraderAndAccount(Trader trader) {
    if (trader.getId() != null) {
      throw new IllegalArgumentException("ID is not allowed as it's auto-gen");
    }
    TraderAccountView view = new TraderAccountView();
    if (trader.getFirstName() == null || trader.getLastName() == null ||
        trader.getCountry() == null || trader.getEmail() == null || trader.getDob() == null) {
      throw new IllegalArgumentException("Trader property cannot be null or empty");
    }
    view.setTrader(this.traderDao.save(trader));
    Account newAccount = new Account();
    newAccount.setAmount(0.0);
    newAccount.setTraderId(view.getTrader().getId());
    view.setAccount(this.accountDao.save(newAccount));
    return view;
  }

  /**
   * A trader can be deleted iff it has no open position and 0 cash balance
   * -validate traderId
   * -get trader account by traderId and check account balance
   * -get positions by accountId and check positions
   * -delete all securityOrders, account, trader (in this order)
   *
   * @param traderId must not be null
   * @throws IllegalArgumentException if traderId is null or not found or unable to delete
   */
  public void deleteTraderById(Integer traderId) {
    if (traderId == null) {
      throw new IllegalArgumentException("ID is not allowed as it's auto-gen");
    }
    Account account = accountDao.findByTraderId(traderId).get();
    if (account.getAmount() != 0.0) {
      throw new IllegalArgumentException("Can't delete Trader due to non-zero account amount");
    }
    List<Position> positions = positionDao.findByAccountId(account.getId());
    positions.forEach(position -> {
      if (position.getPosition() != 0) {
        throw new IllegalArgumentException("Can't delete Trader due to open position");
      }
      else {
        return;
      }
    });
    securityOrderDao.deleteById(account.getId());
    accountDao.deleteById(account.getId());
    traderDao.deleteById(traderId);
  }

  /**
   * Deposit a fund to an account by traderId
   * -validate user input
   * -account = accountDao.findByTraderId
   * -accountDao.updateAmountById
   *
   * @param traderId must not be null
   * @param fund must be greater than 0
   * @return updated account
   * @throws IllegalArgumentException if traderId is null or not found, or fund is <= 0
   */
  public Account deposit(Integer traderId, Double fund) {
    if (traderId == null || fund <= 0.0) {
      throw new IllegalArgumentException("Invalid traderId/fund");
    }
    Account account = accountDao.findByTraderId(traderId).get();
    Double newAmount = account.getAmount() + fund;
    account.setAmount(newAmount);
    return accountDao.save(account);
  }

  /**
   * Withdraw a fund to an account by traderId
   * -validate user input
   * -account = accountDao.findByTraderId
   * -accountDao.updateAmountById
   */
  public Account withdraw (Integer traderId, Double fund) {
    if (traderId == null || fund <= 0.0) {
      throw new IllegalArgumentException("Invalid traderId/fund");
    }
    Account account = accountDao.findByTraderId(traderId).get();
    Double newAmount = account.getAmount() - fund;
    if (newAmount < 0.0) {
      throw new IllegalArgumentException("Insufficient account. Current account amount is:" + account.getAmount());
    }
    account.setAmount(newAmount);
    return accountDao.save(account);
  }
}
