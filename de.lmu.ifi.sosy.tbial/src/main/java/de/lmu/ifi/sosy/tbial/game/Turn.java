package de.lmu.ifi.sosy.tbial.game;
import de.lmu.ifi.sosy.tbial.game.RoleCard.Role;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Saves information about the stage of the turn and the player whose turn it is. */
public class Turn implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LogManager.getLogger(Game.class);

  public static final int DRAW_LIMIT_IN_DRAWING_STAGE = 2;
  public static final int MAX_BUG_CARDS_PER_TURN = 1;

  private List<Player> players;
  private int currentPlayerIndex;
  private int nextPlayerIndex;
  private TurnStage stage;
  private int bugsPlayedThisTurn;

  private Player lastPlayedBugCardBy;
  private ActionCard lastPlayedBugCard;
  private Player attackedPlayer;

  public Turn(List<Player> players) {
    this.players = players;
    this.currentPlayerIndex = 0;
    this.stage = TurnStage.DRAWING_CARDS;
    this.bugsPlayedThisTurn = 0;
  }

  /**
   * Gets the player whose turn it is.
   *
   * @return The player whose turn it is.
   */
  public Player getCurrentPlayer() {
    return players.get(currentPlayerIndex);
  }

  /**
   * Gets the index of the current player.
   *
   * @return The player whose turn it is.
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /** Determines the index of the player with the manager role. */
  public void determineStartingPlayer() {
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getRole() == Role.MANAGER) {
        currentPlayerIndex = i;

        LOGGER.info(players.get(i).getUserName() + " (Manager) starts.");
        return;
      }
    }
  }

  /** Switches the turn to the next player who is not fired yet. */
  public void switchToNextPlayer() {
    stage = TurnStage.DRAWING_CARDS;
    this.bugsPlayedThisTurn = 0;
    currentPlayerIndex++;
    if (currentPlayerIndex == players.size()) {
      currentPlayerIndex = 0;
    }
    while (players.get(currentPlayerIndex).isFired()) {
      switchToNextPlayer();
    }
  }

  /** Gets the next player who is not fired yet. */
  public Player getNextPlayer(int index) {
    nextPlayerIndex = index;
    nextPlayerIndex++;
    if (nextPlayerIndex == players.size()) {
      nextPlayerIndex = 0;
    }
    while (players.get(nextPlayerIndex).isFired()) {
      getNextPlayer(nextPlayerIndex);
    }
    return players.get(nextPlayerIndex);
  }

  /**
   * Call when the player is moving on to the next stage of a turn.
   *
   * @param stage The stage the player moves on to.
   */
  public void setStage(TurnStage stage) {
    this.stage = stage;
  }

  /**
   * Returns the stage of the turn the player is currently in
   *
   * @return the stage
   */
  public TurnStage getStage() {
    return this.stage;
  }

  /** An enum containing the different stages of a turn. */
  public enum TurnStage {
    DRAWING_CARDS,
    PLAYING_CARDS,
    DISCARDING_CARDS,
    WAITING_FOR_PLAYER_RESPONSE,
    CHOOSING_CARD_TO_BLOCK_WITH
  }

  /** Increases the counter for already played bug cars in this turn by 1. */
  public void incrementPlayedBugCardsThisTurn() {
    this.bugsPlayedThisTurn++;

    LOGGER.info(players.get(currentPlayerIndex).getUserName() + " has played a bug card");
  }

  /**
   * Returns the number of played bug cards this turn.
   *
   * @return the number of played bug cards this turn.
   */
  public int getPlayedBugCardsInThisTurn() {
    return this.bugsPlayedThisTurn;
  }
  /**
   * For testing only.
   *
   * @param basePlayer The player to be set as the current player
   */
  public void setTurnPlayerUseForTestingOnly(Player basePlayer) {
    currentPlayerIndex = players.indexOf(basePlayer);
  }

  public Player getLastPlayedBugCardBy() {
	  return lastPlayedBugCardBy;
  }
  
  public void setLastPlayedBugCardBy(Player player)
  {
	  lastPlayedBugCardBy = player;
  }
  
  public ActionCard getLastPlayedBugCard() {
    return lastPlayedBugCard;
  }

  public void setLastPlayedBugCard(ActionCard bugCard) {
    lastPlayedBugCard = bugCard;
  }

  public Player getAttackedPlayer() {
    return attackedPlayer;
  }

  public void setAttackedPlayer(Player player) {
    attackedPlayer = player;
  }
}
