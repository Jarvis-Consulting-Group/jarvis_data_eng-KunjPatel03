package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public abstract class JdbcCrudDao <T extends Entity<Integer>> implements CrudRepository<T, Integer> {
  private static final Logger logger = LoggerFactory.getLogger(JdbcCrudDao.class);

  abstract public JdbcTemplate getJdbcTemplate();

  abstract public SimpleJdbcInsert getSimpleJdbcInsert();

  abstract public String getTableName();

  abstract public String getIdColumnName();

  abstract Class<T> getEntityClass();

  /**
   * Save an entity and update the auto-generated integer ID
   * @param entity to be saved
   * @return saved entity
   */
  @NotNull
  @Override
  public <S extends T> S save(S entity) {
    if (existsById(entity.getId())) {
      if (updateOne(entity) != 1) {
        throw new DataRetrievalFailureException("Unable to update quote");
      }
    }
    else {
      addOne(entity);
    }
    return entity;
  }

  /**
   * Helper method that saves one quote
   */
  private <S extends T> void addOne(final S entity) {
    SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(entity);

    Number newId = getSimpleJdbcInsert().executeAndReturnKey(parameterSource);
    entity.setId(newId.intValue());
  }

  /**
   * Helper method that updates one quote
   */
  abstract public int updateOne(final T entity);

  @NotNull
  @Override
  public Optional<T> findById(Integer id) {
    Optional<T> entity = Optional.empty();
    String selectSql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " =?";
    try{
      entity = Optional.ofNullable((T) getJdbcTemplate()
          .queryForObject(selectSql,
                BeanPropertyRowMapper.newInstance(getEntityClass()), id));
    }
    catch (IncorrectResultSizeDataAccessException e) {
      logger.debug("Can't find trader id:" + id, e);
    }
    return entity;
  }

  @Override
  public <S extends T> Iterable<S> saveAll(Iterable<S> iterable) {
    return null;
  }
  @Override
  public boolean existsById(Integer id) {
    String sql = "SELECT COUNT(*) FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
    Integer count = getJdbcTemplate().queryForObject(sql, Integer.class, id);
    return count != null && count != 0;
  }

  @NotNull
  @Override
  public Iterable<T> findAll() {
    String selectSql = "SELECT * FROM " + getTableName();
    return getJdbcTemplate().query(selectSql, BeanPropertyRowMapper.newInstance(getEntityClass()));
  }

  @Override
  public Iterable<T> findAllById(Iterable<Integer> ids) {
    List<T> traders = new ArrayList<T>();
    for (Integer id : ids) {
      Optional<T> trader = findById(id);
      trader.ifPresent(traders::add);
    }
    return traders;
  }

  @Override
  public long count() {
    String sql = "SELECT COUNT(*) FROM " + getTableName();
    return getJdbcTemplate().queryForObject(sql,Long.class);
  }

  @Override
  public void deleteById(Integer id) {
    String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
    logger.debug(sql);
    int noOfRows = getJdbcTemplate().update(sql, id);
    logger.debug("Deleted " + noOfRows + " rows");
  }

  @Override
  public void deleteAll() {
    String sql = "DELETE FROM " + getTableName();
    int noOfRows = getJdbcTemplate().update(sql);
    logger.debug("Deleted " + noOfRows + " rows");
  }
}
