package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.InMemoryDatabase;
import de.lmu.ifi.sosy.tbial.game.GameManager;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.util.tester.WicketTester;

public abstract class PageTestBase {

  protected TBIALApplication application;

  protected Database database;

  protected WicketTester tester;

  protected final void setupApplication() {
    setupApplication(RuntimeConfigurationType.DEVELOPMENT);
  }

  protected final void setupApplication(RuntimeConfigurationType configuration) {
    database = new InMemoryDatabase();
    application = new TBIALApplication(database);
    application.setConfigurationType(configuration);
    tester = new WicketTester(application);
  }

  protected TBIALSession getSession() {
    return (TBIALSession) tester.getSession();
  }

  protected TBIALApplication getApplication() {
    return application;
  }

  protected GameManager getGameManager() {
    return application.getGameManager();
  }
}
