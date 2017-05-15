package de.lmu.ifi.sosy.tbial;

import static java.util.Objects.requireNonNull;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.User;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

/**
 * An authenticated TBIAL session.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public class TBIALSession extends AuthenticatedWebSession {

  /** UID for serialization. */
  private static final long serialVersionUID = 1;

  private static final Logger LOGGER = LogManager.getLogger(TBIALSession.class);

  private User user;

  public TBIALSession(Request request) {
    super(request);
  }

  @Override
  public boolean authenticate(String name, String password) {
    requireNonNull(name);
    requireNonNull(password);

    User user = getDatabase().getUser(name);

    if (user == null) {
      LOGGER.info("User '" + name + "' failed login attempt: user unknown");
      setUser(null);
      return false;
    }

    if (!user.getPassword().equals(password)) {
      LOGGER.info("User '" + name + "' failed login attempt: wrong password");
      return false;
    }

    setUser(user);
    getTbialApplication().userLoggedIn();
    LOGGER.info("User '" + name + "' login successful");
    return true;
  }

  private Database getDatabase() {
    return TBIALApplication.getDatabase();
  }

  protected TBIALApplication getTbialApplication() {
    return (TBIALApplication) getApplication();
  }

  /** Signs out and clears the user. */
  @Override
  public void signOut() {
    super.signOut();
    if (user != null) {
      String name = user.getName();
      user = null;
      getTbialApplication().userLoggedOut();
      LOGGER.info("User '" + name + "' signed out.");
    }
  }

  public void setSignedIn(User user) {
    Objects.requireNonNull(user);
    setUser(user);
    signIn(true);
    bind();
  }

  @Override
  public Roles getRoles() {
    return null;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
