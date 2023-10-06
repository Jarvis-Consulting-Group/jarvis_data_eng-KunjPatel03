package ca.jrvs.apps.trading.model.domain;

public class Position implements Entity<Integer>{

  private int accountId;
  private String ticker;
  private int position;
  @Override
  public Integer getId() {
    return accountId;
  }

  @Override
  public void setId(Integer integer) {
    this.accountId = integer;
  }

  public int getAccountId() {
    return accountId;
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  public String getTicker() {
    return ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  @Override
  public String toString() {
    return "Position{" +
        "accountId=" + accountId +
        ", ticker='" + ticker + '\'' +
        ", position=" + position +
        '}';
  }
}
