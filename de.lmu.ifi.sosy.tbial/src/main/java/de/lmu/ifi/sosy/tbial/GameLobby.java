package de.lmu.ifi.sosy.tbial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;

import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.game.Game;

/**
 * The Lobby of a specific game in which the players wait for the game to start.
 */
@AuthenticationRequired
public class GameLobby extends BasePage {

  private static final long serialVersionUID = 1L;
  
  private static final Logger LOGGER = LogManager.getLogger(GameLobby.class);

  private final Button startGameButton;

  public GameLobby() {
    startGameButton =
        new Button("startGameButton") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1;

          @Override
          public void onSubmit() {
            startGame();
          }
        };

    Form<?> startGameForm = new Form<>("startGameForm");
    startGameForm.add(startGameButton);
    
    add(startGameForm);
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
