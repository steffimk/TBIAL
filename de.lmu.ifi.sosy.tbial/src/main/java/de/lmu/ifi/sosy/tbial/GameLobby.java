package de.lmu.ifi.sosy.tbial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

/**
 * The Lobby of a specific game in which the players wait for the game to start.
 */
@AuthenticationRequired
public class GameLobby extends BasePage {

  private static final long serialVersionUID = 1L;
  
  private static final Logger LOGGER = LogManager.getLogger(GameLobby.class);

  private final Link<Void> startGameLink;
  private final Label isHostLabel;

  public GameLobby() {

    TBIALSession session = getSession();
    String hostName = session.getCurrentGame().getHost();
    Model<String> isHostModel =
        session.getUser().getName().equals(hostName)
            ? new Model<String>("You are the host of this game.")
            : new Model<String>(hostName + " is the host of this game.");
    isHostLabel = new Label("isHostLabel", isHostModel);

    startGameLink =
        new Link<Void>("startGameLink") {
          private static final long serialVersionUID = 1L;

          @Override
          public void onClick() {
            startGame();
          }
        };
    startGameLink.setOutputMarkupId(true);
    startGameLink.setVisible(session.getUser().getName().equals(hostName));

    add(isHostLabel);
    add(startGameLink);
  }

  /**
   * Checks whether the user is allowed to start a new game and does so if he is allowed. Redirects
   * the user to the game table.
   */
  private void startGame() {
    TBIALSession session = getSession();
    Game game = session.getCurrentGame();
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
    //    setResponsePage(getTbialApplication().getGameTablePage()); TODO: Open actual game table.
  }
}
