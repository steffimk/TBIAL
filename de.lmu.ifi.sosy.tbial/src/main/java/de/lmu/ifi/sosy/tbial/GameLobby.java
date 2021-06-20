package de.lmu.ifi.sosy.tbial;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;

/** The Lobby of a specific game in which the players wait for the game to start. */
@AuthenticationRequired
public class GameLobby extends BasePage {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(GameLobby.class);

  private final Link<Void> startGameLink;
  private final Label isHostLabel;
  private final Label currentStatusLabel;

  private final Game game;

  public GameLobby() {

    game = getSession().getCurrentGame();

    // Go to lobby, if there is no game in the session
    if (game == null || getSession().getUser() == null) {
      LOGGER.debug("Game or User in Session null -- redirecting to Lobby");
      throw new RestartResponseAtInterceptPageException(Lobby.class);
    }

    isHostLabel = new Label("isHostLabel", () -> currentHostMessage());
    isHostLabel.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3)));
    currentStatusLabel = new Label("currentStatusLabel", () -> getCurrentStatusMessage());
    currentStatusLabel.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3)));

    Form startGameForm = new Form("startGameForm");

    startGameLink =
        new Link<Void>("startGameLink") {
          private String customCSS = null;

          private static final long serialVersionUID = 1L;

          @Override
          public void onClick() {
            startGame();
          }

          @Override
          public void onConfigure() {
            if (game.hasStarted()) {
              throw new RestartResponseAtInterceptPageException(GameTable.class);
            }
            super.onConfigure();
          }

          @Override
          public boolean isVisible() {
            return isHost();
          }

          @Override
          public boolean isEnabled() {
            return game.getPlayers().size() > 3;
          }

          @Override
          protected void onComponentTag(ComponentTag tag) {
            if (game.getPlayers().size() < 4) {
              customCSS = "disabledStyle";
            } else {
              customCSS = "linkstyle";
            }
            super.onComponentTag(tag);
            tag.put("class", customCSS);
          }
        };
    startGameForm.add(startGameLink);
    startGameForm.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3)));

    Form leaveForm =
        new Form("leaveForm") {

          private static final long serialVersionUID = 1L;

          protected void onSubmit() {
            leaveCurrentGame();
            setResponsePage(getTbialApplication().getHomePage());
          }
        };

    Form menuForm = new Form("menuForm");
    Button showGamesButton =
        new Button("showGamesButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getGamesPage());
          }
        };
    menuForm.add(showGamesButton);

    Button showPlayersButton =
        new Button("showPlayersButton") {

          private static final long serialVersionUID = 1L;

          public void onSubmit() {
            setResponsePage(getTbialApplication().getPlayersPage());
          }
        };
    menuForm.add(showPlayersButton);
    add(leaveForm);
    add(menuForm);

    startGameLink.setOutputMarkupId(true);

    IModel<List<Player>> gameInfoModel =
        (IModel<List<Player>>) () -> getGame().getInGamePlayersList();

    ListView<Player> gameInfoList =
        new PropertyListView<>("gameInfos", gameInfoModel) {

          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(final ListItem<Player> listItem) {
            final Player player = listItem.getModelObject();
            if (player.getUserName() == null) throw new NullPointerException("Player is null!");
            listItem.add(new Label("playerName", player.getUserName()));
          }
        };

    WebMarkupContainer lockedIcon = new WebMarkupContainer("lockedIcon");
    lockedIcon.setOutputMarkupId(true);
    lockedIcon.setVisible(getGame().isPrivate());
    WebMarkupContainer unlockedIcon = new WebMarkupContainer("unlockedIcon");
    unlockedIcon.setOutputMarkupId(true);
    unlockedIcon.setVisible(!getGame().isPrivate());

    WebMarkupContainer gameInfoContainer = new WebMarkupContainer("gameInfoContainer");
    gameInfoContainer.add(gameInfoList);
    gameInfoContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3)));
    gameInfoContainer.setOutputMarkupId(true);

    Form<?> gameLobbyInfoForm = new Form<>("gameLobbyInfoForm");
    gameLobbyInfoForm.add(new Label("gameInfo", getGame().getName()));
    gameLobbyInfoForm.add(lockedIcon);
    gameLobbyInfoForm.add(unlockedIcon);
    gameLobbyInfoForm.add(gameInfoContainer);
    add(gameLobbyInfoForm);

    startGameLink.setOutputMarkupId(true);

    add(new ChatPanel("chatPanel", game.getChatMessages()));

    add(currentStatusLabel);
    add(isHostLabel);
    add(startGameForm);
  }

  /**
   * Checks whether the user is allowed to start a new game and does so if he is allowed. Redirects
   * the user to the game table.
   */
  private void startGame() {
    TBIALSession session = getSession();
    User user = session.getUser();
    if (game == null || user == null) {
      LOGGER.info(
          "Tried to start game but either current game or the user in the session was null.");
      return;
    } else if (!game.isAllowedToStartGame(user.getName())) {
      LOGGER.info("Tried to start game but wasn't allowed to.");
      return;
    }
    // Permission checked. Start new game!
    LOGGER.info("Starting the game.");
    game.startGame();
    setResponsePage(getTbialApplication().getGameTablePage());
  }

  /**
   * Returns the message about the current host
   *
   * @return String to be displayed in the isHostLabel
   */
  private String currentHostMessage() {
    String hostName = game.getHost();
    return isHost() ? "You are the host of this game." : hostName + " is the host of this game.";
  }

  /**
   * Returns whether the current user is the host of this game.
   *
   * @return whether user is host of the game
   */
  private boolean isHost() {
    TBIALSession session = getSession();
    String hostName = game.getHost();

    if (getSession().getUser() == null) throw new NullPointerException("Player is null!");
    return session.getUser().getName().equals(hostName);
  }

  /**
   * Leaves the current game: Changes host, when leaving player is the host; Removes the game from
   * GamesList; Sets current game of the leaving player null.
   */
  public void leaveCurrentGame() {
    if (!getGame().checkIfLastPlayer() && isHost()) {
      getGame().changeHost();
    }
    if (getGame().checkIfLastPlayer()) {
      getGameManager().removeGame(game);
    }
    getGame().getPlayers().remove(getSession().getUser().getName());
    getSession().setCurrentGameNull();
  }

  /**
   * Returns the state of players.
   *
   * @return A message about how many players can still join the game.
   */
  private String getCurrentStatusMessage() {
    int maxPlayers = game.getMaxPlayers();
    int currentPlayers = game.getCurrentNumberOfPlayers();

    String message = currentPlayers + "/" + maxPlayers + " players.";
    if (maxPlayers - currentPlayers == 0)
      return message + " Waiting for the host to start the game.";

    if (currentPlayers > 4) return message + " The host can start the game.";
    else return message + " Waiting for more players to join.";
  }

  public Game getGame() {
    return game;
  }
}
