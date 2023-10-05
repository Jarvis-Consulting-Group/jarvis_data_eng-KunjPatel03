package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class QuoteService {
  private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

  private QuoteDao quoteDao;
  private MarketDataDao marketDataDao;

  @Autowired
  public QuoteService(QuoteDao quoteDao, MarketDataDao marketDataDao){
    this.quoteDao = quoteDao;
    this.marketDataDao = marketDataDao;
  }

  /**
   * Find IexQuote
   *
   * @param ticker id
   * @return IexQuote object
   * @throws IllegalArgumentException if ticker is invalid
   */
  public IexQuote findIexQuoteByTicker(String ticker){
    return marketDataDao.findById(ticker)
        .orElseThrow(()-> new IllegalArgumentException(ticker + "is invalid"));
  }

  public void updateMarketData(){
    List<Quote> quotes = findAllQuotes();
    List<String> tickers = quotes.stream().map(Quote::getTicker).collect(Collectors.toList());
    List<IexQuote> updatedIexQuotes = marketDataDao.findAllById(tickers);

    // Update the quotes with new market data
    for (Quote quote : quotes) {
      Optional<IexQuote> matchingIexQuote = updatedIexQuotes.stream()
          .filter(iexQuote -> iexQuote.getSymbol().equals(quote.getTicker()))
          .findFirst();

      matchingIexQuote.ifPresent(iexQuote -> {
        updateQuoteWithIexQuote(quote, iexQuote);
        saveQuote(quote);
      });
    }

  }

  public List<Quote> saveQuotes(List<String> tickers){

    List<Quote> savedQuotes = new ArrayList<>();
    for (String ticker : tickers) {
      Quote quote = saveQuote(ticker);
      savedQuotes.add(quote);
    }
    return savedQuotes;

  }

  public Quote saveQuote(String ticker){
    IexQuote iexQuote = findIexQuoteByTicker(ticker);
    Quote quote = new Quote();
    quote.setTicker(ticker);
    updateQuoteWithIexQuote(quote, iexQuote);
    return saveQuote(quote);
  }

  public Quote saveQuote(Quote quote){
    return quoteDao.save(quote);
  }

  public List<Quote> findAllQuotes(){
    return (List<Quote>) quoteDao.findAll();
  }

  private void updateQuoteWithIexQuote(Quote quote, IexQuote iexQuote) {
    quote.setLastPrice(iexQuote.getLatestPrice());
    quote.setAskPrice(iexQuote.getIexAskPrice()==null ? 0 : iexQuote.getIexAskPrice());
    quote.setAskSize(iexQuote.getIexAskSize()==null ? 0 : iexQuote.getIexAskSize());
    quote.setBidPrice(iexQuote.getIexBidPrice()==null ? 0 : iexQuote.getIexBidPrice());
    quote.setBidSize(iexQuote.getIexBidSize()==null ? 0 : iexQuote.getIexBidSize());
  }
}
