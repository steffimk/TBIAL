package de.lmu.ifi.sosy.tbial;

import java.util.Map;

import org.apache.wicket.markup.html.form.Form;

import de.lmu.ifi.sosy.tbial.game.Game;
import de.lmu.ifi.sosy.tbial.game.Player;

@AuthenticationRequired
public class GameLobby extends BasePage {

  private static final long serialVersionUID = 1L;

  public GameLobby() {
    Form LeaveForm =
        new Form("LeaveForm") {

          private static final long serialVersionUID = 1L;

          protected void onSubmit() {
            leaveCurrentGame();
            setResponsePage(getTbialApplication().getHomePage());
          }
        };
    add(LeaveForm);
  }

  //Methods for leaving game: actual leave method, Host Check/Change, and checking if player is the last one to leave
  public void leaveCurrentGame() {

    Map<String, Game> currentGames = getGameManager().getCurrentGames();
    String currentGameName = getSession().getCurrentGame().getName();
    if (!CheckIfLastPlayer()) {
      checkHostChange();
    }
    getSession().setCurrentGameNull();
    currentGames.remove(currentGameName);
  }

  public void checkHostChange() {
    String currentHost = getSession().getCurrentGame().getHost();
    String currentPlayer = getSession().getUser().getName();
    Game currentGame = getSession().getCurrentGame();
    Map<String, Player> inGamePlayers = getSession().getCurrentGame().getInGamePlayers();

    if (currentHost == currentPlayer) {
      if (inGamePlayers.get(0).getUserName() != currentHost)
        currentGame.setHost(inGamePlayers.get(0).getUserName());
    } else {
      currentGame.setHost(inGamePlayers.get(1).getUserName());
    }
  }

  public boolean CheckIfLastPlayer() {
    int currentPlayersInGame = getSession().getCurrentGame().getCurrentNumberOfPlayers();
    if (currentPlayersInGame == 1) {
      return true;
    }
    return false;
  }
}
