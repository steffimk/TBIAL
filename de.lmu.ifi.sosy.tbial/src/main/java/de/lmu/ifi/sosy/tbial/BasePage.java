package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import org.apache.wicket.markup.html.panel.Panel;

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

    // show invitation messages
    Form<?> messageForm = new Form<>("messageForm");
    Link<Void> message =
        new Link<Void>("message") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1L;

          @Override
          public void onClick() {
            // TODO open message in modal window?
          }
        };
    // TODO add correct number of messages; add blinking
    Label numberOfMessages = new Label("numberOfMessages", "1");
    messageForm.add(numberOfMessages);
    messageForm.add(message);

    messageForm.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    messageForm.setVisible(false);
    add(messageForm);

    Session currentSession = super.getSession();
    if (currentSession instanceof TBIALSession
        && ((TBIALSession) currentSession).getUser() != null) {
      loggedInUsername.setDefaultModelObject(((TBIALSession) currentSession).getUser().getName());
      add(loggedInUsername);
      messageForm.setVisible(true);
    }

    if (!getSession().isSignedIn()) {
      link.setVisible(false);
      link.setEnabled(false);
      loggedInUsername.setVisible(false);
    }
  }

  public String getUsersString() {
    int users = getTbialApplication().getUsersLoggedInCount();
    return users == 1 ? "1 player online." : users + " players online.";
  }
}
