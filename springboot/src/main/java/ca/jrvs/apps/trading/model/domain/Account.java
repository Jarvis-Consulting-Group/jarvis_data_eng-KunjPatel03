package ca.jrvs.apps.trading.model.domain;

public class Account  implements Entity<Integer>{
  private Integer id;
  private int traderId;
  private double amount;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public int getTraderId() {
    return traderId;
  }

  public void setTraderId(int traderId) {
    this.traderId = traderId;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "Account{" +
        "id=" + id +
        ", trader_id=" + traderId +
        ", amount=" + amount +
        '}';
  }
}
