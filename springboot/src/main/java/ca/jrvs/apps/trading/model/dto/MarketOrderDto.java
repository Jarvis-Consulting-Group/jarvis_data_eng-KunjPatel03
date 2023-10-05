package ca.jrvs.apps.trading.model.dto;

public class MarketOrderDto {
  private Integer accountId;
  private String ticker;
  private Integer size;

  public int getAccountId() {
    return accountId;
  }

  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
  }

  public String getTicker() {
    return ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  @Override
  public String toString() {
    return "MarketOrderDto{" +
        "accountId=" + accountId +
        ", ticker='" + ticker + '\'' +
        ", size=" + size +
        '}';
  }
}
