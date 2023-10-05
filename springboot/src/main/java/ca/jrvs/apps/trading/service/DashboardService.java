package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.view.PortfolioView;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DashboardService {

  private TraderDao traderDao;
  private PositionDao positionDao;
  private AccountDao accountDao;
  private QuoteDao quoteDao;

  @Autowired
  public DashboardService(TraderDao traderDao, PositionDao positionDao, AccountDao accountDao, QuoteDao quoteDao){
    this.traderDao = traderDao;
    this.positionDao = positionDao;
    this.accountDao = accountDao;
    this.quoteDao = quoteDao;
  }

  /**
   * Create and return a traderAccountView by trader ID
   * - get trader account by id
   * - get trader info by id
   * - create and return a traderAccountView
   *
   * @param traderId must not be null
   * @return traderAccountView
   * @throws IllegalArgumentException if traderId is null or not found
   */
  public TraderAccountView getTraderAccount(Integer traderId){
    if (traderId != null) {
      throw new IllegalArgumentException("ID cannot be null");
    }
    TraderAccountView view = new TraderAccountView();
    view.setTrader(traderDao.findById(traderId)
        .orElseThrow(() -> new IllegalArgumentException("Trader cannot be found")));
    view.setAccount(findAccountByTraderId(traderId));
    return view;
  }

  /**
   * Create and return portfolioView by trader ID
   * - get account by trader id
   * - get position by account id
   * - create and return a portfolioView
   *
   * @param traderId must not be null
   * @retrun portfolioView
   * @throws IllegalArgumentException if traderId is null or not found
   */
  public PortfolioView getProfileViewByTraderId(Integer traderId){
    if (traderId == null) {
      throw new IllegalArgumentException("TraderId cannot be null");
    }

    PortfolioView view = new PortfolioView();
    view.setTraderId(traderId);

    Account account = findAccountByTraderId(traderId);

    List<Position> positions = positionDao.findAllByAccountId(account.getId());
    for (Position position : positions) {
      Quote quote = quoteDao.findById(position.getTicker())
          .orElseThrow(() -> new DataRetrievalFailureException("Quote for " +
              position.getTicker() + " not found."));
      view.addPosition(account, position, quote);
    }

    return view;
  }

  /**
   * @throws IllegalArgumentException if traderId is not found
   */
  private Account findAccountByTraderId(Integer traderId){
    return accountDao.findByTraderId(traderId)
        .orElseThrow(()-> new IllegalArgumentException("Invalid traderId"));
  }
}
