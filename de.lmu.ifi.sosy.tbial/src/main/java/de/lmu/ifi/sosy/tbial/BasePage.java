package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.User;
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
  private Label numberOfMessages;

  private int displayedInvitationCounter;

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
    Session currentSession = super.getSession();

    link =
        new Link<Void>("signout") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1L;

          @Override
          public void onClick() {
            if (currentSession instanceof AuthenticatedWebSession) {
              ((AuthenticatedWebSession) currentSession).signOut();
            }
            currentSession.invalidate();
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

    // show invitation messages
    final ModalWindow modal;
    add(modal = new ModalWindow("modal"));
    modal.setTitle("Your Notifications");
    if (((TBIALSession) currentSession).getUser() != null) {
      modal.setContent(
          new NotificationPanel(modal.getContentId(), ((TBIALSession) currentSession).getUser()));
    }
    modal.setCloseButtonCallback(
        target -> {
          return true;
        });

    WebMarkupContainer messageContainer =
        new WebMarkupContainer("messageContainer") {

          private static final long serialVersionUID = 1L;

          @Override
          public boolean isVisible() {
            return ((TBIALSession) currentSession).getUser() != null;
          }

          @Override
          protected void onBeforeRender() {
            User currentUser = ((TBIALSession) currentSession).getUser();
            if (currentUser != null
                && currentUser.getInvitations().size() != 0
                && currentUser.getInvitations().size() != displayedInvitationCounter) {
              this.add(new AttributeModifier("class", "blink"));
              displayedInvitationCounter = currentUser.getInvitations().size();
            } else {
              this.add(new AttributeModifier("class", "noBlinking"));
            }

            super.onBeforeRender();
          }
        };
    AjaxLink<Void> message =
        new AjaxLink<Void>("message") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1L;

          @Override
          public void onClick(AjaxRequestTarget target) {
            modal.show(target);
          }
        };

    messageContainer.add(message);
    numberOfMessages =
        new Label(
            "numberOfMessages",
            () -> {
              User currentUser = ((TBIALSession) currentSession).getUser();
              if (currentUser == null) {
                return "";
              } else return currentUser.getInvitations().size();
            });
    numberOfMessages.setOutputMarkupId(true);
    messageContainer.add(numberOfMessages);
    messageContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
    add(messageContainer);

    if (currentSession instanceof TBIALSession
        && ((TBIALSession) currentSession).getUser() != null) {
      loggedInUsername.setDefaultModelObject(((TBIALSession) currentSession).getUser().getName());
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

  @Override
  protected void setHeaders(WebResponse response) {
    response.setHeader("Date", "[now]");
    response.setHeader("Expires", "[0]");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, private");
  }
}
