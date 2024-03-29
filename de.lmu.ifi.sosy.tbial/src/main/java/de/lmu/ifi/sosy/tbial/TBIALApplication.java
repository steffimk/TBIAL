package de.lmu.ifi.sosy.tbial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.time.Duration;

import com.googlecode.wicket.kendo.ui.settings.KendoUILibrarySettings;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.SQLDatabase;
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.GameManager;
import de.lmu.ifi.sosy.tbial.util.VisibleForTesting;

/**
 * The web application "The Bug Is A Lie".
 *
 * @author Andreas Schroeder, Christian Kroiß SWEP 2013 Team.
 */
public class TBIALApplication extends WebApplication {

  private final Database database;

  private final GameManager gameManager = new GameManager();

  // Use LinkedHashSet to keep iteration order over current users always the same
  private final Set<User> loggedInUsers = Collections.synchronizedSet(new LinkedHashSet<>());

  public static Database getDatabase() {
    return ((TBIALApplication) get()).database;
  }

  public TBIALApplication() {
    this(new SQLDatabase());
  }

  @VisibleForTesting
  TBIALApplication(Database database) {
    super();
    this.database = database;
  }

  /** Returns a new {@link TBIALSession} instead of a default Wicket {@link Session}. */
  @Override
  public TBIALSession newSession(Request request, Response response) {
    return new TBIALSession(request);
  }

  @Override
  protected void init() {
    super.init();
    initMarkupSettings();
    initPageMounts();
    initAuthorization();
    // initExceptionHandling();
  }

  private void initMarkupSettings() {
    if (getConfigurationType().equals(RuntimeConfigurationType.DEPLOYMENT)) {
      getMarkupSettings().setStripWicketTags(true);
      super.getMarkupSettings().setStripWicketTags(true);
      getMarkupSettings().setStripComments(true);
      getMarkupSettings().setCompressWhitespace(true);
      getResourceSettings().setDefaultCacheDuration(Duration.NONE);

      KendoUILibrarySettings settings = KendoUILibrarySettings.get();
      settings.setCommonStyleSheetReference(
          new CssResourceReference(TBIALApplication.class, "kendo.common.min.css"));
      settings.setThemeStyleSheetReference(
          new CssResourceReference(TBIALApplication.class, "kendo.default.min.css"));
    }
  }

  private void initPageMounts() {
    mountPage("home", getHomePage());
    mountPage("login", Login.class);
    mountPage("register", Register.class);
    mountPage("lobby", Lobby.class);
    mountPage("gameLobby", GameLobby.class);
    mountPage("gameTable", GameTable.class);
    mountPage("users", PlayersPage.class);
    mountPage("games", GamesPage.class);
  }

  /**
   * Initializes authorization so that pages annotated with {@link AuthenticationRequired} require a
   * valid, signed-in user.
   */
  private void initAuthorization() {
    getSecuritySettings()
        .setAuthorizationStrategy(
            new IAuthorizationStrategy() {

              @Override
              public <T extends IRequestableComponent> boolean isInstantiationAuthorized(
                  Class<T> componentClass) {
                boolean requiresAuthentication =
                    componentClass.isAnnotationPresent(AuthenticationRequired.class);
                TBIALSession session = (TBIALSession) Session.get();
                boolean isSignedIn = session.isSignedIn();
                if (requiresAuthentication
                    && (!isSignedIn
                        || !loggedInUsers.contains(((TBIALSession) Session.get()).getUser()))) {
                  session.signOut();
                  session.invalidate();
                  // redirect the user to the login page.
                  throw new RestartResponseAtInterceptPageException(Login.class);
                }

                // continue.
                return true;
              }

              @Override
              public boolean isActionAuthorized(Component component, Action action) {
                // all actions are authorized.
                return true;
              }

              @Override
              public boolean isResourceAuthorized(IResource arg0, PageParameters arg1) {
                // all resources are authorized
                return true;
              }
            });
  }

  @Override
  public Class<Lobby> getHomePage() {
    return Lobby.class;
  }

  public Class<GameLobby> getGameLobbyPage() {
    return GameLobby.class;
  }

  public Class<GameTable> getGameTablePage() {
    return GameTable.class;
  }

  public Class<GamesPage> getGamesPage() {
    return GamesPage.class;
  }

  public Class<PlayersPage> getPlayersPage() {
    return PlayersPage.class;
  }

  public GameManager getGameManager() {
    return gameManager;
  }

  public int getUsersLoggedInCount() {
    return loggedInUsers.size();
  }

  public List<User> getLoggedInUsers() {
    return new ArrayList<>(loggedInUsers);
  }

  public void userLoggedIn(final User pUser) {
    loggedInUsers.add(pUser);
  }

  public void userLoggedOut(final User pUser) {
    loggedInUsers.remove(pUser);
  }
}
