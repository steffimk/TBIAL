package de.lmu.ifi.sosy.tbial.db;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;

public class MockContextFactory implements InitialContextFactory {

  private static DataSource dataSource;

  private static boolean returnNullContext = false;

  static void setDataSource(DataSource pDataSource) {
    dataSource = pDataSource;
  }

  static void setReturnNullContext(boolean pReturnNullContext) {
    returnNullContext = pReturnNullContext;
  }

  @Override
  public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
    if (returnNullContext) {
      return null;
    }
    Context ctx = mock(Context.class);
    when(ctx.lookup(SQLDatabase.JNDI_PATH_DB)).thenReturn(dataSource);

    return ctx;
  }
}
