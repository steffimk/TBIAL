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

  private List<Player> players;
  private int currentPlayerIndex;
  private TurnStage stage;
  private int drawnCardsInDrawingStage;

  public Turn(List<Player> players) {
    this.players = players;
    this.currentPlayerIndex = 0;
    this.stage = TurnStage.DRAWING_CARDS;
    this.drawnCardsInDrawingStage = 0;
  }

  /**
   * Gets the player whose turn it is.
   *
   * @return The player whose turn it is.
   */
  public Player getCurrentPlayer() {
    return players.get(currentPlayerIndex);
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
    this.drawnCardsInDrawingStage = 0;
    currentPlayerIndex++;
    if (currentPlayerIndex == players.size()) {
      currentPlayerIndex = 0;
    }
    while (players.get(currentPlayerIndex).isFired()) {
      switchToNextPlayer();
    }
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
    DISCARDING_CARDS
  }

  /*
   * Increases the counter for already drawn cards in current draw stage by 1.
   */
  public void incrementDrawnCardsInDrawingStage() {
    this.drawnCardsInDrawingStage++;

    LOGGER.info(
        players.get(currentPlayerIndex).getUserName()
            + " has drawn a card in "
            + TurnStage.DRAWING_CARDS.toString());
  }

  /**
   * Returns the number of cards the current player has already drawn in the drawing stage.
   *
   * @return the number of drawn cards in the current drawing stage
   */
  public int getDrawnCardsInDrawingStage() {
    return this.drawnCardsInDrawingStage;
  }

  /**
   * For testing only.
   *
   * @param basePlayer The player to be set as the current player
   */
  public void setTurnPlayerUseForTestingOnly(Player basePlayer) {
    currentPlayerIndex = players.indexOf(basePlayer);
  }
}
