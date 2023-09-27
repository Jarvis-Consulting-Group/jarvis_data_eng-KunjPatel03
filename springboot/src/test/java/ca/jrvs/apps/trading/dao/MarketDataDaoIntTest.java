package ca.jrvs.apps.trading.dao;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.config.MarketDataConfig;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Arrays;
import java.util.List;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;

public class MarketDataDaoIntTest {

    private MarketDataDao dao;

    @Before
    public void init(){
      PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
      cm.setMaxTotal(50);
      cm.setDefaultMaxPerRoute(50);
      MarketDataConfig marketDataConfig = new MarketDataConfig();
      Dotenv dotenv = Dotenv.configure().load();
      marketDataConfig.setHost(dotenv.get("HOST"));
      marketDataConfig.setToken(dotenv.get("TOKEN"));
      dao = new MarketDataDao(cm, marketDataConfig);
    }

    @Test
    public void findIexQuotesbyTickers() {
      //Happy path
      List<IexQuote> quoteList = dao.findAllById(Arrays.asList("AAPL","FB"));
      assertEquals(2,quoteList.size());
      assertEquals("AAPL", quoteList.get(0).getSymbol());

      // Sad path
      try{
        dao.findAllById((Arrays.asList("AAPL","FB2")));
        fail();
      }catch (IllegalArgumentException e){
        assertTrue(true);
      }catch (Exception e){
        fail();
      }
    }

    @Test
    public void findByTicker(){
      String ticker = "AAPL";
      IexQuote iexQuote = dao.findById(ticker).get();
      assertEquals(ticker, iexQuote.getSymbol());
    }
}