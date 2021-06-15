package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.game.GameManager;

/**
 * Basic page with style template as well as access to {@link TBIALSession} and {@link Database}.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public abstract class BasePage extends WebPage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private Link<Void> link;

  private Label users;
  private Label loggedInUsername;

  protected Database getDatabase() {
    return TBIALApplication.getDatabase();
  }

  protected GameManager getGameManager() {
    return getTbialApplication().getGameManager();
  }

  protected TBIALApplication getTbialApplication() {
    return (TBIALApplication) super.getApplication();
  }

  @Override
  public TBIALSession getSession() {
    return (TBIALSession) super.getSession();
  }

  public BasePage() {
    link =
        new Link<Void>("signout") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1L;

          @Override
          public void onClick() {
            Session session = super.getSession();
            if (session instanceof AuthenticatedWebSession) {
              ((AuthenticatedWebSession) session).signOut();
            }

            session.invalidate();
            setResponsePage(getTbialApplication().getHomePage());
          }
        };

    users = new Label("users", new PropertyModel<>(this, "usersString"));
    users.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));

    add(link);
    add(users);

    // Show user name in navigation bar next to Logout
    loggedInUsername = new Label("loggedInUsername", "");
	loggedInUsername.setOutputMarkupId(true);
    add(loggedInUsername);
    
    Session currentSession = super.getSession();
    if (currentSession instanceof TBIALSession && ((TBIALSession)currentSession).getUser() != null) {
    	loggedInUsername.setDefaultModelObject(((TBIALSession)currentSession).getUser().getName());
    	add(loggedInUsername);
    }
    
    if (!getSession().isSignedIn()) {
      link.setVisible(false);
      link.setEnabled(false);
      loggedInUsername.setVisible(false);
    }

    setFreezePageId(true);
  }

  public String getUsersString() {
    int users = getTbialApplication().getUsersLoggedInCount();
    return users == 1 ? "1 player online." : users + " players online.";
  }
}
