package de.lmu.ifi.sosy.tbial.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Objects;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import de.lmu.ifi.sosy.tbial.ConfigurationException;
import de.lmu.ifi.sosy.tbial.DatabaseException;

/**
 * A database using JNDI data source to connect to a real database.
 *
 * @author Andreas Schroeder, SWEP 2014 Team.
 */
public class SQLDatabase implements Database {

  public static final String JNDI_PATH_DB = "java:/comp/env/jdbc/tbial";

  private final DataSource dataSource;

  public SQLDatabase() {
    try {
      InitialContext ctx = new InitialContext();
      dataSource = (DataSource) ctx.lookup(JNDI_PATH_DB);

    } catch (NamingException e) {
      throw new ConfigurationException("Error while looking up data source in JNDI.", e);
    }
    if (dataSource == null) {
      throw new ConfigurationException("No data source registered in JNDI for " + JNDI_PATH_DB);
    }
  }

  public PlayerStatistics getPlayerStatistics(String name) {
    Objects.requireNonNull(name, "name is null");

    try (Connection connection = getConnection();
        PreparedStatement query = userByNameQuery(name, connection);
        ResultSet result = query.executeQuery()) {

      return getPlayerStatisticsFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for player-statistics in DB.", e);
    }
  }

  @Override
  public User getUser(String name) {
    Objects.requireNonNull(name, "name is null");

    try (Connection connection = getConnection();
        PreparedStatement query = userByNameQuery(name, connection);
        ResultSet result = query.executeQuery()) {

      return getUserFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for user in DB.", e);
    }
  }

  @Override
  public boolean userNameTaken(String name) {
    Objects.requireNonNull(name, "name is null");

    try (Connection connection = getConnection();
        PreparedStatement query = userByNameQuery(name, connection);
        ResultSet result = query.executeQuery()) {

      return result.next();
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for user in DB.", e);
    }
  }

  @Override
  public User register(String name, String password) {
    Objects.requireNonNull(name, "name is null");
    Objects.requireNonNull(password, "password is null");

    try (Connection connection = getConnection(false);
        PreparedStatement insert = insertUserStatement(name, password, connection);
        ResultSet result = executeUpdate(insert)) {

      if (result != null && result.next()) {
        int id = result.getInt(1);
        User user = new User(id, name, password);
        connection.commit();
        return user;
      } else {
        connection.rollback();
        return null;
      }

    } catch (SQLException ex) {
      throw new DatabaseException("Error while registering user " + name, ex);
    }
  }

  public void updateStatistic(String name, PlayerStatistics statistics) {
    try (Connection connection = getConnection(false);
        PreparedStatement update = updateStatisticStatement(name, connection, statistics);
        ResultSet result = executeUpdate(update)) {

    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating the player statistic of " + name, ex);
    }
  }

  private User getUserFromResult(ResultSet result) throws SQLException {
    if (result.next()) {
      int id = result.getInt("ID");
      String name = result.getString("NAME");
      String password = result.getString("PASSWORD");
      return new User(id, name, password);
    } else {
      return null;
    }
  }

  private PlayerStatistics getPlayerStatisticsFromResult(ResultSet result) throws SQLException {
    if (result.next()) {
      String name = result.getString("NAME");
      int gameCount = result.getInt("GAMES");
      int winCount = result.getInt("WINS");
      int looseCount = result.getInt("LOSES");
      int managerCount = result.getInt("MANAGER");
      int consultantCount = result.getInt("CONSULTANT");
      int honestDeveloperCount = result.getInt("DEVELOPER");
      int evilCodeMonkeyCount = result.getInt("MONKEY");
      int bugCount = result.getInt("BUGS");
      return new PlayerStatistics(
          name,
          gameCount,
          winCount,
          looseCount,
          managerCount,
          consultantCount,
          honestDeveloperCount,
          evilCodeMonkeyCount,
          bugCount);
    } else {
      return null;
    }
  }

  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  private Connection getConnection(boolean autocommit) throws SQLException {
    Connection connection = dataSource.getConnection();
    connection.setAutoCommit(autocommit);
    return connection;
  }

  private ResultSet executeUpdate(PreparedStatement insertUser) throws SQLException {
    try {
      insertUser.executeUpdate();
      return insertUser.getGeneratedKeys();
    } catch (SQLIntegrityConstraintViolationException ex) {
      return null;
    }
  }

  private PreparedStatement userByNameQuery(String name, Connection connection)
      throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS WHERE NAME=?");
    statement.setString(1, name);
    return statement;
  }

  private PreparedStatement insertUserStatement(String name, String password, Connection connection)
      throws SQLException {
    PreparedStatement insertUser;
    insertUser =
        connection.prepareStatement(
            "INSERT INTO USERS (NAME, PASSWORD) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
    insertUser.setString(1, name);
    insertUser.setString(2, password);
    return insertUser;
  }

  private PreparedStatement updateStatisticStatement(
      String playerName, Connection connection, PlayerStatistics statistics) throws SQLException {
    int id = statistics.id;
    String name = statistics.name;
    int games = statistics.games;
    int wins = statistics.won;
    int loses = statistics.lost;
    int manager = statistics.managerCount;
    int consultant = statistics.consultantCount;
    int developer = statistics.honestDeveloperCount;
    int monkey = statistics.evilCodeMonkeyCount;
    int bugs = statistics.bugs;
    PreparedStatement statement =
        connection.prepareStatement(
            "UPDATE Statistics SET ID="
                + id
                + " NAME="
                + name
                + " GAMES="
                + games
                + " WINS="
                + wins
                + " Loses="
                + loses
                + " MANAGER="
                + manager
                + " CONSULTANT="
                + consultant
                + " DEVELOPER"
                + developer
                + " MONKEY="
                + monkey
                + " BUGS="
                + bugs
                + " WHERE NAME=?");
    statement.setString(1, name);
    return statement;
  }
}
