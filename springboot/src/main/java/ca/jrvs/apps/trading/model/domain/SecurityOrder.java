package ca.jrvs.apps.trading.model.domain;

public class SecurityOrder implements Entity<Integer>{

  private Integer id;
  private int accountId;
  private String status;
  private String ticker;
  private int size;
  private Double price;
  private String notes;

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public void setId(Integer id) {
    this.id = id;
  }

  public int getAccountId() {
    return accountId;
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTicker() {
    return ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  @Override
  public String toString() {
    return "SecurityOrder{" +
        "id=" + id +
        ", account_id=" + accountId +
        ", status='" + status + '\'' +
        ", ticker='" + ticker + '\'' +
        ", size=" + size +
        ", price=" + price +
        ", notes='" + notes + '\'' +
        '}';
  }
}
