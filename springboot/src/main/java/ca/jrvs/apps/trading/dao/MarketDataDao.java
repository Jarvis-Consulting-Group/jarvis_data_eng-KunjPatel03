package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.config.MarketDataConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class MarketDataDao implements CrudRepository<IexQuote, String> {

  private static final String IEX_BATCH_PATH = "/stock/market/batch?symbols=%s&types=quote&token=";
  private final String IEX_BATCH_URL;

  private final Logger logger = LoggerFactory.getLogger(MarketDataDao.class);
  private final HttpClientConnectionManager httpClientConnectionManager;

  @Autowired
  public MarketDataDao(HttpClientConnectionManager httpClientConnectionManager, MarketDataConfig marketDataConfig) {
    this.httpClientConnectionManager = httpClientConnectionManager;
    IEX_BATCH_URL = marketDataConfig.getHost() + IEX_BATCH_PATH + marketDataConfig.getToken();
    System.out.println(IEX_BATCH_URL);

  }
  /**
   * Execute a get and return http entity/body as a string
   * @param ticker resource URL
   * @return http response body or Optional empty for 404 response
   * @throws IllegalArgumentException if a given ticker is invalid
   * @throws DataRetrievalFailureException if HTTP failed or status code is unexpected
   */
  @NotNull
  @Override
  public Optional<IexQuote> findById(@NotNull String ticker) {
    List<IexQuote> quotes = findAllById(Collections.singletonList(ticker));
    return quotes.isEmpty() ? Optional.empty() : Optional.of(quotes.get(0));
  }

  /**
   * Get quotes from IEX
   *
   * @param tickers is a list of tickers
   * @return a list of IexQuote objects
   * @throws IllegalArgumentException      if any ticker is invalid or tickers is empty
   * @throws DataRetrievalFailureException if HTTP request failed
   */
  @NotNull
  @Override
  public List<IexQuote> findAllById(Iterable<String> tickers) {
    List<String> tickerList = new ArrayList<>();
    for (String ticker : tickers) {
      tickerList.add(ticker);
    }

    if (tickerList.isEmpty()) {
      throw new IllegalArgumentException("Tickers is empty");
    }

    Optional<String> body = executeHttpGet(String.format(IEX_BATCH_URL, String.join(",", tickerList)));
    List<IexQuote> quotes = new ArrayList<>();

    if (body.isPresent()) {
      String bodyText = body.get();
      JSONObject allQuotes = new JSONObject(bodyText);
      ObjectMapper objectMapper = new ObjectMapper(); // Create ObjectMapper

      for (String ticker : tickerList) {
        if (!allQuotes.has(ticker)) {
          throw new IllegalArgumentException("Invalid ticker: " + ticker);
        }
        JSONObject quoteJson = allQuotes.getJSONObject(ticker).getJSONObject("quote");
        IexQuote quote; // Deserialize JSON to IexQuote
        try {
          quote = objectMapper.readValue(quoteJson.toString(), IexQuote.class);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        quotes.add(quote);
      }
    }
    return quotes;
  }

  private CloseableHttpClient getHttpClient() {
    return HttpClients.custom()
        .setConnectionManager(httpClientConnectionManager)
        //prevent connectionManager shutdown when calling httpClient.close()
        .setConnectionManagerShared(true)
        .build();
  }

  /**
   * Execute an HTTP GET request and return the response body as a string.
   *
   * @param url the URL to make the GET request to
   * @return the response body as a string wrapped in an Optional
   * @throws DataRetrievalFailureException if the HTTP request fails
   */
  private Optional<String> executeHttpGet(String url) {
    try {
      HttpGet httpGet = new HttpGet(url);
      CloseableHttpClient httpClient = getHttpClient();
      HttpResponse response = httpClient.execute(httpGet);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 200) {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          String responseBody = EntityUtils.toString(entity);
          return Optional.of(responseBody);
        }
      } else if (statusCode == 404) {
        // Handle 404 Not Found response (optional)
        return Optional.empty();
      } else {
        throw new DataRetrievalFailureException("HTTP request failed with status code: " + statusCode);
      }
    } catch (IOException e) {
      throw new DataRetrievalFailureException("HTTP request failed", e);
    }

    return Optional.empty();
  }

  @Override
  public boolean existsById(String s) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Iterable<IexQuote> findAll() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public long count() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteById(String s) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void delete(IexQuote iexQuote) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteAll(Iterable<? extends IexQuote> iterable) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteAll() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public <S extends IexQuote> S save(S s) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public <S extends IexQuote> Iterable<S> saveAll(Iterable<S> iterable) {
    throw new UnsupportedOperationException("Not implemented");
  }


}
