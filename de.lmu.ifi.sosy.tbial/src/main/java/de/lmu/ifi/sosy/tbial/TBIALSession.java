package de.lmu.ifi.sosy.tbial;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

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

  private Game currentGame = null;

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

    setSignedIn(user);
    LOGGER.info("User '" + name + "' login successful");
    return true;
  }

  public boolean isInGame() {
    return getCurrentGame() != null;
  }

  private Database getDatabase() {
    return TBIALApplication.getDatabase();
  }

  protected TBIALApplication getTbialApplication() {
    return (TBIALApplication) getApplication();
  }

  /** Leaves the current game (if not null), signs out and clears the user. */
  @Override
  public void signOut() {
    if (getCurrentGame() != null) {
      if (!getCurrentGame().checkIfLastPlayer()
          && getUser().getName().equals(getCurrentGame().getHost())) {
        getCurrentGame().changeHost();
      }
      if (getCurrentGame().checkIfLastPlayer()) {
        getTbialApplication().getGameManager().removeGame(getCurrentGame());
      }
      getCurrentGame().getPlayers().remove(getUser().getName());
      setCurrentGameNull();
    }
    super.signOut();
    if (user != null) {
      String name = user.getName();
      getTbialApplication().userLoggedOut(user);
      user = null;
      LOGGER.info("User '" + name + "' signed out.");
    }
  }

  public void setSignedIn(User user) {
    Objects.requireNonNull(user);
    setUser(user);
    signIn(true);
    getTbialApplication().userLoggedIn(user);
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

  public Game getCurrentGame() {
    return currentGame;
  }

  public void setCurrentGame(Game game) {
    this.currentGame = game;
  }

  public void setCurrentGameNull() {
    this.currentGame = null;
  }

  /**
   * Method to join a game, adding to the games player list and setting the current game
   *
   * @param game
   * @param password
   */
  public boolean joinGame(Game game, String password) {
    String username = getUser().getName();
    if (game.checkIfYouCanJoin(username, password)) {
      game.addNewPlayer(username);
      setCurrentGame(game);
      return true;
    }
    return false;
  }
}
