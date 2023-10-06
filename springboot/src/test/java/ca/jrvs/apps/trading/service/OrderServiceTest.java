package ca.jrvs.apps.trading.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.model.dto.MarketOrderDto;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {
  @Captor
  ArgumentCaptor<SecurityOrder> captorSecurityOrder;
  @Mock
  private AccountDao accountDao;
  @Mock
  private SecurityOrderDao securityOrderDao;
  @Mock
  private QuoteDao quoteDao;
  @Mock
  private PositionDao positionDao;

  @InjectMocks
  private OrderService orderService;

  @Test
  public void testExecuteMarketOrderBuy() {
    MarketOrderDto orderDto = new MarketOrderDto();
    orderDto.setAccountId(1);
    orderDto.setTicker("AAPL");
    orderDto.setSize(10);

    Quote quote = new Quote();
    quote.setAskPrice(150.0);

    Account account = new Account();
    account.setAmount(500.0); // Updated account balance

    when(quoteDao.findById(eq("AAPL"))).thenReturn(java.util.Optional.of(quote));
    when(accountDao.findByTraderId(eq(1))).thenReturn(java.util.Optional.of(account));

    SecurityOrder securityOrder = orderService.executeMarketOrder(orderDto);

    verify(securityOrderDao).save(captorSecurityOrder.capture());
    SecurityOrder capturedSecurityOrder = captorSecurityOrder.getValue();

    assertEquals("CANCELED", capturedSecurityOrder.getStatus());
    assertEquals(500.0, account.getAmount(), 0.01);
  }

  @Test
  public void testExecuteMarketOrderSell() {
    MarketOrderDto orderDto = new MarketOrderDto();
    orderDto.setAccountId(1);
    orderDto.setTicker("AAPL");
    orderDto.setSize(-5);

    Quote quote = new Quote();
    quote.setBidPrice(150.0);

    Account account = new Account();
    account.setAmount(1000.0);

    when(quoteDao.findById(eq("AAPL"))).thenReturn(java.util.Optional.of(quote));
    when(accountDao.findByTraderId(eq(1))).thenReturn(java.util.Optional.of(account));
    when(positionDao.findByIdAndTicker(eq(1), eq("AAPL"))).thenReturn(10L);

    SecurityOrder securityOrder = orderService.executeMarketOrder(orderDto);

    verify(securityOrderDao).save(captorSecurityOrder.capture());
    SecurityOrder capturedSecurityOrder = captorSecurityOrder.getValue();

    assertEquals("FILLED", capturedSecurityOrder.getStatus());
    assertNull(capturedSecurityOrder.getNotes());

    assertEquals(1750.0, account.getAmount(), 0.01);
  }
}
