package ca.jrvs.apps.trading.model.view;

import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.HashMap;

public class PortfolioView {
  private Integer traderId;

  private HashMap<String, Object[]> portfolio;

  public Integer getTraderId() {
    return traderId;
  }

  public void setTraderId(Integer traderId) {
    this.traderId = traderId;
  }

  public HashMap<String, Object[]> getPortfolio() {
    return portfolio;
  }

  public void setPortfolio(HashMap<String, Object[]> portfolio) {
    this.portfolio = portfolio;
  }

  public void addAccount(Account account) {
    if (portfolio == null) {
      portfolio = new HashMap<>();
    }
    portfolio.put(account.toString(), new Object[2]);
  }

  public void addPosition(Account account, Position position, Quote quote) {
    portfolio.get(account.toString())[0] = position;
    portfolio.get(account.toString())[1] = quote;
  }

}
