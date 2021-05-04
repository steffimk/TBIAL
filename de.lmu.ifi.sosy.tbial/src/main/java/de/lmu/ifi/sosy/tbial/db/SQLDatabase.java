package de.lmu.ifi.sosy.tbial.db;

import de.lmu.ifi.sosy.tbial.ConfigurationException;
import de.lmu.ifi.sosy.tbial.DatabaseException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
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
  public boolean nameTaken(String name) {
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

  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  private Connection getConnection(boolean autocommit) throws SQLException {
    Connection connection = dataSource.getConnection();
    connection.setAutoCommit(autocommit);
    return connection;
  }

  private PreparedStatement userByNameQuery(String name, Connection connection)
      throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS WHERE NAME=?");
    statement.setString(1, name);
    return statement;
  }

  private ResultSet executeUpdate(PreparedStatement insertUser) throws SQLException {
    try {
      insertUser.executeUpdate();
      return insertUser.getGeneratedKeys();
    } catch (SQLIntegrityConstraintViolationException ex) {
      return null;
    }
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

  private PreparedStatement insertGameStatement(
      int hostId,
      String name,
      int maxPlayers,
      boolean isPrivate,
      String hash,
      String salt,
      Connection connection)
      throws SQLException {
    PreparedStatement insertGame;
    if (isPrivate) {
      insertGame =
          connection.prepareStatement(
              "INSERT INTO GAMES (HOSTID, NAME, MAXPLAYERS, ISPRIVATE, HASH, SALT) VALUES (?,?,?,?,?,?)",
              Statement.RETURN_GENERATED_KEYS);
      insertGame.setInt(1, hostId);
      insertGame.setString(2, name);
      insertGame.setInt(3, maxPlayers);
      insertGame.setBoolean(4, isPrivate);
      insertGame.setString(5, hash);
      insertGame.setString(6, salt);
    } else {
      insertGame =
          connection.prepareStatement(
              "INSERT INTO GAMES (HOSTID, NAME, MAXPLAYERS, ISPRIVATE) VALUES (?,?,?,?)",
              Statement.RETURN_GENERATED_KEYS);
      insertGame.setInt(1, hostId);
      insertGame.setString(2, name);
      insertGame.setInt(3, maxPlayers);
      insertGame.setBoolean(4, isPrivate);
    }

    return insertGame;
  }

  @Override
  public Game newGame(int hostId, String name, int maxPlayers, boolean isPrivate, String password) {
    Objects.requireNonNull(hostId, "hostId is null");
    Objects.requireNonNull(name, "game name is null");
    Objects.requireNonNull(maxPlayers, "maxPlayers is null");
    Objects.requireNonNull(isPrivate, "isPrivate is null");
    String hash = "", salt = "";
    if (isPrivate) {
      Objects.requireNonNull(password, "password is null");
      SecureRandom random = new SecureRandom();
      byte[] saltByteArray = new byte[16];
      random.nextBytes(saltByteArray);
      hash = getHashedPassword(password, saltByteArray);
      salt = new String(saltByteArray, StandardCharsets.UTF_8);
    }

    try (Connection connection = getConnection(false);
        PreparedStatement insert =
            insertGameStatement(hostId, name, maxPlayers, isPrivate, hash, salt, connection);
        ResultSet result = executeUpdate(insert)) {

      if (result != null && result.next()) {
        int id = result.getInt(1);
        Game game = new Game(id, hostId, name, maxPlayers, isPrivate, hash, salt);
        connection.commit();
        return game;
      } else {
        connection.rollback();
        return null;
      }

    } catch (SQLException ex) {
      throw new DatabaseException("Error while saving new game " + name + " to the database", ex);
    }
  }

  /**
   * Returns the hash of password and salt
   *
   * @param password
   * @param salt
   * @return
   */
  public static String getHashedPassword(String password, byte[] salt) {
    String hash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt);
      byte[] hashByteArray = md.digest(password.getBytes(StandardCharsets.UTF_8));
      hash = new String(hashByteArray, StandardCharsets.UTF_8);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return hash;
  }
  
}
