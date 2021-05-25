package de.lmu.ifi.sosy.tbial;

import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

/** The Lobby of a specific game in which the players wait for the game to start. */
@AuthenticationRequired
public class GameLobby extends BasePage {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(GameLobby.class);

  private final Link<Void> startGameLink;
  private final Label isHostLabel;
  private final Label currentStatusLabel;

  private static final int maxMessages = 80;
  private static final LinkedList<ChatMessage> chatMessages = new LinkedList<ChatMessage>();
  private MarkupContainer chatMessagesContainer;

  private final Game game;

  public GameLobby() {

    game = getSession().getCurrentGame();

    // Go to lobby, if there is no game in the session
    if (game == null || getSession().getUser() == null) {
      LOGGER.debug("Game or User in Session null -- redirecting to Lobby");
      throw new RestartResponseAtInterceptPageException(Lobby.class);
    }

    isHostLabel = new Label("isHostLabel", () -> currentHostMessage());
    currentStatusLabel = new Label("currentStatusLabel", () -> getCurrentStatusMessage());

    startGameLink =
        new Link<Void>("startGameLink") {
          private static final long serialVersionUID = 1L;

          @Override
          public void onClick() {
            startGame();
          }

          @Override
          public boolean isVisible() {
            // TODO: When implementing change of host: Check if this works or if we have to set
            // visibility differently.
            return isHost();
          }

          @Override
          public boolean isEnabled() {
            // TODO: When implementing join game: Check if this works or if we have to set
            // isEnabled differently.
            return game.getPlayers().size() > 3;
          }
        };

    Form<?> LeaveForm =
        new Form<>("LeaveForm") {

          private static final long serialVersionUID = 1L;

          protected void onSubmit() {
            leaveCurrentGame();
            setResponsePage(getTbialApplication().getHomePage());
          }
        };
    add(LeaveForm);

    Form<?> gameLobbyInfoForm = new Form<>("gameLobbyInfoForm");
    gameLobbyInfoForm.add(new Label("name", getGame().getName()));
    gameLobbyInfoForm.add(new Label("privacy", getGame().isPrivate()));
    gameLobbyInfoForm.add(new Label("privacyTextOne", "(Private: "));
    gameLobbyInfoForm.add(new Label("privacyTextTwo", " )"));
    add(gameLobbyInfoForm);

    startGameLink.setOutputMarkupId(true);

    final TextField<String> textField = new TextField<String>("message", new Model<String>());
    textField.setOutputMarkupId(true);

    chatMessagesContainer = new WebMarkupContainer("chatMessages");

    final ListView<ChatMessage> listView =
        new ListView<ChatMessage>("messages", chatMessages) {
          private static final long serialVersionUID = 1L;

          @Override
          protected void populateItem(ListItem<ChatMessage> item) {
            this.modelChanging();

            ChatMessage chatMessage = item.getModelObject();

            Label sender = new Label("sender", new PropertyModel<String>(chatMessage, "sender"));
            item.add(sender);

            Label text =
                new Label("textMessage", new PropertyModel<String>(chatMessage, "textMessage"));
            item.add(text);
          }
        };

    chatMessagesContainer.setOutputMarkupId(true);
    chatMessagesContainer.add(listView);

    AjaxSelfUpdatingTimerBehavior ajaxBehavior =
        new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3));
    chatMessagesContainer.add(ajaxBehavior);
    add(chatMessagesContainer);

    Component send =
        new AjaxSubmitLink("send") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onSubmit(AjaxRequestTarget target) {
            String username = ((TBIALSession) getSession()).getUser().getName();
            String text = textField.getModelObject();

            ChatMessage chatMessage = new ChatMessage(username, text);

            synchronized (chatMessages) {
              if (chatMessages.size() >= maxMessages) {
                chatMessages.removeFirst();
              }

              chatMessages.addLast(chatMessage);
            }

            textField.setModelObject("");
            target.add(chatMessagesContainer, textField);
          }
        };

    Component chatForm = new Form<String>("form").add(textField, send);

    add(currentStatusLabel);
    add(isHostLabel);
    add(startGameLink);
    add(chatForm);
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
    return session.getUser().getName().equals(hostName);
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

  //Leaves the current game
  public void leaveCurrentGame() {
    Map<String, Game> currentGames = getGameManager().getCurrentGames();
    String currentGameName = getSession().getCurrentGame().getName();
    if (!getGame().checkIfLastPlayer()) {
      getGame().checkHostChange();
    }
    if (getGame().checkIfLastPlayer()) {
      currentGames.remove(currentGameName);
    }
    getSession().setCurrentGameNull();
  }

  public Game getGame() {
    return game;
  }
}
