package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Quote;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class QuoteDao implements CrudRepository<Quote, String> {

  private static final String TABLE_NAME = "quote";
  private static final String ID_COLUMN_NAME = "ticker";

  private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class);
  private JdbcTemplate jdbcTemplate;
  private SimpleJdbcInsert simpleJdbcInsert;

  @Autowired
  public QuoteDao(DataSource dataSource){
    jdbcTemplate = new JdbcTemplate(dataSource);
    simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME);
  }

  @Override
  public Quote save(Quote quote) {
    if (existsById(quote.getTicker())) {
      int updatedRowNo = updateOne(quote);
      if (updatedRowNo != 1) {
        throw new DataRetrievalFailureException("Unable to update quote");
      }
    } else {
      addOne(quote);
    }
    return quote;
  }

  private void addOne(Quote quote) {
    SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(quote);
    int row = simpleJdbcInsert.execute(parameterSource);
    if (row != 1) {
      throw new IncorrectResultSizeDataAccessException("Failed to insert", 1, row);
    }
  }

  private int updateOne(Quote quote) {
    String update_sql = "UPDATE " + TABLE_NAME + " SET last_price=?, bid_price=?, " +
        "bid_size=?, ask_price=?, ask_size=? WHERE " + ID_COLUMN_NAME + "=?";
    return jdbcTemplate.update(update_sql, makeUpdateValues(quote));
  }

  private Object[] makeUpdateValues(Quote quote) {
    return new Object[]{quote.getLastPrice(), quote.getBidPrice(), quote.getBidSize(), quote.getAskPrice(),
        quote.getAskSize(), quote.getTicker()};
  }

  @Override
  public <S extends Quote> Iterable<S> saveAll(Iterable<S> iterable) {
    iterable.forEach(this::save);
    return iterable;
  }

  @Override
  public Optional<Quote> findById(String ticker) {
    String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + " = ?";
    try {
      Quote quote = jdbcTemplate.queryForObject(query, BeanPropertyRowMapper.newInstance(Quote.class), ticker);
      return Optional.of(quote);
    } catch (EmptyResultDataAccessException e) {
      logger.debug("Can't find trader id: " + ticker, e);
      return Optional.empty();
    }
  }

  @Override
  public boolean existsById(String ticker) {
    String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + " = ?";
    int row = jdbcTemplate.queryForObject(query, Integer.class, ticker);
    return row == 1;
  }

  @Override
  public Iterable<Quote> findAll() {
    String query = "SELECT * FROM " + TABLE_NAME;
    return jdbcTemplate.query(query, BeanPropertyRowMapper.newInstance(Quote.class));
  }

  @Override
  public Iterable<Quote> findAllById(Iterable<String> iterable) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public long count() {
    String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
    return jdbcTemplate.queryForObject(query, Long.class);
  }

  @Override
  public void deleteById(String ticker) {
    if (ticker.length() == 0) {
      throw new IllegalArgumentException("Id/ticker cannot be null");
    }
    String query = "DELETE FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + " = ?";
    jdbcTemplate.update(query, ticker);
  }

  @Override
  public void delete(Quote quote) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteAll(Iterable<? extends Quote> iterable) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteAll() {
    String query = "DELETE FROM " + TABLE_NAME;
    jdbcTemplate.update(query);
  }
}
