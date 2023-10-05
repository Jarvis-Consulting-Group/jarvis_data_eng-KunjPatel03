package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Account;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDao extends JdbcCrudDao<Account>{
  public static final String TABLE_NAME = "account";
  public static final String ID_NAME = "id";
  public static final String TRADER_ID_NAME = "trader_id";
  static final Logger logger = LoggerFactory.getLogger(AccountDao.class);
  private JdbcTemplate jdbcTemplate;
  private SimpleJdbcInsert simpleJdbcInsert;

  @Autowired
  public AccountDao(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
    simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME).usingGeneratedKeyColumns(ID_NAME);
  }

  @Override
  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  @Override
  public SimpleJdbcInsert getSimpleJdbcInsert() {
    return simpleJdbcInsert;
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
  Class<Account> getEntityClass() {
    return Account.class;
  }

  @Override
  public int updateOne(Account entity) {
    String update_sql = "UPDATE " + getTableName() + " SET amount = ? WHERE " + getIdColumnName() + "= ?";
    Object[] values = { entity.getAmount(), entity.getId() };
    return jdbcTemplate.update(update_sql, values);
  }

  @Override
  public void delete(Account account) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void deleteAll(Iterable<? extends Account> iterable) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public Optional<Account> findByTraderId(Integer traderId) {
    Optional<Account> entity = Optional.empty();
    String selectSql = "SELECT * FROM " + getTableName() + " WHERE " + TRADER_ID_NAME + " = ?";
    try {
      entity = Optional.ofNullable(getJdbcTemplate().queryForObject(selectSql, BeanPropertyRowMapper.newInstance(getEntityClass()), traderId));
    }
    catch (IncorrectResultSizeDataAccessException e) {
      AccountDao.logger.debug("Can't find trader id:" + traderId, e);
    }
    return entity;
  }
}
