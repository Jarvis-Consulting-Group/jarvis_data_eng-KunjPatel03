package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.model.dto.MarketOrderDto;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {
  private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

  private final AccountDao accountDao;
  private final SecurityOrderDao securityOrderDao;
  private final QuoteDao quoteDao;
  private final PositionDao positionDao;

  @Autowired
  public OrderService(AccountDao accountDao, SecurityOrderDao securityOrderDao, QuoteDao quoteDao, PositionDao positionDao){
    this.accountDao = accountDao;
    this.securityOrderDao = securityOrderDao;
    this.positionDao = positionDao;
    this.quoteDao = quoteDao;
  }

  /**
   * Execute a market order
   * - validate the order (e.g size, and ticker)
   * - create a securityOder (for security_order table)
   * - handle buy or sell order
   *  - buy oder: check account balance (calls helper method)
   *  - sell order: check position for the ticker/symbol (calls helper method)
   * - Save and return securityOrder
   *
   * @param orderDto market order
   * @return SecurityOrder from security_order table
   * @throws org.springframework.dao.DataAccessException if unable to get data from DAO
   * @throws IllegalArgumentException for invalid input
   */
  public SecurityOrder executeMarketOrder(MarketOrderDto orderDto){
    if (orderDto == null || orderDto.getSize() == null || orderDto.getSize() == 0) {
      throw new IllegalArgumentException("Invalid order size");
    }
    Quote quote = quoteDao.findById(orderDto.getTicker()).orElseThrow(() -> new IllegalArgumentException("Invalid ticker id"));
    SecurityOrder securityOrder = new SecurityOrder();
    securityOrder.setAccountId(orderDto.getAccountId());
    securityOrder.setTicker(orderDto.getTicker());
    securityOrder.setSize(orderDto.getSize());
    Account account = accountDao.findByTraderId(orderDto.getAccountId()).orElseThrow(() -> new IllegalArgumentException("Invalid TraderId"));
    if (orderDto.getSize() > 0) {
      securityOrder.setPrice(quote.getAskPrice());
      handleBuyMarketOrder(orderDto, securityOrder, account);
    }
    else {
      securityOrder.setPrice(quote.getBidPrice());
      handleSellMarketOrder(orderDto, securityOrder, account);
    }
    return securityOrderDao.save(securityOrder);
  }

  /**
   * Helper method that execute a buy order
   * @param marketOrderDto user order
   * @param securityOrder to be saved in data database
   * @param account account
   */
  protected void handleBuyMarketOrder(MarketOrderDto marketOrderDto, SecurityOrder securityOrder, Account account){
    double orderAmount = marketOrderDto.getSize() * securityOrder.getPrice();
    if (account.getAmount() >= orderAmount) {
      double updateAmount = account.getAmount() - orderAmount;
      account.setAmount(updateAmount);
      accountDao.save(account);
      securityOrder.setStatus("FILLED");
    }
    else {
      securityOrder.setStatus("CANCELED");
      securityOrder.setNotes("Insufficient fund. Order amount: " + orderAmount);
    }

  }
  /**
   * Helper method that execute a sell order
   * @param marketOrderDto user order
   * @param securityOrder to be saved in data database
   * @param account account
   */
  protected void handleSellMarketOrder(MarketOrderDto marketOrderDto, SecurityOrder securityOrder, Account account){
    Long position = positionDao.findByIdAndTicker(marketOrderDto.getAccountId(), marketOrderDto.getTicker());
    OrderService.logger.debug("AccountId:" + marketOrderDto.getAccountId() + " has position:" + position);
    if (position + marketOrderDto.getSize() >= 0L) {
      double sellAmount = -securityOrder.getSize() * securityOrder.getPrice();
      double updateAmount = account.getAmount() + sellAmount;
      securityOrder.setStatus("FILLED");
      account.setAmount(updateAmount);
      accountDao.save(account);
    }
    else {
      securityOrder.setStatus("CANCELED");
      securityOrder.setNotes("Insufficient position.");
    }
  }
}
