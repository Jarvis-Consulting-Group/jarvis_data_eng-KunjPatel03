package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Position;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class PositionDao extends JdbcCrudDao<Position>{
  private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);
  private static final String TABLE_NAME = "position";
  private static final String ID_NAME = "account_id";
  private JdbcTemplate jdbcTemplate;

  @Autowired
  public PositionDao(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }
  @Override
  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  @Override
  public SimpleJdbcInsert getSimpleJdbcInsert() {
    return null;
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public String getIdColumnName() {
    return ID_NAME;
  }

  @Override
  Class getEntityClass() {
    return Position.class;
  }

  @Override
  public int updateOne(Position entity) {
    throw new UnsupportedOperationException("Position view is read-only");
  }

  public List<Position> findByAccountId(Integer accountId) {
    String selectSql = "SELECT * FROM " + TABLE_NAME + " WHERE account_id = ?";
    return jdbcTemplate.query(selectSql, BeanPropertyRowMapper.newInstance(Position.class), accountId);
  }

  public Long findByIdAndTicker(Integer accountId, String ticker) {
    String selectSql = "SELECT * FROM " + TABLE_NAME + " WHERE account_id = ? AND ticker = ?";
    Long position = 0L;

    try {
      position = jdbcTemplate.queryForObject(selectSql,Long.class,accountId, ticker);
    }
    catch (EmptyResultDataAccessException e) {
      PositionDao.logger.debug(String.format("select position from position accountId=%s and ticker=%s", accountId, ticker));
    }
    return position;
  }

  public List<Position> findAllByAccountId(int account_id) {
    List<Integer> accountId = Collections.singletonList(account_id);
    return (List<Position>) findAllById(accountId);
  }

  @Override
  public void delete(Position position) {
    throw new UnsupportedOperationException("Position view is read-only");
  }

  @Override
  public void deleteAll(Iterable<? extends Position> iterable) {
    throw new UnsupportedOperationException("Position view is read-only");
  }
}
