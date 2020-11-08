package de.lmu.ifi.sosy.tbial.db;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.lmu.ifi.sosy.tbial.ConfigurationException;
import de.lmu.ifi.sosy.tbial.DatabaseException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.tools.ij;
import org.junit.Before;
import org.junit.Test;

public class SQLDatabaseTest extends AbstractDatabaseTest {

  private static EmbeddedDataSource dataSource = new EmbeddedDataSource();

  static {
    dataSource.setDatabaseName("tbial_test");
    dataSource.setCreateDatabase("create");
  }

  private Connection con;

  public static class CtxFactory implements InitialContextFactory {

    private static DataSource ds = dataSource;

    private static boolean fReturnNullContext = false;

    static void setDataSource(DataSource dataSource) {
      ds = dataSource;
    }

    static void setReturnNullContext(boolean returnNullContext) {
      fReturnNullContext = returnNullContext;
    }

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
      if (fReturnNullContext) {
        return null;
      }
      Context ctx = mock(Context.class);
      when(ctx.lookup(SQLDatabase.JNDI_PATH_DB)).thenReturn(ds);

      return ctx;
    }
  }

  @Before
  public void setUpFixture() throws NamingException, SQLException, IOException {
    System.getProperties().setProperty("java.naming.factory.initial", CtxFactory.class.getName());
    CtxFactory.setDataSource(dataSource);
    CtxFactory.setReturnNullContext(false);

    InitialContext ctx = new InitialContext();
    DataSource ds = (DataSource) ctx.lookup(SQLDatabase.JNDI_PATH_DB);
    database = new SQLDatabase();
    try {
      con = ds.getConnection();

      ij.runScript(
          con,
          new FileInputStream("etc/database/sql/create_tables_derby.sql"),
          "UTF8",
          System.out,
          "UTF8");

    } finally {
      if (con != null) {
        con.close();
      }
    }
  }

  @Override
  protected void addUser(User user) {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = dataSource.getConnection();
      con.setAutoCommit(true);

      ps =
          con.prepareStatement(
              "INSERT INTO USERS (NAME, PASSWORD) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, user.getName());
      ps.setString(2, user.getPassword());

      ps.executeUpdate();

      ResultSet generatedIds = ps.getGeneratedKeys();
      int id = -1;
      if (generatedIds.next()) {
        id = generatedIds.getInt(1);
      } else {
        throw new SQLException("No id was generated for new row in table USERS.");
      }

      user.setId(id);

    } catch (SQLException ex) {
      throw new ConfigurationException("msg.exception.ConfigException", ex);

    } finally {
      try {
        if (ps != null) {
          ps.close();
        }
        if (con != null) {
          con.close();
        }
      } catch (Exception ex) {
        ex.printStackTrace(System.err);
      }
    }
  }

  @Test(expected = ConfigurationException.class)
  public void newSQLDatabaseWhenNoContextThrowsConfigurationException() {
    CtxFactory.setReturnNullContext(true);
    database = new SQLDatabase();
  }

  @Test(expected = ConfigurationException.class)
  public void newSQLDatabaseWhenNoDSThrowsConfigurationException() {
    CtxFactory.setDataSource(null);
    database = new SQLDatabase();
  }

  @Test(expected = DatabaseException.class)
  public void getUserWhenSQLExceptionInQueryThrowsDatabaseException() throws SQLException {
    DataSource ds = createDataSourceWithStatement(createStatementThatFailsOnQuery());

    CtxFactory.setDataSource(ds);
    database = new SQLDatabase();
    database.getUser("test");
  }

  @Test(expected = DatabaseException.class)
  public void registerUserWhenNoConnectionThrowsDatabaseException() throws SQLException {
    DataSource ds = mock(DataSource.class);
    when(ds.getConnection()).thenThrow(new SQLException("This is totally fake..."));

    CtxFactory.setDataSource(ds);
    database = new SQLDatabase();
    database.register("user", "password");
  }

  @Test(expected = DatabaseException.class)
  public void getUserWhenCloseResourcesFailsThrowsDatabaseException() throws SQLException {
    PreparedStatement ps = createStatementThatReturns(userResultSet());
    doThrow(new SQLException("Closing resources is for lamers!")).when(ps).close();

    DataSource ds = createDataSourceWithStatement(ps);
    CtxFactory.setDataSource(ds);
    database = new SQLDatabase();
    database.getUser("test");
  }

  @Test
  public void registerUserWhenNoIdsGeneratedDoRollback() throws SQLException {

    DataSource ds = mock(DataSource.class);

    Connection conn = createConnectionThatDoesntGenerateIds();

    when(ds.getConnection()).thenReturn(conn);

    CtxFactory.setDataSource(ds);
    database = new SQLDatabase();
    database.register("test", "test");

    verify(conn).rollback();
  }

  private DataSource createDataSourceWithStatement(PreparedStatement statement)
      throws SQLException {
    DataSource ds = mock(DataSource.class);
    Connection conn = mock(Connection.class);

    when(conn.prepareStatement(anyString())).thenReturn(statement);
    when(ds.getConnection()).thenReturn(conn);

    return ds;
  }

  private PreparedStatement createStatementThatFailsOnQuery() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    when(ps.executeQuery()).thenThrow(new SQLException("This is totally fake..."));
    return ps;
  }

  private PreparedStatement createStatementThatReturns(ResultSet result) throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);
    when(ps.executeQuery()).thenReturn(result);
    return ps;
  }

  private ResultSet userResultSet() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    // only return one row
    when(rs.next()).thenReturn(true).thenReturn(false);

    when(rs.getString("NAME")).thenReturn("name");
    when(rs.getString("PASSWORD")).thenReturn("pass");
    when(rs.getInt("ID")).thenReturn(1);
    return rs;
  }

  private PreparedStatement createStatementThatDoesntGenerateIds() throws SQLException {
    PreparedStatement ps = mock(PreparedStatement.class);

    ResultSet emptyResult = emptyResultSet();
    when(ps.getGeneratedKeys()).thenReturn(emptyResult);

    return ps;
  }

  private ResultSet emptyResultSet() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.next()).thenReturn(false);
    return rs;
  }

  private Connection createConnectionThatDoesntGenerateIds() throws SQLException {
    PreparedStatement ps1 = createStatementThatReturns(emptyResultSet());
    PreparedStatement ps2 = createStatementThatDoesntGenerateIds();

    Connection conn = mock(Connection.class);

    when(conn.prepareStatement(anyString())).thenReturn(ps1);
    when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
        .thenReturn(ps2);
    return conn;
  }
}
